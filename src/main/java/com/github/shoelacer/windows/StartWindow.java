package com.github.shoelacer.windows;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StartWindow {
    private Stage window;

    public StartWindow(Stage window) {
        this.window = window;
    }
    public Scene getPane() throws Exception {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("StartWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        Scene scene = new Scene(root,800,600);
        scene.getStylesheets().add(getClass().getResource("../styles/styles.css").toExternalForm());
        return scene;
    }

    public void enterVisualizer(ActionEvent event) throws Exception {
        System.out.println("Entering Visualizer");
        window.setScene((new VisualizerWindow()).getPane());
    }

}
