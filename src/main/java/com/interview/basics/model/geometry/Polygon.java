package com.interview.basics.model.geometry;

import com.interview.utils.GeoUtil;

/**
 * Created_By: stefanie
 * Date: 15-1-4
 * Time: 下午8:24
 */
public class Polygon {
    float[][] points;

    public Polygon(float[][] points){
        this.points = points;
    }

    public float area(){
        float area = 0;
        for(int i = 1; i < points.length - 1; i++){
            area += GeoUtil.crossProduct(points[0], points[i], points[i + 1]);
        }
        return Math.abs(area/2);
    }

    public static void main(String[] args){
        float[][] points = new float[][]{
                {100,0}, {80,58},  {30,95},  {-30,95}, {-80, 58},
                {-100,0},{-80,-58},{-30,-95},{30, -95},{80,-58}
        };
        Polygon polygon = new Polygon(points);
        System.out.println(polygon.area());//29020.0
    }
}
