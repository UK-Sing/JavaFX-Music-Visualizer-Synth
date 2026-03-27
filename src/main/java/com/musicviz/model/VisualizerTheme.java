package com.musicviz.model;

// Defines color and style parameters for a visualizer theme.
// Can be extended to allow user-selectable themes.
public class VisualizerTheme {

    private final String name;
    private final String backgroundHex;
    private final double barSaturation;
    private final double barBrightness;

    public static final VisualizerTheme DEFAULT =
        new VisualizerTheme("Default", "#0A0A14", 1.0, 1.0);

    public VisualizerTheme(String name, String backgroundHex,
                           double barSaturation, double barBrightness) {
        this.name = name;
        this.backgroundHex = backgroundHex;
        this.barSaturation = barSaturation;
        this.barBrightness = barBrightness;
    }

    public String getName() { return name; }
    public String getBackgroundHex() { return backgroundHex; }
    public double getBarSaturation() { return barSaturation; }
    public double getBarBrightness() { return barBrightness; }

    @Override
    public String toString() { return name; }
}
