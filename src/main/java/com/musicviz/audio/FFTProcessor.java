package com.musicviz.audio;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.*;

// Applies a Hanning window to reduce spectral leakage then runs FFT.
// Input: raw PCM samples (length must be a power of 2, e.g. 2048).
// Output: magnitude array of length N/2 representing frequency bins.
// Bin i corresponds to frequency: i * (sampleRate / N)
public class FFTProcessor {

    private final FastFourierTransformer fft =
        new FastFourierTransformer(DftNormalization.STANDARD);

    public double[] process(double[] samples) {
        double[] windowed = applyHanning(samples);
        Complex[] result = fft.transform(windowed, TransformType.FORWARD);
        int half = result.length / 2;
        double[] magnitudes = new double[half];
        for (int i = 0; i < half; i++) {
            magnitudes[i] = result[i].abs();
        }
        return magnitudes;
    }

    private double[] applyHanning(double[] in) {
        int N = in.length;
        double[] out = new double[N];
        for (int i = 0; i < N; i++) {
            double w = 0.5 * (1 - Math.cos(2 * Math.PI * i / (N - 1)));
            out[i] = in[i] * w;
        }
        return out;
    }
}
