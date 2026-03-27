package com.musicviz.audio;

// Shapes note amplitude over time through 4 phases:
// ATTACK  — amplitude rises from 0 to 1 over attackMs milliseconds
// DECAY   — amplitude falls from 1 to sustainLevel over decayMs milliseconds
// SUSTAIN — amplitude holds at sustainLevel while key is held
// RELEASE — amplitude falls from current level to 0 over releaseMs milliseconds
public class ADSREnvelope {

    public enum Phase { ATTACK, DECAY, SUSTAIN, RELEASE, DONE }

    private final double attackMs;
    private final double decayMs;
    private final double sustainLevel;
    private final double releaseMs;

    private Phase phase = Phase.DONE;
    private long startTime;
    private long releaseStart;
    private double releaseStartLevel;

    public ADSREnvelope(double attackMs, double decayMs,
                        double sustainLevel, double releaseMs) {
        this.attackMs = attackMs;
        this.decayMs = decayMs;
        this.sustainLevel = sustainLevel;
        this.releaseMs = releaseMs;
    }

    public void noteOn() {
        phase = Phase.ATTACK;
        startTime = System.currentTimeMillis();
    }

    public void noteOff() {
        releaseStartLevel = getLevel();
        phase = Phase.RELEASE;
        releaseStart = System.currentTimeMillis();
    }

    public double getLevel() {
        long now = System.currentTimeMillis();
        long t = now - startTime;
        return switch (phase) {
            case ATTACK  -> Math.min(1.0, t / attackMs);
            case DECAY   -> 1.0 - (1.0 - sustainLevel)
                              * Math.min(1.0, (t - attackMs) / decayMs);
            case SUSTAIN -> sustainLevel;
            case RELEASE -> releaseStartLevel
                              * Math.max(0, 1.0 - (now - releaseStart) / releaseMs);
            default      -> 0.0;
        };
    }

    public void updatePhase() {
        long t = System.currentTimeMillis() - startTime;
        if (phase == Phase.ATTACK && t >= attackMs) phase = Phase.DECAY;
        else if (phase == Phase.DECAY && t >= attackMs + decayMs) phase = Phase.SUSTAIN;
        else if (phase == Phase.RELEASE && getLevel() <= 0.001) phase = Phase.DONE;
    }

    public boolean isDone() { return phase == Phase.DONE; }
}
