package com.musicviz.ui;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import com.musicviz.audio.*;

// Root layout. Assembles all UI components and wires audio pipeline together.
// BorderPane: top = control bar, center = visualizer canvas, bottom = piano.
// Starts the audio capture thread and visualizer animation loop.
public class MainLayout {

    private final BorderPane root = new BorderPane();
    private final WaveformBuffer buffer = new WaveformBuffer(8192);
    private final SynthEngine synth = new SynthEngine();
    private final AudioCapture capture = new AudioCapture(buffer);
    private final VisualizerCanvas viz = new VisualizerCanvas(buffer);
    private final PianoKeyboard piano = new PianoKeyboard(synth);
    private Thread captureThread;

    public MainLayout() {
        root.setCenter(viz.getNode());
        root.setBottom(piano.getCanvas());
        root.setTop(buildControls());
        root.setStyle("-fx-background-color: #0A0A14;");

        synth.setVizBuffer(buffer);

        // Start audio capture in background daemon thread
        captureThread = new Thread(capture);
        captureThread.setDaemon(true);
        captureThread.start();

        // Start 60fps render loop
        viz.start();
    }

    private HBox buildControls() {
        // Visualization mode selector
        ComboBox<String> modeBox = new ComboBox<>();
        modeBox.getItems().addAll("Bars", "Oscilloscope", "Circular", "Spectrogram");
        modeBox.setValue("Bars");
        modeBox.setOnAction(e -> viz.setMode(switch (modeBox.getValue()) {
            case "Oscilloscope" -> VisualizerCanvas.Mode.OSCILLOSCOPE;
            case "Circular"     -> VisualizerCanvas.Mode.CIRCULAR;
            case "Spectrogram"  -> VisualizerCanvas.Mode.SPECTROGRAM;
            default             -> VisualizerCanvas.Mode.BARS;
        }));

        // Waveform type selector for synthesizer
        ComboBox<String> waveBox = new ComboBox<>();
        waveBox.getItems().addAll("Sine", "Square", "Sawtooth", "Triangle");
        waveBox.setValue("Sine");
        waveBox.setOnAction(e -> synth.setWaveType(switch (waveBox.getValue()) {
            case "Square"   -> SynthEngine.WaveType.SQUARE;
            case "Sawtooth" -> SynthEngine.WaveType.SAW;
            case "Triangle" -> SynthEngine.WaveType.TRIANGLE;
            default         -> SynthEngine.WaveType.SINE;
        }));

        Slider sensitivitySlider = new Slider(0.5, 20.0, 5.0);
        sensitivitySlider.setPrefWidth(120);
        sensitivitySlider.setShowTickMarks(false);
        sensitivitySlider.valueProperty().addListener((obs, oldVal, newVal) ->
            viz.setSensitivity(newVal.doubleValue()));

        Label modeLabel = new Label("Visualizer:");
        Label waveLabel = new Label("Waveform:");
        Label sensLabel = new Label("Sensitivity:");
        modeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        waveLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        sensLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");

        HBox bar = new HBox(12, modeLabel, modeBox, waveLabel, waveBox, sensLabel, sensitivitySlider);
        bar.setStyle("-fx-padding: 10; -fx-background-color: #12122A;");
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    public BorderPane getRoot() { return root; }

    public void shutdown() {
        capture.stop();
        synth.shutdown();
    }
}
