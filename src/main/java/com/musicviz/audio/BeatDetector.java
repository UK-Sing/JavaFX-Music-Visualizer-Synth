package com.musicviz.audio;

import java.util.LinkedList;
import java.util.Queue;

// Energy-based beat detector.
// Sums squared magnitudes in bass bins (2-10, roughly 60-200 Hz).
// A beat fires when instantaneous energy exceeds THRESHOLD * average of
// the last HISTORY frames, with a minimum cooldown of 200ms between beats.
public class BeatDetector {

    private static final int HISTORY = 43;
    private static final double THRESHOLD = 1.4;
    private static final int BASS_LOW = 2;
    private static final int BASS_HIGH = 10;

    private final Queue<Double> history = new LinkedList<>();
    private long lastBeatMs = 0;

    public boolean detect(double[] magnitudes) {
        double energy = 0;
        for (int i = BASS_LOW; i <= BASS_HIGH; i++) {
            energy += magnitudes[i] * magnitudes[i];
        }

        history.offer(energy);
        if (history.size() > HISTORY) history.poll();

        double avg = history.stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(1.0);

        long now = System.currentTimeMillis();
        if (energy > THRESHOLD * avg && now - lastBeatMs > 200) {
            lastBeatMs = now;
            return true;
        }
        return false;
    }
}
