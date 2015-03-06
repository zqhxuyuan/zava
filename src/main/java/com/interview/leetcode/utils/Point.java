package com.interview.leetcode.utils;

/**
 * Created_By: stefanie
 * Date: 14-11-15
 * Time: 下午4:47
 */
public class Point {
    public int x;
    public int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Point)) return false;
        Point point = (Point) obj;
        if(point.x == x && point.y == y) return true;
        else return false;
    }

    public float[] getFloat(){
        return new float[]{x, y};
    }
}
