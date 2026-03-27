package com.musicviz.ui;

import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import com.musicviz.audio.SynthEngine;

// Renders a 2-octave interactive piano keyboard (C4 to B5, 24 keys).
// Mouse press triggers noteOn, mouse release triggers noteOff on SynthEngine.
// Hit detection checks black keys before white keys because black keys overlap.
public class PianoKeyboard {

    // Equal temperament frequencies starting at C4 = 261.63 Hz
    // Each semitone = previous * 2^(1/12)
    private static final double[] FREQS = {
        261.63, 277.18, 293.66, 311.13, 329.63, 349.23,
        369.99, 392.00, 415.30, 440.00, 466.16, 493.88,
        523.25, 554.37, 587.33, 622.25, 659.25, 698.46,
        739.99, 783.99, 830.61, 880.00, 932.33, 987.77
    };

    // true = black key, false = white key (pattern repeats every octave)
    private static final boolean[] IS_BLACK = {
        false, true,  false, true,  false,
        false, true,  false, true,  false, true, false,
        false, true,  false, true,  false,
        false, true,  false, true,  false, true, false
    };

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final SynthEngine synth;
    private final boolean[] pressed = new boolean[24];

    public PianoKeyboard(SynthEngine synth) {
        this.synth = synth;
        canvas = new Canvas(700, 140);
        gc = canvas.getGraphicsContext2D();
        draw();
        canvas.setOnMousePressed(e  -> handleClick(e.getX(), e.getY(), true));
        canvas.setOnMouseReleased(e -> handleClick(e.getX(), e.getY(), false));
    }

    private void handleClick(double mx, double my, boolean on) {
        int key = getKeyAt(mx, my);
        if (key < 0 || key >= 24) return;
        pressed[key] = on;
        if (on) synth.noteOn(FREQS[key]);
        else    synth.noteOff(FREQS[key]);
        draw();
    }

    private int getKeyAt(double x, double y) {
        int whites = 14;
        double ww = canvas.getWidth() / whites;

        // Check black keys first — they visually overlap white keys
        if (y < canvas.getHeight() * 0.6) {
            for (int i = 0; i < 24; i++) {
                if (!IS_BLACK[i]) continue;
                double bx = blackKeyX(i, ww);
                if (x >= bx && x <= bx + ww * 0.6) return i;
            }
        }

        // Then check white keys
        int wi = (int) (x / ww);
        int count = 0;
        for (int i = 0; i < 24; i++) {
            if (!IS_BLACK[i]) {
                if (count == wi) return i;
                count++;
            }
        }
        return -1;
    }

    // Returns the x pixel position of a black key
    private double blackKeyX(int keyIdx, double ww) {
        int wc = 0;
        for (int i = 0; i < keyIdx; i++) {
            if (!IS_BLACK[i]) wc++;
        }
        return wc * ww - ww * 0.3;
    }

    private void draw() {
        double w = canvas.getWidth(), h = canvas.getHeight();
        int whites = 14;
        double ww = w / whites;

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, w, h);

        // Draw white keys first
        int wi = 0;
        for (int i = 0; i < 24; i++) {
            if (!IS_BLACK[i]) {
                gc.setFill(pressed[i] ? Color.DEEPSKYBLUE : Color.WHITE);
                gc.fillRoundRect(wi * ww + 1, 0, ww - 2, h - 2, 4, 4);
                wi++;
            }
        }

        // Draw black keys on top
        for (int i = 0; i < 24; i++) {
            if (IS_BLACK[i]) {
                double bx = blackKeyX(i, ww);
                gc.setFill(pressed[i] ? Color.DODGERBLUE : Color.BLACK);
                gc.fillRect(bx, 0, ww * 0.6, h * 0.6);
            }
        }
    }

    public Canvas getCanvas() { return canvas; }
}
