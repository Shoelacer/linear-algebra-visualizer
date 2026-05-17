package com.github.shoelacer.windows;

import com.github.shoelacer.geometry.VectorArrow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

public class VisualizerWindow {
    private Stage window;

    @FXML private SubScene pane3d;

    public VisualizerWindow(Stage window) {
        this.window = window;
    }

    public Scene getPane() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VisualizerWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        setup3D();

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("../styles/styles.css").toExternalForm());
        return scene;
    }

    private void setup3D() {
        Group root3D = new Group();

        Box box = new Box(2, 2, 2);
        box.setMaterial(new PhongMaterial(Color.RED));
        root3D.getChildren().add(box);

        root3D.getChildren().add(new VectorArrow(Color.BLUE,2,2,0));

        AmbientLight light = new AmbientLight(Color.WHITE);
        root3D.getChildren().add(light);

        pane3d.setRoot(root3D);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-15);
        camera.setFarClip(1000);
        pane3d.setCamera(camera);
    }

    public void exitVisualizer(ActionEvent event) throws Exception {
        System.out.println("Exiting Visualizer");
        StartWindow startWindow = new StartWindow(window);
        window.setScene(startWindow.getPane());
    }
}