package com.github.shoelacer.windows;

import com.github.shoelacer.geometry.VectorArrow;
import com.github.shoelacer.math.Vector3D;
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
    @FXML
    private SubScene pane3d;
    @FXML
    private VBox vectorList;
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    private double cameraDistance;
    private PerspectiveCamera camera;

    private double pitch = 0;
    private double yaw = 0;

    private ArrayList<DisplayItem> clickedItems = new ArrayList<DisplayItem>();

    final double SENSITIVITY = 1.0;

    public VisualizerWindow(Stage window) {
        this.window = window;
    }

    public Scene getPane() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VisualizerWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        setup3D();


        camera.getTransforms().addAll(rotateX, rotateY);
        double[] mousePosition = new double[2];

        pane3d.setOnMousePressed((MouseEvent me) -> {
            mousePosition[0] = me.getSceneX();
            mousePosition[1] = me.getSceneY();
        });

        pane3d.setOnMouseDragged((MouseEvent me) -> {
            double dx = (mousePosition[0] - me.getSceneX());
            double dy = (mousePosition[1] - me.getSceneY());

            yaw+=dx/(SENSITIVITY*100);
            pitch+=dy/(SENSITIVITY*100);

            camera.setTranslateX(Math.sin(yaw)*cameraDistance-200);
            camera.setTranslateY(-1*Math.sin(pitch)*cameraDistance-200);
            camera.setTranslateZ(Math.cos(yaw)*Math.cos(pitch)*cameraDistance);

            rotateZ.setAngle(Math.toDegrees(pitch));
            rotateY.setAngle(Math.toDegrees(yaw));

            System.out.printf("Camera Y: %f\nCamera Z: %f\nYaw: %f\nPitch: %f\n",
                    Math.sin(pitch)*cameraDistance+200,
                    Math.cos(pitch)*cameraDistance,
                    yaw,
                    pitch);

            mousePosition[0] = me.getSceneX();
            mousePosition[1] = me.getSceneY();
        });

        pane3d.setOnScroll(scroll -> {
            cameraDistance+=scroll.getDeltaY()*SENSITIVITY/5;
            camera.setTranslateZ(cameraDistance);

        });

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("../styles/styles.css").toExternalForm());
        return scene;
    }

    private void setup3D() {
        root3D = new Group();
        cameraDistance = 300;

        Box box = new Box(30,30,30);
        box.setMaterial(new PhongMaterial(Color.rgb(0, 0, 0,0.5)));
        box.setTranslateX(0);
        box.setTranslateY(0);
        box.setTranslateZ(0);

        root3D.getChildren().add(box);

        root3D.getChildren().add(new VectorArrow(Color.RED, 1000, 0, 0));
        root3D.getChildren().add(new VectorArrow(Color.GREEN, 0, 1000, 0));
        root3D.getChildren().add(new VectorArrow(Color.BLUE, 0, 0, 1000));
        AmbientLight light = new AmbientLight(Color.rgb(200,200,200,1));
        root3D.getChildren().add(light);
        rootZoom = new Scale(25,25,25,0,0,0);

        root3D.setTranslateX(0);
        root3D.setTranslateY(0);

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(-50);
        pointLight.setTranslateY(-50);
        pointLight.setTranslateZ(50);

        root3D.getChildren().add(pointLight);

        pane3d.setRoot(root3D);
        root3D.getTransforms().add(rootZoom);

        camera = new PerspectiveCamera();
        camera.setTranslateZ(-1*cameraDistance);
        camera.setTranslateX(-200);
        camera.setTranslateY(-200);
        camera.setFarClip(1000);
        camera.setNearClip(0.01);
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
                addVectorToPane(text[0],text[1],text[2],Color.BLUE);
            }
        );
    }

    public void addVectorToPane(double x, double y, double z, Color color) {

        System.out.printf("Adding Vector: (%f, %f, %f)\n", x, y, z);
        DisplayItem arrow = new DisplayItem(x,y,z,color);
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
            }
        });
    }
    public void addVectorToPane(Vector3D vector, Color color) {
        addVectorToPane(vector.getX(), vector.getY(), vector.getZ(), color);
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

            ButtonType addButton = new ButtonType("Add Vector");

            alert.getButtonTypes().add(addButton);
            Optional<ButtonType> selected = alert.showAndWait();
            if(selected.isPresent()&&selected.get()==(addButton)){
                addVectorToPane(clickedItems.get(0).getArrow().getVector().cross(clickedItems.get(1).getArrow().getVector()),Color.BLUE);
            }
        }
    }
    public void resetView(ActionEvent event) throws Exception {
        rotateX.setAngle(0);
        rotateY.setAngle(0);
        rotateZ.setAngle(0);

        yaw=0;
        pitch=0;

        camera.setTranslateX(Math.sin(yaw)*cameraDistance);
        camera.setTranslateY(Math.sin(pitch)*cameraDistance);
        camera.setTranslateZ(Math.cos(yaw)*Math.cos(pitch)*cameraDistance);

        rootZoom.setX(25);
        rootZoom.setY(25);
    }
}