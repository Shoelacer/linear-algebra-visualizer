package com.github.shoelacer;

import com.github.shoelacer.windows.StartWindow;
import com.github.shoelacer.windows.VisualizerWindow;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.Scanner;

public class Launch extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage window) throws Exception {
        window.setScene((new StartWindow(window)).getPane());
        window.show();
    }
}
