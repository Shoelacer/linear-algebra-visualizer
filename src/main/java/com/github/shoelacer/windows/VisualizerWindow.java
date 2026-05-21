package com.github.shoelacer.windows;

import com.github.shoelacer.geometry.VectorArrow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

public class VisualizerWindow {
    private Stage window;
    private Group root3D;

    private Scale rootZoom;

    @FXML
    private SubScene pane3d;
    @FXML
    private VBox vectorList;

    final double SENSITIVITY = 1.0;

    public VisualizerWindow(Stage window) {
        this.window = window;
    }

    public Scene getPane() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VisualizerWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        setup3D();

        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);

        root3D.getTransforms().addAll(rotateX, rotateY, rotateZ);
        double[] mousePosition = new double[2];

        pane3d.setOnMousePressed((MouseEvent me) -> {
            mousePosition[0] = me.getSceneX();
            mousePosition[1] = me.getSceneY();
        });

        pane3d.setOnMouseDragged((MouseEvent me) -> {
            double dx = (mousePosition[0] - me.getSceneX());
            double dy = (mousePosition[1] - me.getSceneY());

            if (me.isPrimaryButtonDown()) {
                if(me.isShiftDown()){
                    rotateZ.setAngle(rotateZ.getAngle() - dx * SENSITIVITY);
                }else {
                    rotateX.setAngle(rotateX.getAngle() - dy * SENSITIVITY);
                    rotateY.setAngle(rotateY.getAngle() - dx * SENSITIVITY);
                }
            }

            mousePosition[0] = me.getSceneX();
            mousePosition[1] = me.getSceneY();
        });

        pane3d.setOnScroll(scroll -> {
            System.out.println(scroll.getDeltaY());
            root3D.setScaleX(root3D.getScaleX() + scroll.getDeltaY() * SENSITIVITY/1000);
            root3D.setScaleY(root3D.getScaleY() + scroll.getDeltaY() * SENSITIVITY/1000);
            //rootZoom = new Scale((Math.pow(2,scroll.getDeltaY())+1)*rootZoom.getX(), (Math.pow(2,scroll.getDeltaY())+1)*rootZoom.getY());
        });

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("../styles/styles.css").toExternalForm());
        return scene;
    }

    private void setup3D() {
        root3D = new Group();

        Box box = new Box(2, 2, 2);
        box.setMaterial(new PhongMaterial(Color.BLUE));
        root3D.getChildren().add(box);

        root3D.getChildren().add(new VectorArrow(Color.BLUE, 1000, 0, 0));
        root3D.getChildren().add(new VectorArrow(Color.RED, 0, 1000, 0));
        root3D.getChildren().add(new VectorArrow(Color.BLACK, 0, 0, 1000));
        //root3D.getChildren().add(new VectorArrow(Color.ORANGE, 0, 2, 0));
        //root3D.getChildren().add(new VectorArrow(Color.BLACK, 0, -2, 0));

        AmbientLight light = new AmbientLight(Color.rgb(200,200,200,0.4));
        root3D.getChildren().add(light);

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(-400);
        pointLight.setTranslateY(-400);
        pointLight.setTranslateZ(-10);
        root3D.getChildren().add(pointLight);

        rootZoom = new Scale(30,30);

        pane3d.setRoot(root3D);
        root3D.getTransforms().add(rootZoom);
        //root3D.getTransforms().add(new Rotate(30,Rotate.X_AXIS));
        //root3D.getTransforms().add(new Rotate(30,Rotate.Y_AXIS));
        //PerspectiveCamera camera = new PerspectiveCamera(false);
        ParallelCamera camera = new ParallelCamera();
        camera.setTranslateZ(-10);
        camera.setTranslateX(-200);
        camera.setTranslateY(-200);
        camera.setFarClip(100);
        camera.setNearClip(0.1);
        pane3d.setCamera(camera);
    }

    public void exitVisualizer(ActionEvent event) throws Exception {
        System.out.println("Exiting Visualizer");
        StartWindow startWindow = new StartWindow(window);
        window.setScene(startWindow.getPane());
    }

    public void addVector(ActionEvent event) throws Exception {
        Dialog<Double[]> dialog = new Dialog<>();
        dialog.setTitle("Add Vector");
        dialog.setHeaderText("Enter vector dimensions: ");

        TextField inputX = new TextField();
        TextField inputY = new TextField();
        TextField inputZ = new TextField();
        inputX.setPromptText("X");
        inputY.setPromptText("Y");
        inputZ.setPromptText("Z");
        VBox box = new VBox(10);

        box.getChildren().addAll(inputX, inputY, inputZ);
        dialog.getDialogPane().setContent(box);

        ButtonType addButton = new ButtonType("Add Vector", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, addButton);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                return new Double[]{Double.valueOf(inputX.getText()), Double.valueOf(inputY.getText()), Double.valueOf(inputZ.getText())};
            }
            return null;
        });

        Optional<Double[]> result = dialog.showAndWait();
        result.ifPresent(text -> {
                    System.out.printf("Adding Vector: (%f, %f, %f)\n", text[0], text[1], text[2]);
                    root3D.getChildren().add(new VectorArrow(Color.BLUE, text[0], text[1], text[2]));
                    Label label = new Label("Vector: (" + text[0] + ", " + text[1] + ", " + text[2] + ")");
                    label.setMinHeight(50);
                    label.setWrapText(true);
                    vectorList.getChildren().add(label);
                }
        );
    }
}