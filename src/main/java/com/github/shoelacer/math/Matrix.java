package com.github.shoelacer.math;

/* TODO */
/*
 *
 *
    Vector3D multiply(Vector3D v);
    Matrix transpose();

    double determinant();
    Matrix inverse();  // maybe do PA=LU decomp as well?

    static Matrix rotation2D(double angle);
    static Matrix scale2D(double sx, double sy);

    static Matrix rotationX(double angle);
    static Matrix rotationY(double angle);
    static Matrix rotationZ(double angle);
    static Matrix scale3D(double sx, double sy, double sz);

 *
 *
 */

public class Matrix {

    private double[][] matrix;

    public Matrix(double[][] matrix){
        this.matrix = matrix;
    }
    public Matrix(int rows, int cols){
        this.matrix = new double[rows][cols];
    }
    public Matrix(Matrix matrix){
        this.matrix = matrix.matrix;
    }
    public static Matrix identity(int size){
        double[][] matrix = new double[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++) matrix[i][j] = 1;
        }
        return new Matrix(matrix);
    }

    public Matrix multiply(Matrix matrix){
        if(matrix == null) throw new NullPointerException("Cannot multiply null matrix");
        if(matrix.matrix.length != this.matrix[0].length) {
            throw new IllegalArgumentException("Cannot multiply matrix of width "+this.matrix[0].length+" with matrix of length "+matrix.matrix.length);
        }
        double[][] newMatrix = new double[this.matrix.length][matrix.matrix[0].length];
        for(int i = 0; i < this.matrix.length; i++){
            for(int j=0; j< matrix.matrix[0].length; j++){
                double dot = 0;
                for(int k=0; k< matrix.matrix.length; k++){
                    dot+=this.matrix[i][k]*matrix.matrix[k][j];
                }
                newMatrix[i][j] = dot;
            }
        }
        return  new Matrix(newMatrix);
    }

    public double getCell(int i, int j){
        return this.matrix[i][j];
    }
    public void setCell(int i, int j, double value){
        this.matrix[i][j] = value;
    }
}
