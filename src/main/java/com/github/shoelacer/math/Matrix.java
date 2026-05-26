package com.github.shoelacer.math;

/* TODO */
/*
 *
 *

    double determinant();
    Matrix inverse();  // maybe do PA=LU decomp as well?

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
            matrix[i][i] = 1;
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

    public Vector3D multiply(Vector3D vector3D){
        if(this.matrix == null) throw new NullPointerException("Cannot multiply null matrix");
        if(this.matrix[0].length!=3||this.matrix.length!=3)throw new IllegalArgumentException("Invalid matrix dimensions");
        Vector3D result = new Vector3D(0,0,0);
        result = result.add(new Vector3D(matrix[0][0],matrix[1][0],matrix[2][0]).scale(vector3D.getX()));
        result = result.add(new Vector3D(matrix[0][1],matrix[1][1],matrix[2][1]).scale(vector3D.getY()));
        result = result.add(new Vector3D(matrix[0][2],matrix[1][2],matrix[2][2]).scale(vector3D.getZ()));
        return result;
    }

    public double determinant(){
        if(this.matrix == null) throw new NullPointerException("Cannot compute determinant of null matrix");
        if(this.matrix.length!=3 || this.matrix[0].length!=3)throw new IllegalArgumentException("Determinant only supported for 3x3 matrices");
        double det = 0;
        det+=this.matrix[0][0]*(this.matrix[1][1]*this.matrix[2][2]-this.matrix[1][2]*this.matrix[2][1]);
        det-=this.matrix[0][1]*(this.matrix[1][0]*this.matrix[2][2]-this.matrix[1][2]*this.matrix[2][0]);
        det+=this.matrix[0][2]*(this.matrix[1][0]*this.matrix[2][1]-this.matrix[1][1]*this.matrix[2][0]);
        return det;
    }

    public static Matrix transpose(Matrix matrix){
        if(matrix == null) throw new NullPointerException("Cannot transpose null matrix");
        Matrix result = new Matrix(matrix.matrix[0].length, matrix.matrix.length);
        for(int i = 0; i < matrix.matrix.length; i++){
            for(int j = 0; j < matrix.matrix[0].length; j++){
                result.matrix[j][i] = matrix.matrix[i][j];
            }
        }
        return result;
    }

    public static Matrix rotation2D(double angle){
        Matrix matrix = new Matrix(2,2);
        matrix.matrix[0][0] = Math.cos(angle);
        matrix.matrix[1][1] = Math.cos(angle);
        matrix.matrix[1][0] = Math.sin(angle);
        matrix.matrix[0][1] = -1*Math.sin(angle);
        return matrix;
    }

    public static Matrix rotationX(double angle){
        Matrix matrix = identity(3);
        matrix.matrix[1][1] = Math.cos(angle);
        matrix.matrix[2][2] = Math.cos(angle);
        matrix.matrix[2][1] = Math.sin(angle);
        matrix.matrix[1][2] = -1*Math.sin(angle);
        return matrix;
    }

    public static Matrix rotationY(double angle){
        Matrix matrix = identity(3);
        matrix.matrix[0][0] = Math.cos(angle);
        matrix.matrix[2][2] = Math.cos(angle);
        matrix.matrix[0][2] = Math.sin(angle);
        matrix.matrix[2][0] = -1*Math.sin(angle);
        return matrix;
    }

    public static Matrix rotationZ(double angle){
        Matrix matrix = identity(3);
        matrix.matrix[0][0] = Math.cos(angle);
        matrix.matrix[1][1] = Math.cos(angle);
        matrix.matrix[1][0] = Math.sin(angle);
        matrix.matrix[0][1] = -1*Math.sin(angle);
        return matrix;
    }

    public static Matrix scale2D(double sx, double sy){
        Matrix matrix = identity(2);
        matrix.setCell(0, 0, sx);
        matrix.setCell(1, 1, sy);
        return matrix;
    }

    public static Matrix scale3D(double sx,  double sy, double sz){
        Matrix matrix = identity(3);
        matrix.setCell(0, 0, sx);
        matrix.setCell(1, 1, sy);
        matrix.setCell(2, 2, sz);
        return matrix;
    }

    public double getCell(int i, int j){
        if(i>this.matrix.length)throw new ArrayIndexOutOfBoundsException("Cannot row "+i+" of matrix with "+this.matrix.length+" rows");
        if(j>this.matrix[0].length)throw new ArrayIndexOutOfBoundsException("Cannot col "+j+" of matrix with "+this.matrix[0].length+" rows");
        return this.matrix[i][j];
    }
    public void setCell(int i, int j, double value){
        if(i>this.matrix.length)throw new ArrayIndexOutOfBoundsException("Cannot row "+i+" of matrix with "+this.matrix.length+" rows");
        if(j>this.matrix[0].length)throw new ArrayIndexOutOfBoundsException("Cannot col "+j+" of matrix with "+this.matrix[0].length+" rows");
        this.matrix[i][j] = value;
    }

    public String toString(){
        String result = "";
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++){
                result += this.matrix[i][j] + " ";
            }
            result += "\n";
        }
        return result;
    }
}
