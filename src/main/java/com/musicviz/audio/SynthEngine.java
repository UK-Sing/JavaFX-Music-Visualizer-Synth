package com.musicviz.audio;

import javax.sound.sampled.*;
import java.util.*;

// Polyphonic synthesizer engine.
// Generates audio samples for multiple simultaneous notes (voices).
// Supports SINE, SQUARE, SAW, TRIANGLE waveforms with ADSR shaping.
// Writes mixed output to a SourceDataLine at 44100 Hz 16-bit mono.
public class SynthEngine {

    public enum WaveType { SINE, SQUARE, SAW, TRIANGLE }

    private static class Voice {
        double freq;
        WaveType wave;
        ADSREnvelope env;
        double phase = 0;

        Voice(double freq, WaveType wave, ADSREnvelope env) {
            this.freq = freq;
            this.wave = wave;
            this.env = env;
        }
    }

    private final List<Voice> voices = Collections.synchronizedList(new ArrayList<>());
    private SourceDataLine out;
    private final int sampleRate = 48000;
    private WaveType waveType = WaveType.SINE;
    private volatile boolean running = true;
    private WaveformBuffer vizBuffer;

    public SynthEngine() {
        try {
            AudioFormat fmt = new AudioFormat(sampleRate, 16, 1, true, false);
            out = AudioSystem.getSourceDataLine(fmt);
            out.open(fmt, 16384);
            out.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread renderThread = new Thread(() -> {
            while (running) {
                render(512);
            }
        });
        renderThread.setDaemon(true);
        renderThread.setPriority(Thread.MAX_PRIORITY);
        renderThread.start();
    }

    public void noteOn(double freq) {
        ADSREnvelope env = new ADSREnvelope(10, 80, 0.7, 300);
        env.noteOn();
        voices.add(new Voice(freq, waveType, env));
    }

    public void noteOff(double freq) {
        voices.stream()
            .filter(v -> Math.abs(v.freq - freq) < 0.01 && !v.env.isDone())
            .forEach(v -> v.env.noteOff());
    }

    // Called from the render thread 512 samples at a time.
    public void render(int numSamples) {
        byte[] buf = new byte[numSamples * 2];
        double[] doubleSamples = new double[numSamples];

        // Snapshot and prune voices once per chunk — not per sample
        Voice[] snapshot;
        synchronized (voices) {
            voices.removeIf(v -> v.env.isDone());
            snapshot = voices.toArray(new Voice[0]);
        }

        for (int i = 0; i < numSamples; i++) {
            double mixed = 0;
            for (Voice v : snapshot) {
                v.env.updatePhase();
                mixed += sample(v) * v.env.getLevel();
            }
            if (snapshot.length > 0) mixed /= snapshot.length;
            mixed = Math.max(-1, Math.min(1, mixed));
            doubleSamples[i] = mixed;
            short val = (short) (mixed * 32767);
            buf[2 * i]     = (byte) (val & 0xFF);
            buf[2 * i + 1] = (byte) (val >> 8);
        }
        if (out != null) out.write(buf, 0, buf.length);
        // Feed visualizer only when piano is actively playing
        if (vizBuffer != null && snapshot.length > 0) {
            vizBuffer.write(doubleSamples);
        }
    }

    private double sample(Voice v) {
        v.phase += 2 * Math.PI * v.freq / sampleRate;
        if (v.phase > 2 * Math.PI) v.phase -= 2 * Math.PI;
        return switch (v.wave) {
            case SINE     -> Math.sin(v.phase);
            case SQUARE   -> Math.sin(v.phase) >= 0 ? 1.0 : -1.0;
            case SAW      -> (v.phase / Math.PI) - 1.0;
            case TRIANGLE -> 2.0 / Math.PI * Math.asin(Math.sin(v.phase));
        };
    }

    public void setWaveType(WaveType w) { this.waveType = w; }
    public void setVizBuffer(WaveformBuffer buf) { this.vizBuffer = buf; }

    public void shutdown() {
        running = false;
        if (out != null) {
            out.drain();
            out.close();
        }
    }
}
