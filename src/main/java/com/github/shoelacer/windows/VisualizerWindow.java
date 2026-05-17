package com.github.shoelacer.windows;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class VisualizerWindow {
    private Stage window;

    public VisualizerWindow(Stage window) {
        this.window = window;
    }
    public Scene getPane() throws Exception {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("VisualizerWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        Scene scene = new Scene(root,600,400);
        scene.getStylesheets().add(getClass().getResource("../styles/styles.css").toExternalForm());
        return scene;
    }
    public void exitVisualizer(ActionEvent event) throws Exception {
        System.out.println("Exiting Visualizer");
        window.setScene((new StartWindow(window)).getPane());
    }
}
