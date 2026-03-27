module com.musicviz {
    requires javafx.controls;
    requires javafx.fxml;
    requires commons.math3;
    requires com.google.gson;
    requires java.desktop;
    requires jlayer;
    opens com.musicviz.ui to javafx.fxml;
    exports com.musicviz.ui;
}