package com.interview.basics.model.geometry;

import com.interview.utils.FloatAssertion;
import com.interview.utils.GeoUtil;

/**
 * Created_By: stefanie
 * Date: 15-1-4
 * Time: 下午8:58
 */
public class Line {
    boolean isSegment;
    float A;
    float B;
    float C;

    float[] X;
    float[] Y;

    public Line(float A, float B, float C){
        isSegment = false;
        this.A = A;
        this.B = B;
        this.C = C;
        //use x = 0 and 100 as endpoint for line.
        sampleEndpoint();
    }

    private void sampleEndpoint(){
        X = new float[2];
        X[0] = 0;
        X[1] = (C - A * X[0])/B;
        Y = new float[2];
        Y[0] = 100;
        Y[1] = (C - A * Y[0])/B;
    }

    /**
     * A = y2-y1, B = x1-x2, C = A*x1+B*y1
     */
    public Line(float[] p1, float[] p2){
        isSegment = true;
        this.A = p2[1] - p1[1];
        this.B = p1[0] - p2[0];
        this.C = A * p1[0] + B * p1[1];
        X = p1;
        Y = p2;
    }

    public Line(float A, float B, float[] point){
        isSegment = false;
        this.A = A;
        this.B = B;
        this.C = A * point[0] + B * point[1];
        sampleEndpoint();
    }

    public float[][] endpoints(){
        if(isSegment){
            return new float[][]{X, Y};
        } else {
            return new float[2][0];
        }
    }

    public float distance(float[] Z){
        if(isSegment){
            if(GeoUtil.dotProduct(X, Y, Z) > 0) return GeoUtil.distance(Y, Z);
            if(GeoUtil.dotProduct(Y, X, Z) > 0) return GeoUtil.distance(X, Z);
        }
        return Math.abs(GeoUtil.crossProduct(X, Y, Z) / GeoUtil.distance(X, Y));
    }

    /**
     * be careful about double precision issues, FloatAssertion.
     * @param point
     * @return
     */
    public boolean onLine(float[] point){
        if(isSegment){
            if(!FloatAssertion.inRange(point[0], Math.min(X[0], Y[0]), Math.max(X[0], Y[0]))
                    || !FloatAssertion.inRange(point[0], Math.min(X[0], Y[0]), Math.max(X[0], Y[0]))) return false;
        }
        return FloatAssertion.isZero(Math.abs(A * point[0] + B * point[1] - C));
    }

    public float[] intersection(Line line){
        float det = this.A * line.B - line.A * this.B;
        if(FloatAssertion.isZero(det)){
            return null;
        } else {
            float x = (line.B * this.C - this.B * line.C)/det;
            float y = (this.A * line.C - line.A * this.C)/det;
            float[] intersection =  new float[]{x, y};
            if(isSegment){
                if( !this.onLine(intersection) || !line.onLine(intersection)) return null;
            }
            return intersection;
        }
    }

    //Ax+By = C -> -Bx+Ay = D
    public Line perpendicular(){
        if(isSegment){
            float[] midpoint = GeoUtil.midpoint(X, Y);
            return new Line(-this.B, this.A, midpoint);
        } else {
            return new Line(-this.B, this.A, 0);
        }
    }

    public Line perpendicular(float[] point){
        return new Line(-this.B, this.A, point);
    }

    public float[] reflection(float[] point){
        Line perpendicular = perpendicular(point);
        float[] intersection = intersection(perpendicular);
        float[] reflection = new float[2]; //intersection - (point - intersection).
        reflection[0] = 2 * intersection[0] - point[0];
        reflection[1] = 2 * intersection[1] - point[1];
        return reflection;
    }
}
