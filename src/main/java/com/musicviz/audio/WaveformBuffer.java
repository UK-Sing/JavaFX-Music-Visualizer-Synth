package com.musicviz.audio;

import java.util.concurrent.locks.ReentrantLock;

// Thread-safe ring buffer. The audio capture thread writes samples here.
// The JavaFX AnimationTimer reads samples from here every frame.
public class WaveformBuffer {

    private final double[] data;
    private int writePos = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public WaveformBuffer(int capacity) {
        data = new double[capacity];
    }

    public void write(double[] samples) {
        lock.lock();
        try {
            for (double s : samples) {
                data[writePos % data.length] = s;
                writePos++;
            }
        } finally {
            lock.unlock();
        }
    }

    public double[] read(int count) {
        lock.lock();
        try {
            double[] out = new double[count];
            int start = writePos - count;
            for (int i = 0; i < count; i++) {
                int idx = (start + i + data.length * 2) % data.length;
                out[i] = data[idx];
            }
            return out;
        } finally {
            lock.unlock();
        }
    }
}
