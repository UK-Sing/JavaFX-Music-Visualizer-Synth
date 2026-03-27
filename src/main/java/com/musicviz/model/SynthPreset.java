package com.musicviz.model;

// Plain Java object representing a saved synthesizer preset.
// Persisted to/from JSON via JsonUtil and Gson.
public class SynthPreset {

    private String name;
    private String waveType;
    private double attackMs;
    private double decayMs;
    private double sustainLevel;
    private double releaseMs;

    public SynthPreset() {}

    public SynthPreset(String name, String waveType,
                       double attackMs, double decayMs,
                       double sustainLevel, double releaseMs) {
        this.name = name;
        this.waveType = waveType;
        this.attackMs = attackMs;
        this.decayMs = decayMs;
        this.sustainLevel = sustainLevel;
        this.releaseMs = releaseMs;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getWaveType() { return waveType; }
    public void setWaveType(String waveType) { this.waveType = waveType; }

    public double getAttackMs() { return attackMs; }
    public void setAttackMs(double attackMs) { this.attackMs = attackMs; }

    public double getDecayMs() { return decayMs; }
    public void setDecayMs(double decayMs) { this.decayMs = decayMs; }

    public double getSustainLevel() { return sustainLevel; }
    public void setSustainLevel(double sustainLevel) { this.sustainLevel = sustainLevel; }

    public double getReleaseMs() { return releaseMs; }
    public void setReleaseMs(double releaseMs) { this.releaseMs = releaseMs; }

    @Override
    public String toString() { return name; }
}
