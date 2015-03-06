package com.interview.books.leetcode;

import com.interview.leetcode.utils.Point;
import com.interview.utils.GeoUtil;

/**
 * Created_By: stefanie
 * Date: 14-12-10
 * Time: 下午10:12
 */
public class LCS2_PointInRectangle {

    public boolean isInRectangle(Point[] points, Point target){
        int len = points.length;
        float[][] pointArray = new float[2][points.length];
        for(int i = 0; i < points.length; i++) pointArray[i] = points[i].getFloat();

        float[] targetPoint = target.getFloat();
        for(int i = 0; i < points.length; i++){
            float cross = GeoUtil.crossProduct(pointArray[(i) % len], pointArray[(i + 1) % len], targetPoint);
            if(cross > 0) return false;
        }
        return true;
    }

    public boolean isInAlignedRectangle(Point[] points, Point target){
        return !(target.x < points[0].x || target.x > points[1].x || target.y > points[0].y || target.y < points[1].y);
    }
}
