package com.github.shoelacer.windows;

import com.github.shoelacer.geometry.VectorArrow;
import com.github.shoelacer.math.Matrix;
import com.github.shoelacer.math.Vector3D;
import com.github.shoelacer.visuals.DisplayItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Optional;

public class VisualizerWindow {
    private static final double INITIAL_CAMERA_DISTANCE = 80;
    final static double SENSITIVITY = 1.0;
    final static double OFFSET = 0;

    private Stage window;
    private Group root3D;
    @FXML
    private SubScene pane3d;
    @FXML
    private VBox vectorList;

    // These will be applied to the camera rig
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);

    private double cameraDistance;
    private Group cameraRig;
    private PerspectiveCamera camera;

    private double pitch = 0;
    private double yaw = 0;
    private double roll = 0;

    private Vector3D cameraPosition;
    private Vector3D cameraUp;
    private Vector3D cameraRight;

    private ArrayList<DisplayItem> clickedItems = new ArrayList<DisplayItem>();

    public VisualizerWindow(Stage window) {
        this.window = window;
    }

    public Scene getPane() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VisualizerWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        setup3D();

        // Add rotations to the camera rig (not the camera directly)
        camera.getTransforms().addAll(rotateX, rotateY);

        double[] mousePosition = new double[2];

        pane3d.setOnMousePressed((MouseEvent me) -> {
            mousePosition[0] = me.getSceneX();
            mousePosition[1] = me.getSceneY();
        });

        pane3d.setOnMouseDragged((MouseEvent mouse) -> {
            double dx = (mousePosition[0] - mouse.getSceneX());
            double dy = (mousePosition[1] - mouse.getSceneY());
            mousePosition[0] = mouse.getSceneX();
            mousePosition[1] = mouse.getSceneY();
            //dx=0;
            //dy=100;


            /*DEBUG CODE*/
            {
                if (Math.abs(dy) > Math.abs(dx)) dx = 0;
                else dy = 0;

            }
            /*END*/

            Vector3D newPosition = cameraPosition
                    .add(cameraRight.normalize().scale(dx/3))
                    .add(cameraUp.normalize().scale(dy/3))
                    .normalize()
                    .scale(cameraDistance);



            Vector3D a = new Vector3D(cameraPosition.getX(), cameraPosition.getY(), cameraPosition.getZ()).normalize();
            Vector3D b = new Vector3D(newPosition.getX(), newPosition.getY(), newPosition.getZ()).normalize();

            Vector3D axis = a.cross(b);
            double c = a.dot(b);
            double s = axis.magnitude();
            axis=axis.normalize();

            if(c>0.999999999) return;

            Matrix K = new Matrix(new double[][]{
                    {0,-axis.getZ(),axis.getY()},
                    {axis.getZ(),0,-axis.getX()},
                    {-axis.getY(),axis.getX(),0}});
            double angle = Math.acos(c);
            Matrix rotation = Matrix.identity(3).add(K.scale(Math.sin(angle))).add(K.multiply(K).scale(1 - Math.cos(angle)));

            cameraUp = rotation.multiply(cameraUp).normalize();
            cameraRight = rotation.multiply(cameraRight).normalize();
            cameraPosition = rotation.multiply(cameraPosition).normalize().scale(cameraDistance);

            //Reorthonormalize?
            Vector3D forward = cameraPosition.normalize().scale(1);
            cameraRight = forward.cross(cameraUp).normalize();
            cameraUp = cameraRight.cross(forward).normalize();



            positionCamera();
        });

        pane3d.setOnScroll(scroll -> {
            cameraDistance -= scroll.getDeltaY() * SENSITIVITY / 5;
            cameraDistance = Math.max(10, cameraDistance);
            positionCamera();
        });

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("../styles/styles.css").toExternalForm());
        return scene;
    }

    private void positionCamera() {
        cameraPosition=cameraPosition.normalize().scale(cameraDistance);
        camera.setTranslateX(cameraPosition.getX());
        camera.setTranslateY(cameraPosition.getY());
        camera.setTranslateZ(cameraPosition.getZ());

        Vector3D forward = cameraPosition.normalize().scale(-1);
        Vector3D defaultForward = new Vector3D(0,0,-1);

        //if(cameraPosition.getZ()>0) defaultForward.setZ(1);

        Vector3D axis = defaultForward.cross(forward);
        double dot = defaultForward.dot(forward);

        if (axis.magnitude() < 1e-6) {
            rotateX.setAngle(0);
            return;
        }

        axis = axis.normalize();
        double angle = Math.toDegrees(Math.acos(dot));


        rotateX.setAxis(new Point3D(axis.getX(), axis.getY(), axis.getZ()));
        System.out.println("axis: " + axis);
        System.out.println("angle: " + angle);
        System.out.println("Position: "+cameraPosition);
        System.out.println("Camera Right: "+cameraRight);
        System.out.println("Camera Up: "+cameraUp);
        rotateX.setAngle(180+angle);
    }

    private void setup3D() {
        root3D = new Group();
        cameraDistance = INITIAL_CAMERA_DISTANCE;

        cameraPosition = new Vector3D(0,0,-1*cameraDistance);
        cameraUp = new Vector3D(0,1,0);
        cameraRight = new Vector3D(1,0,0);

        // Create a box at origin
        Box box = new Box(20, 20, 20);
        box.setMaterial(new PhongMaterial(Color.rgb(0, 0, 0, 0.5)));
        root3D.getChildren().add(box);

        // Add axis arrows
        root3D.getChildren().add(new VectorArrow(Color.RED, 1000, 0, 0));
        root3D.getChildren().add(new VectorArrow(Color.GREEN, 0, 1000, 0));
        root3D.getChildren().add(new VectorArrow(Color.BLUE, 0, 0, 1000));

        // Lighting
        AmbientLight light = new AmbientLight(Color.rgb(200, 200, 200, 1));
        //root3D.getChildren().add(light);

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(-50);
        pointLight.setTranslateY(-50);
        pointLight.setTranslateZ(-50);
        root3D.getChildren().add(pointLight);

        // Set up the scene
        pane3d.setRoot(root3D);

        // Create camera and camera rig
        camera = new PerspectiveCamera(true);
        camera.setFarClip(10000);
        camera.setNearClip(0.01);

        camera.setTranslateZ(-cameraDistance);


        pane3d.setCamera(camera);

        positionCamera();
    }

    public void resetView(ActionEvent event) throws Exception {
        yaw = 0;
        pitch = 0;
        roll = 0;
        cameraDistance = INITIAL_CAMERA_DISTANCE;
        cameraPosition = new Vector3D(0,0,-1*cameraDistance);
        cameraRight = new Vector3D(1,0,0);
        cameraUp = new Vector3D(0,1,0);
        positionCamera();
    }

    public void exitVisualizer(ActionEvent event) throws Exception {
        System.out.println("Exiting Visualizer");
        StartWindow startWindow = new StartWindow(window);
        window.setScene(startWindow.getPane());
    }

    // Called by the Add Vector button in FXML
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
                try {
                    return new Double[]{Double.valueOf(inputX.getText()),
                            Double.valueOf(inputY.getText()),
                            Double.valueOf(inputZ.getText())};
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        Optional<Double[]> result = dialog.showAndWait();
        result.ifPresent(text -> {
            addVectorToPane(text[0], text[1], text[2], Color.BLUE);
        });
    }

    public void addVectorToPane(double x, double y, double z, Color color) {
        System.out.printf("Adding Vector: (%f, %f, %f)\n", x, y, z);
        DisplayItem arrow = new DisplayItem(x, y, z, color);
        root3D.getChildren().add(arrow.getArrow());
        vectorList.getChildren().add(arrow.getLabel());

        arrow.getLabel().setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                if (clickedItems.contains(arrow)) {
                    clickedItems.remove(arrow);
                    arrow.getLabel().setBackground(Background.EMPTY);
                } else {
                    clickedItems.add(arrow);
                    arrow.getLabel().setBackground(Background.fill(Color.rgb(0, 0, 255, 0.05)));
                }
            }
        });
    }

    public void addVectorToPane(Vector3D vector, Color color) {
        addVectorToPane(vector.getX(), vector.getY(), vector.getZ(), color);
    }

    public void dotProduct(ActionEvent event) throws Exception {
        if (clickedItems.size() != 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Must select 2 vectors");
            alert.setContentText("Please select 2 vectors to display the dot product");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Dot Product");
            alert.setHeaderText("Dot Product");
            alert.setContentText("Dot product of " + clickedItems.get(0).getLabel().getText()
                    + " and " + clickedItems.get(1).getLabel().getText() + " is "
                    + clickedItems.get(0).getArrow().getVector().dot(clickedItems.get(1).getArrow().getVector()));
            alert.showAndWait();
        }
    }

    public void crossProduct(ActionEvent event) throws Exception {
        if (clickedItems.size() != 2) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Must select 2 vectors");
            alert.setContentText("Please select 2 vectors to display the cross product");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cross Product");

            alert.getDialogPane().setContent(new Label("Cross product of " + clickedItems.get(0).getLabel().getText()
                    + " and " + clickedItems.get(1).getLabel().getText() + " is "
                    + clickedItems.get(0).getArrow().getVector().cross(clickedItems.get(1).getArrow().getVector())));

            ButtonType addButton = new ButtonType("Add Vector");
            alert.getButtonTypes().add(addButton);

            Optional<ButtonType> selected = alert.showAndWait();
            if (selected.isPresent() && selected.get() == addButton) {
                addVectorToPane(clickedItems.get(0).getArrow().getVector()
                        .cross(clickedItems.get(1).getArrow().getVector()), Color.BLUE);
            }
        }
    }
}