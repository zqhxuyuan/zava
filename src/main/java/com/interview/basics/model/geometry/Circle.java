package com.interview.basics.model.geometry;

/**
 * Created_By: stefanie
 * Date: 15-1-4
 * Time: 下午9:27
 */

/**
 * This task turns out to be a simple application of line intersection.
 * We want to find the perpendicular bisectors of XY and YZ, and then find the intersection of
 * those two bisectors. This gives us the center of the circle.
 */
public class Circle {
    public float[] center;
    public float radius;

    public Circle(){
        this.center = new float[2];
        this.radius = 0;
    }

    public Circle(float[] X, float[] Y, float[] Z){
        Line xy = new Line(X, Y);
        Line xyPerpendicular = xy.perpendicular();

        Line yz = new Line(Y, Z);
        Line yzPerpendicular = yz.perpendicular();

        center = xyPerpendicular.intersection(yzPerpendicular);
        radius = new Vector(X, center).length();
    }
}
