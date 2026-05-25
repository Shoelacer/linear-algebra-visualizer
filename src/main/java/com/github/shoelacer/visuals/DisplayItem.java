package com.github.shoelacer.visuals;

import com.github.shoelacer.geometry.VectorArrow;
import com.github.shoelacer.math.Matrix;
import com.github.shoelacer.math.Vector3D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Optional;

public class DisplayItem {
    private VectorArrow arrow;
    private Label text;
    private ContextMenu contextMenu;

    public DisplayItem(double x, double y, double z, Color color) {
        this.arrow = new VectorArrow(color, x, y, z);
        text = new Label("(" + x + ", " + y + ", " + z+")");

        MenuItem hideVector = new MenuItem("Hide Vector");
        MenuItem showVector = new MenuItem("Show Vector");
        MenuItem editVector = new MenuItem("Edit Vector");
        MenuItem matrixTransform = new MenuItem("Matrix Transform");
        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(hideVector, editVector, matrixTransform);
        text.setContextMenu(contextMenu);

        hideVector.setOnAction(e -> {
            arrow.setVisible(false);
            contextMenu.getItems().remove(hideVector);
            contextMenu.getItems().addFirst(showVector);
        });

        showVector.setOnAction(e -> {
            arrow.setVisible(true);
            contextMenu.getItems().addFirst(hideVector);
            contextMenu.getItems().remove(showVector);
        });

        editVector.setOnAction(event -> {

            Dialog<Double[]> dialog = new Dialog<>();
            dialog.setTitle("Edit Vector");
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

            ButtonType addButton = new ButtonType("Edit Vector", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, addButton);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButton) {
                    return new Double[]{Double.valueOf(inputX.getText()), Double.valueOf(inputY.getText()), Double.valueOf(inputZ.getText())};
                }
                return null;
            });

            Optional<Double[]> result = dialog.showAndWait();
            result.ifPresent(text -> {
                        System.out.printf("Editing Vector: (%f, %f, %f)\n", text[0], text[1], text[2]);

                        setComponents(text[0], text[1], text[2]);
                    }
            );
        });

        matrixTransform.setOnAction(event -> {
            Dialog<Matrix> dialog = new Dialog<>();
            dialog.setTitle("Matrix Transform");
            dialog.setHeaderText("Enter Matrix");
            GridPane grid = new GridPane();
            dialog.getDialogPane().setContent(grid);
            grid.setHgap(10);
            grid.setVgap(10);
            TextField[] inputs = new TextField[9];
            for (int i = 0; i < 9; i++) {
                inputs[i] = new TextField();
                grid.add(inputs[i], i%3, i/3);
            }

            ButtonType transformButton = new ButtonType("Matrix Transform", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, transformButton);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == transformButton) {
                    Matrix matrix = new Matrix(3,3);
                    for(int i=0;i<9;i++){
                        matrix.setCell(i/3,i%3,Double.valueOf(inputs[i].getText()));
                    }
                    return matrix;
                }
                return null;
            });

            Optional<Matrix> result = dialog.showAndWait();
            result.ifPresent(matrix -> {
                Vector3D vector = arrow.getVector();
                Vector3D newVector = result.get().multiply(vector);
                System.out.println(vector);
                System.out.println(newVector);
                setComponents(newVector.getX(), newVector.getY(), newVector.getZ());
            });
        });

    }


    public void setComponents(double x, double y, double z) {
        arrow.updateCoordinates(x, y, z);
        text.setText("(" + x + ", " + y + ", " + z + ")");
    }



    public VectorArrow getArrow() { return arrow; }
    public Label getLabel() { return text; }
}
