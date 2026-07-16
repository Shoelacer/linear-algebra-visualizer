package com.github.shoelacer.visuals;

import com.github.shoelacer.math.Matrix;
import com.github.shoelacer.math.Vector3D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class MatrixDisplayItem {
    private Matrix matrix;
    private Label text;
    private ContextMenu contextMenu;
    public MatrixDisplayItem(Matrix matrix) {
        this.matrix = matrix;
        text = new Label(matrix.toString());
        contextMenu = new ContextMenu();
        MenuItem editMatrix = new MenuItem("Edit Vector");
        contextMenu.getItems().addAll(editMatrix);
        text.setContextMenu(contextMenu);

        editMatrix.setOnAction(e -> {
            Dialog<Matrix> dialog = new Dialog<>();
            dialog.setTitle("Edit Matrix");
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
                    Matrix newMatrix = new Matrix(3,3);
                    for(int i=0;i<9;i++){
                        newMatrix.setCell(i/3,i%3,Double.valueOf(inputs[i].getText()));
                    }
                    return newMatrix;
                }
                return null;
            });

            Optional<Matrix> result = dialog.showAndWait();
            result.ifPresent(newMatrix -> {
                for(int i=0;i<3;i++){
                    for(int j=0;j<3;j++) {
                        this.matrix.setCell(i,j,newMatrix.getCell(i,j));
                    }
                }
                this.getLabel().setText(matrix.toString());
            });
        });


    }
    public Label getLabel() { return text; }

}
