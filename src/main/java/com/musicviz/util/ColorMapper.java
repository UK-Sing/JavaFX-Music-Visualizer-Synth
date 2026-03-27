package com.musicviz.util;

import javafx.scene.paint.Color;

// Maps a normalized value [0.0, 1.0] to a Color using various schemes.
// Used by visualizer modes to assign colors to frequency bins.
public class ColorMapper {

    // Maps 0.0 (blue) → 1.0 (red) along the HSB hue spectrum.
    public static Color heatMap(double value) {
        double hue = 240 - value * 240;
        return Color.hsb(hue, 1.0, 1.0);
    }

    // Maps position along the rainbow: 0.0=red, 0.5=green, 1.0=violet.
    public static Color rainbow(double position) {
        return Color.hsb(position * 360, 1.0, 1.0);
    }

    // Maps bass→treble with custom saturation and brightness.
    public static Color spectrum(double position, double saturation, double brightness) {
        double hue = 240 - position * 240;
        return Color.hsb(hue, saturation, brightness);
    }
}
