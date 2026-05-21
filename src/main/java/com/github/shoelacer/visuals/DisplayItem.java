package com.github.shoelacer.visuals;

import com.github.shoelacer.geometry.VectorArrow;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.Optional;

public class DisplayItem {
    private VectorArrow arrow;
    private Label text;

    public DisplayItem(double x, double y, double z, Color color) {
        this.arrow = new VectorArrow(color, x, y, z);
        text = new Label("X: " + x + " Y: " + y + " Z: " + z);
        text.setOnMouseClicked(event -> {

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
    }
    public void setComponents(double x, double y, double z) {
        arrow.updateCoordinates(x, y, z);
        text.setText("Vector: (" + x + ", " + y + ", " + z + ")");
    }

    public VectorArrow getArrow() { return arrow; }
    public Label getLabel() { return text; }
}
