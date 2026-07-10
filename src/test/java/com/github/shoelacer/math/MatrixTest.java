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
    }

    @org.junit.jupiter.api.Test
    void transpose() {
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