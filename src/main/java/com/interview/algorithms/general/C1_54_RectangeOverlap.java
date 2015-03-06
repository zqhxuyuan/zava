package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-9-7
 * Time: 下午3:00
 */


class Rectangle {
    /**
     *  (x1,y1), (x2,y2) is the left-up and right-down corner points
     */
    int x1;
    int y1;
    int x2;
    int y2;

    Rectangle(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
}
public class C1_54_RectangeOverlap {
    public static boolean hasOverlap(Rectangle r1, Rectangle r2){
        return !(r1.x1 > r2.x2 || r1.x2 < r2.x1 || r1.y1 < r2.y2 || r1.y2 > r2.y1);
    }
}
