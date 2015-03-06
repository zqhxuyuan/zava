package com.interview.algorithms.general;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created_By: stefanie
 * Date: 14-8-26
 * Time: 下午5:05
 */

public class C1_50_RectangleChecker {
    static class Point{
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public static Point[] getPoints(int[][] points){
            Point[] pointArray = new Point[points.length];
            for(int i = 0; i < points.length; i++) pointArray[i] = new Point(points[i][0], points[i][1]);
            return pointArray;
        }
    }

    public static boolean isRectangle(int[][] points){
        Point[] p = Point.getPoints(points);
        Arrays.sort(p, new Comparator<Point>(){
            @Override
            public int compare(Point o1, Point o2) {
                if(o1.x == o2.x){
                    if(o1.y == o2.y)    return 0;
                    else if(o1.y > o2.y)   return -1;
                    else return 1;
                }
                else if(o1.x > o2.x) return -1;
                else return 1;
            }
        });

        if(p[0].x == p[1].x){    //vertical to x-axis and y-axis
            if(p[0].y != p[1].y && p[2].x == p[3].x && p[0].y == p[2].y && p[1].y == p[3].y) return true;
            return false;
        } else {    //check slope, when vertical, the slope product is -1
            if(isVertical(p, 0, 1, 2) && isVertical(p, 3, 1, 2))    return true;
            else return false;
        }
    }

    public static boolean isVertical(Point[] p, int i, int j, int k){
        return ((p[i].y - p[j].y) * (p[k].y - p[i].y))/((p[i].x - p[j].x) * p[k].x - p[i].x) == -1;
    }


}
