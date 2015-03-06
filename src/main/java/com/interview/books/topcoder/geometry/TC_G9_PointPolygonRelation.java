package com.interview.books.topcoder.geometry;

import com.interview.basics.model.geometry.Line;
import com.interview.utils.FloatAssertion;
import com.interview.utils.GeoUtil;

/**
 * Created_By: stefanie
 * Date: 15-1-5
 * Time: 下午4:02
 */
public class TC_G9_PointPolygonRelation {
    public static final String INTERIOR = "INTERIOR";
    public static final String EXTERIOR = "EXTERIOR";
    public static final String BOUNDARY = "BOUNDARY";

    public String relation(float[][] polygon, float[] X){
        int len = polygon.length;
        for(int i = 0; i < polygon.length; i++){
            float cross = GeoUtil.crossProduct(polygon[(i) % len], polygon[(i + 1) % len], X);
            int cmp = FloatAssertion.compareTo(cross, 0.0);
            if(cmp == 0) {
                Line line = new Line(polygon[(i) % len], polygon[(i + 1) % len]);
                if(line.onLine(X))  return BOUNDARY;
            }
            if(cmp > 0) return EXTERIOR;
        }
        return INTERIOR;
    }

    public static void main(String[] args){
        TC_G9_PointPolygonRelation checker = new TC_G9_PointPolygonRelation();
        float[][] polygon = new float[][]{
                {0, 0},
                {0, 10},
                {10,10},
                {6, 6},
                {10, 0}
        };
        System.out.println(checker.relation(polygon, new float[]{5,10})); //BOUNDARY
        System.out.println(checker.relation(polygon, new float[]{10,15}));//EXTERIOR
        System.out.println(checker.relation(polygon, new float[]{5,5}));  //INTERIOR
        System.out.println(checker.relation(polygon, new float[]{10,5}));  //EXTERIOR
    }
}
