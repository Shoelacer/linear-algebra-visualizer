package com.github.shoelacer.visuals;

import com.github.shoelacer.geometry.Plane2D;
import com.github.shoelacer.math.Matrix;
import com.github.shoelacer.math.Vector3D;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.util.Optional;

public class MatrixDisplayItem {
    private Matrix matrix;
    private Label text;
    private ContextMenu contextMenu;
    private Plane2D columnSpace;
    private Group root;
    public MatrixDisplayItem(Matrix matrix, Group root) {
        this.matrix = matrix;
        text = new Label(matrix.toString());
        contextMenu = new ContextMenu();
        MenuItem editMatrix = new MenuItem("Edit Matrix");
        MenuItem showColumnSpace = new MenuItem("Show Column Space");
        MenuItem hideColumnSpace = new MenuItem("Hide Column Space");
        contextMenu.getItems().addAll(editMatrix,showColumnSpace);
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

            ButtonType transformButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
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
        showColumnSpace.setOnAction(e -> {
            contextMenu.getItems().remove(showColumnSpace);
            contextMenu.getItems().add(hideColumnSpace);
            columnSpace = new Plane2D(Color.RED, 10,10,1,1,1);
            root.getChildren().add(columnSpace);
        });
        hideColumnSpace.setOnAction(e->{
            contextMenu.getItems().remove(hideColumnSpace);
            contextMenu.getItems().add(showColumnSpace);
            root.getChildren().remove(columnSpace);
        });

    }
    public Label getLabel() { return text; }

}
