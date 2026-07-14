package com.github.shoelacer.objects;

import com.github.shoelacer.math.Matrix;
import com.github.shoelacer.math.Vector3D;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;

public class OrbitCamera {
    private static final double INITIAL_CAMERA_DISTANCE = 80;

    private Camera camera;
    private Rotate rotate;
    private Vector3D cameraPosition;
    private Vector3D cameraUp;
    private Vector3D cameraRight;
    private double cameraDistance;

    public OrbitCamera() {
        camera = new PerspectiveCamera(true);
        cameraDistance = INITIAL_CAMERA_DISTANCE;
        camera.setFarClip(10000);
        camera.setNearClip(0.01);
        cameraPosition = new Vector3D(0,0,-1*cameraDistance);
        cameraUp = new Vector3D(0,1,0);
        cameraRight = new Vector3D(1,0,0);
        rotate = new Rotate();
        camera.getTransforms().add(rotate);
        positionCamera();
    }

    public void moveCamera(double dx, double dy){
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
    }

    public void zoomCamera(double scroll) {

        cameraDistance -= scroll;
        cameraDistance = Math.max(10, cameraDistance);
        positionCamera();
    }

    private void positionCamera() {
        cameraPosition=cameraPosition.normalize().scale(cameraDistance);
        camera.setTranslateX(cameraPosition.getX());
        camera.setTranslateY(cameraPosition.getY());
        camera.setTranslateZ(cameraPosition.getZ());

        Vector3D forward = cameraPosition.normalize().scale(-1);
        Vector3D defaultForward = new Vector3D(0,0,-1);


        Vector3D axis = defaultForward.cross(forward);
        double dot = defaultForward.dot(forward);

        if (axis.magnitude() < 1e-6) {
            rotate.setAngle(0);
            return;
        }

        axis = axis.normalize();
        double angle = Math.toDegrees(Math.acos(dot));


        rotate.setAxis(new Point3D(axis.getX(), axis.getY(), axis.getZ()));
        rotate.setAngle(180+angle);

        System.out.println("axis: " + axis);
        System.out.println("angle: " + angle);
        System.out.println("Position: "+cameraPosition);
        System.out.println("Camera Right: "+cameraRight);
        System.out.println("Camera Up: "+cameraUp);

    }

    public void resetView() {
        cameraDistance = INITIAL_CAMERA_DISTANCE;
        cameraPosition = new Vector3D(0,0,-1*cameraDistance);
        cameraRight = new Vector3D(1,0,0);
        cameraUp = new Vector3D(0,1,0);
        positionCamera();
    }
    public Camera getCamera() {
        return camera;
    }
}
