package com.github.shoelacer.windows;

import com.github.shoelacer.geometry.Plane2D;
import com.github.shoelacer.geometry.VectorArrow;
import com.github.shoelacer.math.Matrix;
import com.github.shoelacer.math.Vector3D;
import com.github.shoelacer.objects.OrbitCamera;
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

    private OrbitCamera orbitCamera;



    private ArrayList<DisplayItem> clickedItems = new ArrayList<DisplayItem>();

    public VisualizerWindow(Stage window) {
        this.window = window;
    }

    public Scene getPane() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VisualizerWindow.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        setup3D();
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
            orbitCamera.moveCamera(dx,dy);

        });

        pane3d.setOnScroll(scroll -> {

            orbitCamera.zoomCamera(scroll.getDeltaY()*SENSITIVITY/5);
        });

        Scene scene = new Scene(root, 600, 400);
        scene.getStylesheets().add(getClass().getResource("../styles/styles.css").toExternalForm());
        return scene;
    }


    private void setup3D() {
        root3D = new Group();

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
        orbitCamera = new OrbitCamera();
        pane3d.setCamera(orbitCamera.getCamera());


        //Test Stuff
        Plane2D test = new Plane2D(Color.BLUE, 10, 10, 10, 1,1 );
        root3D.getChildren().add(test);

    }

    public void resetView(ActionEvent event) throws Exception {
        orbitCamera.resetView();
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