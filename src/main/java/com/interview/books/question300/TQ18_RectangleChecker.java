package com.interview.books.question300;

import com.interview.leetcode.utils.Point;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 下午10:50
 */
public class TQ18_RectangleChecker {

    public static boolean isRectangle(Point[] points){
        /**
         * Sort the Rectangle to be
         *      0       1               (0-1) vertical to (0-2)
         *      2       3               (3-1) vertical to (3-2)
         */
        Arrays.sort(points, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                if (o1.x == o2.x) {
                    if (o1.y == o2.y) return 0;
                    else if (o1.y > o2.y) return -1;
                    else return 1;
                } else if (o1.x > o2.x) return -1;
                else return 1;
            }
        });

        if(points[0].x == points[1].x){    //vertical to x-axis and y-axis
            if(points[0].y != points[1].y && points[2].x == points[3].x
                    && points[0].y == points[2].y && points[1].y == points[3].y) return true;
            return false;
        } else {    //check slope, when vertical, the slope product is -1
            if(isVertical(points, 0, 1, 2) && isVertical(points, 3, 1, 2))    return true;
            else return false;
        }
    }

    public static boolean isVertical(Point[] p, int i, int j, int k){
        return ((p[i].y - p[j].y) * (p[k].y - p[i].y))/((p[i].x - p[j].x) * p[k].x - p[i].x) == -1;
    }
}
