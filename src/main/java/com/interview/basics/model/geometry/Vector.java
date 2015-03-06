package com.interview.basics.model.geometry;

/**
 * Created_By: stefanie
 * Date: 15-1-4
 * Time: 下午6:21
 */
public class Vector {
    float[] fields;

    public Vector(float[] vector){
        this.fields = vector;
    }

    public Vector(float[] pointA, float[] pointB){
        fields = new float[pointA.length];
        for(int i = 0; i < pointA.length; i++) fields[i] = pointB[i] - pointA[i];
    }

    public float length(){
        float sum = 0;
        for(int i = 0; i < fields.length; i++) sum += fields[i] * fields[i];
        return (float) Math.sqrt(sum);
    }

    public Vector plus(Vector v1){
        float[] vector = new float[fields.length];
        for(int i = 0; i < fields.length; i++) vector[i] = fields[i] + v1.fields[i];
        return new Vector(vector);
    }

    public Vector minus(Vector v1){
        float[] vector = new float[fields.length];
        for(int i = 0; i < fields.length; i++) vector[i] = fields[i] - v1.fields[i];
        return new Vector(vector);
    }

    public float dot(Vector v1){
        float dot = 0;
        for(int i = 0; i < fields.length; i++) dot += fields[i] * v1.fields[i];
        return dot;
    }

    public float cosine(Vector v1){
        float dot = dot(v1);
        return dot / (this.length() * v1.length());
    }

    /**
     * Implement only for 2D
     */
    public float cross(Vector v1){
        return fields[0] * v1.fields[1] - fields[1] * v1.fields[0];
    }

    //x' = x Cos(θ) - y Sin(θ) and y' = x Sin(θ) + y Cos(θ)
    public Vector rotate(int degree){
        double radians = Math.toRadians(degree);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        float[] vector = new float[2];
        vector[0] = (float) (fields[0] * cos - fields[1] * sin);
        vector[1] = (float) (fields[0] * sin - fields[1] * cos);
        return new Vector(vector);
    }
}
