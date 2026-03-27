package com.musicviz.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        MainLayout layout = new MainLayout();
        Scene scene = new Scene(layout.getRoot(), 1280, 720);
        scene.getStylesheets().add(
            getClass().getResource("/styles/main.css").toExternalForm());
        stage.setTitle("Music Visualizer + Synthesizer");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> layout.shutdown());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
