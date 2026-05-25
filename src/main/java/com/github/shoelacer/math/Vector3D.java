package com.github.shoelacer.math;

import java.util.Vector;

public class Vector3D {
    private final double x, y, z;

    // Constructors
    public Vector3D(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3D(double[] components){
        this.x = components[0];
        this.y = components[1];
        this.z = components[2];
    }

    // Basic operations

    /** Returns a NEW VECTOR that is the sum of the given vectors
     */
    public Vector3D add(Vector3D v){
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }
    /** Returns a NEW VECTOR that is the difference of the given vectors
     */
    public Vector3D subtract(Vector3D v){
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }
    /** Returns a NEW SCALED VECTOR
     */
    public Vector3D scale(double scalar){
        return new Vector3D(x * scalar, y * scalar, z * scalar);
    }

    // Products
    public double dot(Vector3D v){
        return x * v.x + y * v.y + z * v.z;
    }
    public Vector3D cross(Vector3D v){
        return new Vector3D(y * v.z-z*v.y, z * v.x-x*v.z, x * v.y-y*v.z);
    }

    // Properties
    public double magnitude(){
        return Math.sqrt(x * x + y * y + z * z);
    }
    public Vector3D normalize(){
        double mag = this.magnitude();
        return new Vector3D(this.x/mag, this.y/mag, this.z/mag);
    }
    public double angleBetween(Vector3D v){
        double cosAngle = dot(v) / (magnitude() * v.magnitude());
        cosAngle = Math.clamp(cosAngle, -1.0, 1.0);
        return Math.acos(cosAngle);
    }

    // Getters
    public double getX() {
        return this.x;
    }
    public double getY() {
        return this.y;
    }
    public double getZ() {
        return this.z;
    }


    // Utility
    public boolean equals(Vector3D v){
        return  this.x == v.x && this.y == v.y && this.z == v.z;
    }
    public String toString(){
        return "[" + this.x + ", " + this.y + ", " + this.z+"]";
    }
}