package com.github.shoelacer.windows;

import com.github.shoelacer.geometry.VectorArrow;
import com.github.shoelacer.visuals.DisplayItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

public class VisualizerWindow {
    private Stage window;
    private Group root3D;

    private Scale rootZoom;


    /*
    * TODO:
    *  Replace lastClicked with an ArrayList or a HashMap or smth that lets me see if its clicked
    *  Probably an ArrayList since that lets me check the size easily
    *  Order does matter so a set won't work
    *
    * */

    private DisplayItem[] lastClicked = new DisplayItem[2];
    private ArrayList<DisplayItem> clickedItems = new ArrayList<DisplayItem>();

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
            System.out.println(rootZoom.getX());
            double zoomFactor = rootZoom.getX();
            zoomFactor = rootZoom.getX()+scroll.getDeltaY()*SENSITIVITY/20;
            zoomFactor = Math.max(zoomFactor, 1.0);
            zoomFactor = Math.min(zoomFactor, 100);
            rootZoom.setX(zoomFactor);
            rootZoom.setY(zoomFactor);
        });

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("../styles/styles.css").toExternalForm());
        return scene;
    }

    private void setup3D() {
        root3D = new Group();


        root3D.getChildren().add(new VectorArrow(Color.BLUE, 1000, 0, 0));
        root3D.getChildren().add(new VectorArrow(Color.RED, 0, 1000, 0));
        root3D.getChildren().add(new VectorArrow(Color.BLACK, 0, 0, 1000));
        AmbientLight light = new AmbientLight(Color.rgb(200,200,200,1));
        root3D.getChildren().add(light);
        rootZoom = new Scale(25,25,25,0,0,0);

        pane3d.setRoot(root3D);
        root3D.getTransforms().add(rootZoom);

        PerspectiveCamera camera = new PerspectiveCamera();
        camera.setTranslateZ(-10);
        camera.setTranslateX(-200);
        camera.setTranslateY(-200);
        camera.setFarClip(100);
        camera.setNearClip(0.0001);
        pane3d.setCamera(camera);
    }

    public void exitVisualizer(ActionEvent event) throws Exception {
        System.out.println("Exiting Visualizer");
        StartWindow startWindow = new StartWindow(window);
        window.setScene(startWindow.getPane());
    }

    //Called by the Add Vector button in FXML
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
                DisplayItem arrow = new DisplayItem(text[0],text[1],text[2],Color.BLUE);
                root3D.getChildren().add(arrow.getArrow());
                vectorList.getChildren().add(arrow.getLabel());

                arrow.getLabel().setOnMouseClicked(mouseEvent -> {
                    if(mouseEvent.getButton() == MouseButton.PRIMARY){

                        if(clickedItems.contains(arrow)){
                            clickedItems.remove(arrow);
                            arrow.getLabel().setBackground(Background.EMPTY);
                        }else{
                            clickedItems.add(arrow);
                            arrow.getLabel().setBackground(Background.fill(Color.rgb(0,0,255,0.05)));
                        }
                        /*
                        if(lastClicked[1]!=null)lastClicked[1].getLabel().setBackground(Background.EMPTY);
                        lastClicked[1]=lastClicked[0];
                        lastClicked[0]=arrow;
                        lastClicked[0].getLabel().setBackground(Background.fill(Color.rgb(0,0,255,0.05)));*/
                    }
                });
            }
        );
    }

    public void dotProduct(ActionEvent event) throws Exception {
        if(clickedItems.size()!=2){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Must select 2 vectors");
            alert.setContentText("Please select 2 vectors to display the dot product");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dot Product");
            alert.setHeaderText("Dot Product");
            alert.setContentText("Dot product of "+clickedItems.get(0).getLabel().getText()
                    + " and "+clickedItems.get(1).getLabel().getText()+" is "
                    +clickedItems.get(0).getArrow().getVector().dot(clickedItems.get(1).getArrow().getVector()));
            alert.showAndWait();
        }
    }
    public void crossProduct(ActionEvent event) throws Exception {
        if(clickedItems.size()!=2){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Must select 2 vectors");
            alert.setContentText("Please select 2 vectors to display the dot product");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cross Product");

            alert.getDialogPane().setContent(new Label("Cross product of "+clickedItems.get(0).getLabel().getText()
                    + " and "+clickedItems.get(1).getLabel().getText()+" is "
                    +clickedItems.get(0).getArrow().getVector().cross(clickedItems.get(1).getArrow().getVector())));
            alert.showAndWait();
        }
    }
}