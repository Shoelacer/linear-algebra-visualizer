package com.github.shoelacer.geometry;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

public class VectorArrow extends Group {

    public VectorArrow(Color color, double x, double y, double z) {
        double length = Math.sqrt(x*x + y*y + z*z);
        if (length < 0.01) return;

        // Shaft
        Cylinder shaft = new Cylinder(0.05, length);
        shaft.setMaterial(new PhongMaterial(color));
        shaft.setTranslateY(length / 2);

        // Tip (small sphere at the end)
        // Might change to cone later
        Sphere tip = new Sphere(0.12);
        tip.setMaterial(new PhongMaterial(color));
        tip.setTranslateY(length);

        getChildren().addAll(shaft, tip);
        rotateTo(x, y, z);
    }

    private void rotateTo(double x, double y, double z) {
        double length = Math.sqrt(x*x + y*y + z*z);
        /*TODO
        * Fix this because since the rotates are applied one by one, it ends up rotating them incorrectly.
        *
        *   */
        double angleY = Math.atan2(-x, z);
        double angleX = Math.atan2(y, z);
        double angleZ = Math.atan2(y, x);
        System.out.printf("Angle X: %f, Angle Y: %f, Angle Z: %f\n", Math.toDegrees(angleX),Math.toDegrees(angleY),Math.toDegrees(angleZ));
        getTransforms().addAll(
                //new Rotate(Math.toDegrees(angleX), 0,0,0,Rotate.X_AXIS),
                //new Rotate(Math.toDegrees(angleY), 0, 0, 0, Rotate.Y_AXIS),
                new Rotate(Math.toDegrees(angleZ), 0, 0, 0, Rotate.Z_AXIS)
        );
    }
}