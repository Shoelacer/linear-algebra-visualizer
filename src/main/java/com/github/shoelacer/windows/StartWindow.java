package com.github.shoelacer.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class StartWindow {


    public Scene getPane() throws Exception {


        FXMLLoader loader = new FXMLLoader(getClass().getResource("StartWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        Scene scene = new Scene(root,800,600);

        return scene;
    }

}
