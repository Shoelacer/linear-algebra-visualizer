package com.github.shoelacer.geometry;

import javafx.geometry.Point3D;
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
        Rotate orientation = new Rotate();
        Point3D target = new Point3D(x, y, z).normalize();
        Point3D up = Rotate.Y_AXIS;

        double dot = up.dotProduct(target);
        System.out.printf("target: %f,%f,%f\nDot Product: %f\n", target.getX(),target.getY(),target.getZ(),dot);

        //Parallel/AntiParallel cases
        if (Math.abs(dot - 1.0) < 1e-3) {
            System.out.println("Same axis");
            return;
        }
        if (Math.abs(dot + 1.0) < 1e-3) {
            orientation.setAngle(180);
            orientation.setAxis(Rotate.X_AXIS);
            System.out.println("diff axis");
            if (!getTransforms().contains(orientation)) {
                getTransforms().add(orientation);
            }
            return;
        }

        Point3D axis = up.crossProduct(target).normalize();
        double angle = Math.toDegrees(Math.acos(dot));

        orientation.setAxis(axis);
        orientation.setAngle(angle);

        if (!getTransforms().contains(orientation)) {
            getTransforms().add(orientation);
        }
    }
}