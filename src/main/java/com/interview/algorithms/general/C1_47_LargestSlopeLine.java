package com.interview.algorithms.general;

import com.interview.utils.models.Point;

/**
 * Created_By: stefanie
 * Date: 14-8-21
 * Time: 上午9:53
 */
public class C1_47_LargestSlopeLine {

    public static Point[] find(Point[] points){
        //TODO: HAVEN'T FIGURE OUT
        return null;
    }

    public static Point[] findBF(Point[] points){
        Point[] line = new Point[2];
        float maxSlope = 0.0f;
        for(int i = 0; i < points.length; i++){
            for(int j = i + 1; j < points.length; j++){
                if(points[i].x - points[j].x == 0) continue;
                float slope = Math.abs((points[i].y - points[j].y + 0.0f)/(points[i].x - points[j].x));
                if(slope > maxSlope){
                    maxSlope = slope;
                    line[0] = points[i];
                    line[1] = points[j];
                }
            }
        }
        return line;
    }
}
