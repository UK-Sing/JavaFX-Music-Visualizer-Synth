package com.musicviz.model;

// Snapshot of audio analysis data for a single frame.
// Passed between the audio pipeline and the visualizer.
public class AudioData {

    private final double[] samples;
    private final double[] magnitudes;
    private final boolean beat;

    public AudioData(double[] samples, double[] magnitudes, boolean beat) {
        this.samples = samples;
        this.magnitudes = magnitudes;
        this.beat = beat;
    }

    public double[] getSamples() { return samples; }
    public double[] getMagnitudes() { return magnitudes; }
    public boolean isBeat() { return beat; }
}
