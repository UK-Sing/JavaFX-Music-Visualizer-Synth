package com.musicviz.audio;

import javax.sound.sampled.*;

// Captures raw PCM audio from the default microphone.
// Runs in a background daemon thread.
// Converts raw bytes to doubles in range [-1.0, 1.0] and writes to WaveformBuffer.
public class AudioCapture implements Runnable {

    public static final int SAMPLE_RATE = 48000;
    public static final int BUFFER_SIZE = 2048;

    private final WaveformBuffer buffer;
    private volatile boolean running = false;
    private TargetDataLine line;

    public AudioCapture(WaveformBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            AudioFormat fmt = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            line = openCaptureLine(fmt);
            if (line == null) {
                System.err.println("AudioCapture: no capture line found, visualizer will be silent.");
                return;
            }
            line.start();
            running = true;
            System.out.println("AudioCapture: started on " + line);
            byte[] raw = new byte[BUFFER_SIZE * 2];
            while (running) {
                int read = line.read(raw, 0, raw.length);
                if (read > 0) buffer.write(toDoubles(raw, read));
            }
        } catch (Exception e) {
            System.err.println("AudioCapture error: " + e);
            e.printStackTrace();
        }
    }

    private TargetDataLine openCaptureLine(AudioFormat fmt) {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, fmt);
        // Try default line first
        try {
            TargetDataLine l = (TargetDataLine) AudioSystem.getLine(info);
            l.open(fmt);
            System.out.println("AudioCapture: opened default line");
            return l;
        } catch (Exception e) {
            System.err.println("AudioCapture: default line failed (" + e.getMessage() + "), trying mixers...");
        }
        // Fall back: scan mixers for first one supporting TargetDataLine
        for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
            try {
                Mixer mixer = AudioSystem.getMixer(mi);
                if (mixer.getTargetLineInfo().length == 0) continue;
                TargetDataLine l = (TargetDataLine) mixer.getLine(info);
                l.open(fmt);
                System.out.println("AudioCapture: opened via mixer: " + mi.getName());
                return l;
            } catch (Exception ex) {
                // try next mixer
            }
        }
        return null;
    }

    private double[] toDoubles(byte[] raw, int len) {
        double[] out = new double[len / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = (raw[2 * i + 1] << 8 | (raw[2 * i] & 0xFF)) / 32768.0;
        }
        return out;
    }

    public void stop() {
        running = false;
        if (line != null) {
            line.stop();
            line.close();
        }
    }
}
