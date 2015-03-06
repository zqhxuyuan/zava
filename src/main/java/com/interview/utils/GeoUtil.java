package com.interview.utils;

import com.interview.basics.model.geometry.Vector;

/**
 * Created_By: stefanie
 * Date: 15-1-5
 * Time: 下午12:02
 */
public class GeoUtil {
    public static float distance(float[] A, float[] B){
        return new Vector(A, B).length();
    }

    public static float dotProduct(float[] A, float[] B, float[] C){
        Vector ab = new Vector(A, B);
        Vector bc = new Vector(B, C);
        return ab.dot(bc);
    }

    public static float crossProduct(float[] A, float[] B, float[] C){
        Vector ab = new Vector(A, B);
        Vector ac = new Vector(A, C);
        return ab.cross(ac);
    }

    public static float[] midpoint(float[] A, float[] B){
        return new float[]{(A[0] + B[0])/2, (A[1] + B[1])/2};
    }

    public static void plus(float[] point, float[] move){
        point[0] += move[0];
        point[1] += move[1];
    }

    public static void minus(float[] point, float[] move){
        point[0] -= move[0];
        point[1] -= move[1];
    }

    public static float[] rotate(float[] point, float[] origin, int degree){
        minus(point, origin);

        double radians = Math.toRadians(degree);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        float[] rotated = new float[2];
        rotated[0] = (float) (point[0] * cos - point[1] * sin);
        rotated[1] = (float) (point[0] * sin - point[1] * cos);

        plus(rotated, origin);
        plus(point, origin);
        return rotated;
    }

    public static boolean isPerpendicular(float[] A, float[] B, float[] C){
        float dotProduct = dotProduct(A, B, C);
        if(FloatAssertion.isZero(dotProduct)) return true;
        else return false;
    }
}
