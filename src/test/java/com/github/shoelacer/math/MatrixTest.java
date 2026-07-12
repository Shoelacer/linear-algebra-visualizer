package com.github.shoelacer.math;

import static org.junit.jupiter.api.Assertions.*;

class MatrixTest {

    @org.junit.jupiter.api.Test
    void identity() {
        Matrix matrix = Matrix.identity(3);
        assertEquals(new Matrix(new double[][]{{1,0,0},{0,1,0},{0,0,1}}), matrix);
    }

    @org.junit.jupiter.api.Test
    void multiplyMatrix() {

        Matrix matrix1 = new Matrix(new double[][]{{5,2},{3,5},{3,5},{6,4},{2,4}});
        Matrix matrix2 = new Matrix(new double[][]{{2,5,4},{-3,4,-2}});

        Matrix result =  matrix1.multiply(matrix2);

        assertEquals(new Matrix(
                new double[][]{
                        {4,33,16},
                        {-9,35,2},
                        {-9,35,2},
                        {0,46,16},
                        {-8,26,0}
                }
        ), result);

    }

    @org.junit.jupiter.api.Test
    void multiplyVector() {
        Matrix matrix = new Matrix(new double[][]{
                {4,33,16},
                {-9,35,2},
                {8,2,1},
        });
        Vector3D vector = new Vector3D(2,-5,3);
        Vector3D result = matrix.multiply(vector);
        assertEquals(new Vector3D(-109,-187,9), result);
    }

    @org.junit.jupiter.api.Test
    void determinant() {

        Vector3D a = new Vector3D(0,0,80).normalize();
        Vector3D b = new Vector3D(10,10,80).normalize();
        Vector3D cameraUp = new Vector3D(0,1,0);
        Vector3D cameraRight = new Vector3D(1,0,0);

        Vector3D axis = a.cross(b);
        double c = a.dot(b);
        double s = axis.magnitude();
        axis=axis.normalize();

        Matrix K = new Matrix(new double[][]{
                {0,-axis.getZ(),axis.getY()},
                {axis.getZ(),0,-axis.getX()},
                {-axis.getY(),axis.getX(),0}});
        Matrix rotation = Matrix.identity(3).add(K.scale(s)).add(K.multiply(K).scale((1-c)));
        System.out.println("New: "+b+"\nRotation: "+rotation.multiply(a).normalize().scale(80));

        cameraUp = rotation.multiply(cameraUp).normalize();
        a = rotation.multiply(a).normalize().scale(80);
        cameraRight = (rotation.multiply(cameraRight)).normalize();

        System.out.println("A: "+a+"Camera Up: "+cameraUp+"\nCamera Right: "+cameraRight);
    }

    @org.junit.jupiter.api.Test
    void transpose() {
        System.out.println(new Vector3D(0.0000, 0.1231, -0.9924).cross(new Vector3D(0.0000, 0.2452, -0.9695)));
    }

    @org.junit.jupiter.api.Test
    void rotation2D() {
    }

    @org.junit.jupiter.api.Test
    void rotationX() {
    }

    @org.junit.jupiter.api.Test
    void rotationY() {
    }

    @org.junit.jupiter.api.Test
    void rotationZ() {
    }

    @org.junit.jupiter.api.Test
    void scale2D() {
    }

    @org.junit.jupiter.api.Test
    void scale3D() {
    }

    @org.junit.jupiter.api.Test
    void getCell() {
    }

    @org.junit.jupiter.api.Test
    void setCell() {
    }
}