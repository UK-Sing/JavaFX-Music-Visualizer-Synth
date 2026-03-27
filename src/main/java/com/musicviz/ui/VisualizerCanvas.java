package com.musicviz.ui;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.*;
import com.musicviz.audio.*;
import java.util.LinkedList;

// Core rendering class. Runs at 60fps using AnimationTimer.
// Each frame: reads samples from WaveformBuffer, runs FFT, detects beats,
// draws the selected visualization mode, updates particles.
public class VisualizerCanvas {

    public enum Mode { BARS, OSCILLOSCOPE, CIRCULAR, SPECTROGRAM }

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Pane wrapper;
    private final WaveformBuffer waveBuffer;
    private final FFTProcessor fft = new FFTProcessor();
    private final BeatDetector beatDetector = new BeatDetector();

    private Mode mode = Mode.BARS;
    private double[] smoothedBars;
    private volatile double sensitivity = 5.0;

    private static final int SPEC_HISTORY = 300;
    private final LinkedList<double[]> specHistory = new LinkedList<>();

    public VisualizerCanvas(WaveformBuffer buf) {
        this.waveBuffer = buf;
        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        wrapper = new Pane(canvas);
        canvas.widthProperty().bind(wrapper.widthProperty());
        canvas.heightProperty().bind(wrapper.heightProperty());
    }

    public void start() {
        new AnimationTimer() {
            public void handle(long now) {
                double[] samples = waveBuffer.read(2048);
                double[] mags = fft.process(samples);
                render(samples, mags);
            }
        }.start();
    }

    private void render(double[] samples, double[] mags) {
        // Dark transparent fill creates motion blur trail effect
        gc.setFill(Color.rgb(10, 10, 20, 0.85));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        switch (mode) {
            case BARS         -> drawBars(mags);
            case OSCILLOSCOPE -> drawOscilloscope(samples);
            case CIRCULAR     -> drawCircular(mags);
            case SPECTROGRAM  -> drawSpectrogram(mags);
        }
    }

    // Draws 64 frequency bars. Color shifts from blue (bass) to red (treble).
    // Bars use exponential smoothing so they don't jump harshly between frames.
    private void drawBars(double[] mags) {
        int bars = 64;
        if (smoothedBars == null) smoothedBars = new double[bars];
        double w = canvas.getWidth() / bars;
        double h = canvas.getHeight();
        for (int i = 0; i < bars; i++) {
            int bin = (int) (i * (mags.length / 2.0) / bars);
            double target = Math.min(1.0, mags[bin] * sensitivity);
            smoothedBars[i] = smoothedBars[i] * 0.8 + target * 0.2;
            double barH = smoothedBars[i] * h * 0.85;
            double hue = 240 - (i / (double) bars) * 240;
            gc.setFill(Color.hsb(hue, 1.0, 1.0, 0.85));
            gc.fillRoundRect(i * w + 1, h - barH, w - 2, barH, 4, 4);
        }
    }

    // Draws the raw PCM waveform as a glowing cyan line.
    private void drawOscilloscope(double[] samples) {
        double w = canvas.getWidth(), h = canvas.getHeight();
        gc.setStroke(Color.rgb(0, 255, 180, 0.9));
        gc.setLineWidth(2.0);
        gc.beginPath();
        for (int i = 0; i < samples.length; i++) {
            double x = i / (double) samples.length * w;
            double y = h / 2 + samples[i] * h * 0.4 * (sensitivity / 5.0);
            if (i == 0) gc.moveTo(x, y);
            else gc.lineTo(x, y);
        }
        gc.stroke();
    }

    // Draws frequency bars arranged in a full circle.
    private void drawCircular(double[] mags) {
        double cx = canvas.getWidth() / 2;
        double cy = canvas.getHeight() / 2;
        double baseR = 100;
        int bars = 128;
        for (int i = 0; i < bars; i++) {
            int bin = (int) (i * (mags.length / 2.0) / bars);
            double mag = Math.min(1.0, mags[bin] * sensitivity);
            double angle = 2 * Math.PI * i / bars;
            double r1 = baseR;
            double r2 = baseR + mag * 150;
            double hue = (i / (double) bars) * 360;
            gc.setStroke(Color.hsb(hue, 1.0, 1.0, 0.8));
            gc.setLineWidth(2);
            gc.strokeLine(
                cx + r1 * Math.cos(angle), cy + r1 * Math.sin(angle),
                cx + r2 * Math.cos(angle), cy + r2 * Math.sin(angle)
            );
        }
    }

    // Scrolling heatmap. Each column is one FFT frame. Color = frequency intensity.
    private void drawSpectrogram(double[] mags) {
        int bins = 128;
        double[] row = new double[bins];
        for (int i = 0; i < bins; i++) {
            int bin = (int) (i * (mags.length / 2.0) / bins);
            row[i] = Math.min(1.0, mags[bin] * sensitivity * 0.8);
        }
        specHistory.addLast(row);
        if (specHistory.size() > SPEC_HISTORY) specHistory.removeFirst();

        double w = canvas.getWidth(), h = canvas.getHeight();
        double colW = w / SPEC_HISTORY;
        double rowH = h / bins;
        int xi = 0;
        for (double[] r : specHistory) {
            for (int yi = 0; yi < bins; yi++) {
                double v = r[yi];
                gc.setFill(Color.hsb(240 - v * 240, 1, v));
                gc.fillRect(xi * colW, h - (yi + 1) * rowH, colW + 1, rowH + 1);
            }
            xi++;
        }
    }

    public void setMode(Mode m) { this.mode = m; }
    public void setSensitivity(double s) { this.sensitivity = s; }
    public Pane getNode() { return wrapper; }
}
