package com.github.shoelacer.geometry;

import com.github.shoelacer.math.Vector3D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.scene.shape.Box;

public class Plane2D extends Group {

    private Color color;
    private double width;
    private double height;

    //Normal vectors
    private double nx;
    private double ny;
    private double nz;
    private double opacity;

    public Plane2D(Color color, double width, double height, double nx, double ny, double nz) {
        this(color, width, height, nx, ny, nz, 0.2);
    }

    public Plane2D(Color color, double width, double height, double nx, double ny, double nz, double opacity) {
        this.color = color;
        this.width = width;
        this.height = height;
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        this.opacity = opacity;
        createPlane();
    }

    public void createPlane() {
        getTransforms().clear();
        getChildren().clear();

        double length = Math.sqrt(nx*nx + ny*ny + nz*nz);
        if (length < 0.01) return;

        Box plane = new Box(width, height,1);
        PhongMaterial material = new PhongMaterial(color);
        plane.setOpacity(0.1);
        plane.setMaterial(material);

        getChildren().add(plane);

        rotateTo(nx, ny, nz);
    }

    private void rotateTo(double nx, double ny, double nz) {
        Point3D target = new Point3D(nx, ny, nz).normalize();
        Point3D defaultNormal = new Point3D(0, 0, 1);

        double dot = defaultNormal.dotProduct(target);

        if (Math.abs(dot - 1.0) < 1e-6) {
            return;
        }

        if (Math.abs(dot + 1.0) < 1e-6) {
            Rotate rotation = new Rotate(180, Rotate.X_AXIS);
            getTransforms().add(rotation);
            return;
        }

        Point3D axis = defaultNormal.crossProduct(target).normalize();
        double angle = Math.toDegrees(Math.acos(Math.max(-1, Math.min(1, dot))));

        Rotate rotation = new Rotate(angle, axis);
        getTransforms().add(rotation);
    }

    public void setPosition(double x, double y, double z) {
        getTransforms().removeIf(t -> t instanceof Translate);
        getTransforms().add(new Translate(x, y, z));
    }

    public void updatePlane(double nx, double ny, double nz) {
        this.nx = nx;
        this.ny = ny;
        this.nz = nz;
        createPlane();
    }

    public void updatePlane(double nx, double ny, double nz, double width, double height) {
        this.width = width;
        this.height = height;
        updatePlane(nx, ny, nz);
    }

    public Vector3D getNormal() {
        return new Vector3D(nx, ny, nz);
    }

    public void setWidth(double width) {
        this.width = width;
        createPlane();
    }

    public void setHeight(double height) {
        this.height = height;
        createPlane();
    }

    public void setColor(Color color) {
        this.color = color;
        createPlane();
    }

}