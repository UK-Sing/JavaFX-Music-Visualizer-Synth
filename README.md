# JavaFX Music Visualizer & Synthesizer

A real-time music visualizer and polyphonic synthesizer desktop application built with Java 21 and JavaFX 21.

![Java](https://img.shields.io/badge/Java-21-orange) ![JavaFX](https://img.shields.io/badge/JavaFX-21-blue) ![Maven](https://img.shields.io/badge/Build-Maven-red)

## Features

- **4 Visualization Modes**
  - **Bars** — 64-band frequency spectrum with color gradient (blue → red)
  - **Oscilloscope** — Raw PCM waveform display
  - **Circular** — 128-band radial frequency ring
  - **Spectrogram** — Scrolling frequency heatmap over time

- **Polyphonic Synthesizer**
  - Interactive on-screen piano keyboard (click to play)
  - 4 waveforms: Sine, Square, Sawtooth, Triangle
  - ADSR envelope shaping per note

- **Real-time Microphone Capture**
  - Visualizer responds to microphone input when no piano keys are pressed
  - Automatically switches to piano audio when keys are held

- **Adjustable Sensitivity** — slider to scale visualizer response

## Requirements

- Java 21 (JDK)
- Maven 3.6+
- PipeWire or ALSA audio (Linux)

## Building & Running

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk mvn javafx:run
```

Or compile first:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk mvn clean compile
JAVA_HOME=/usr/lib/jvm/java-21-openjdk mvn javafx:run
```

## Project Structure

```
src/main/java/com/musicviz/
├── audio/
│   ├── WaveformBuffer.java     # Thread-safe ring buffer for audio samples
│   ├── AudioCapture.java       # Microphone capture thread
│   ├── FFTProcessor.java       # Hanning-windowed FFT (Apache Commons Math)
│   ├── BeatDetector.java       # Energy-based beat detection
│   ├── ADSREnvelope.java       # Attack/Decay/Sustain/Release envelope
│   ├── SynthEngine.java        # Polyphonic synthesizer engine
│   └── AudioFilePlayer.java    # MP3 playback stub (JLayer)
├── ui/
│   ├── MainApp.java            # JavaFX Application entry point
│   ├── MainLayout.java         # Root layout and audio pipeline wiring
│   ├── VisualizerCanvas.java   # Canvas rendering at 60fps (AnimationTimer)
│   ├── PianoKeyboard.java      # Interactive piano keyboard UI
│   └── ControlPanel.java       # Control panel placeholder
├── model/
│   ├── AudioData.java          # Audio analysis snapshot
│   ├── SynthPreset.java        # Synthesizer preset data
│   └── VisualizerTheme.java    # Visualizer theme parameters
└── util/
    ├── JsonUtil.java           # JSON preset persistence (Gson)
    └── ColorMapper.java        # Audio-to-color mapping utilities
```

## Dependencies

| Library | Version | Purpose |
|---|---|---|
| JavaFX | 21.0.1 | UI & Canvas rendering |
| Apache Commons Math | 3.6.1 | FFT processing |
| Gson | 2.10.1 | Preset JSON serialization |
| JLayer | 1.0.1 | MP3 playback |

## Linux Audio Notes

The app uses **48000 Hz** sample rate to match PipeWire's native rate and avoid resampling artifacts. Ensure your microphone capture is enabled:

```bash
amixer -c 1 sset Capture cap
sudo alsactl store
```
