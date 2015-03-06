package com.interview.utils.models;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/24/14
 * Time: 3:44 PM
 */
public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "(" + x + ", " + y + ")";
    }

    public boolean equals(Point obj) {
        return x == obj.x && y == obj.y;
    }

    public static double product(Point a, Point b, Point c){
        return (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y);
    }

    public static double length(Point a, Point b){
        return Math.sqrt(Math.pow(b.y - a.y, 2) + Math.pow(b.x - a.x, 2));
    }
}
