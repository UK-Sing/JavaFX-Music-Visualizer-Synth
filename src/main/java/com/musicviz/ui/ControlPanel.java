package com.musicviz.ui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

// Placeholder for a dedicated control panel component.
// Currently the controls are inlined in MainLayout.buildControls().
// This class can be expanded to hold ADSR sliders, volume knobs, etc.
public class ControlPanel {

    private final HBox root;

    public ControlPanel() {
        root = new HBox(12);
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-padding: 10; -fx-background-color: #12122A;");

        Label placeholder = new Label("Control Panel");
        placeholder.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px;");
        root.getChildren().add(placeholder);
    }

    public HBox getRoot() { return root; }
}
