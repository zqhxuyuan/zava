package com.interview.algorithms.sort;

import java.util.Arrays;
import java.util.Comparator;

import com.interview.basics.model.collection.stack.LinkedStack;
import com.interview.basics.model.collection.stack.Stack;

/**
 * The convex hull of a set of N points is the smallest perimeter fence enclosing the points.
 * There are 2 facts of the problem:
 * Fact1. Can traverse the convex hull by making only counterclockwise turns.
 * Fact2. The vertices of convex hull appear in increasing order of polar angle 
 * 		  with respect to point p with lowest y-coordinate.
 * 
 * Solution is based on the previous 2 facts:
 * 1. Choose point p with smallest y-coordinate.
 * 2. Sort points by polar angle with p.
 * 3. Consider points in order; discard unless it create a counterclockwise turn.
 * 
 * About counter clockwise:
 * by calculate (b.x-a.x) *(c.y-a.y)- (b.y-a.y)*(c.x-a.x), 
 * 		if the result > 0, they are clockwise, 
 * 		if result < 0, they ain't clockwise, 
 * 		if result = 0, they are in a line  
 * 
 * @author stefaniezhao
 *
 */
class Point {
    public static final Comparator<Point> BY_Y_AXIS = new YAxisComparator();
    public static final Comparator<Point> BY_ANGLE = new AngleComparator();
    private double x;
    private double y;
    private double angle;

    public Point(double x, double y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "(" + x + ", " + y + ")";
    }

    private static class YAxisComparator implements Comparator<Point>{

        @Override
        public int compare(Point p1, Point p2) {
            if(p1.y == p2.y) return 0;
            else if (p1.y > p2.y) return 1;
            else return -1;
        }
    }

    private static class AngleComparator implements Comparator<Point>{

        @Override
        public int compare(Point p1, Point p2) {
            if (p1.angle == p2.angle) return 0;
            else if(p1.angle > p2.angle) return 1;
            else return -1;
        }

    }

    public static int isLower(Point a, Point b){
        if(a.y < b.y) return 1;
        else if (a.y > b.y) return -1;
        else return 0;
    }

    public static void sortByPolarAngle(Point[] points){
        for(int i = 1; i < points.length; i++){
            double dx = points[i].x - points[0].x;
            double dy = points[i].y - points[0].y;
            points[i].angle = Math.atan2(dy,dx);
        }

        Arrays.sort(points, 1, points.length, Point.BY_ANGLE);
//		//selection sorting
//		for(int i = 1; i < points.length; i++){
//			int minIndex = i;
//			for(int j = i + 1; j < points.length; j ++){
//				if(points[j].angle < points[minIndex].angle)
//					minIndex = j;
//			}
//			if(minIndex != i){
//				Point temp = points[i];
//				points[i] = points[minIndex];
//				points[minIndex] = temp;
//			}
//		}
    }

    public static int counterclockwise(Point a, Point b, Point c){
        double area2 = (b.x-a.x) *(c.y-a.y)- (b.y-a.y)*(c.x-a.x);
        if(area2 > 0) return 1;
        else if(area2 < 0) return -1;
        else return 0;
    }

}
public class ConvexHull {
	
	public static Point[] grahamScan(Point[] points){
		Stack<Point> hull = new LinkedStack<Point>();
		
		//1. sort point based on Y-coordinate to find p0.
		//getLowestY(points);
		Arrays.sort(points, Point.BY_Y_AXIS);
		//2. sort pints by polar angle with respect to p0.
		Point.sortByPolarAngle(points);
		
		
//		for(Point p : points){
//			System.out.print(p.toString() + ", ");
//		}
//		System.out.println();
	
		//find the edges
		hull.push(points[0]);
		hull.push(points[1]);
		
		for(int i = 2; i < points.length; i ++){
			Point top = hull.pop();
			//System.out.println(top.toString());
			while(Point.counterclockwise(hull.peek(), top, points[i]) <= 0){
				//System.out.println(top.toString());
				top = hull.pop();
			}
			hull.push(top);
			hull.push(points[i]);
		}
		
		int size = hull.size();
		Point[] edges = new Point[size];
		int index = 0;
		while(!hull.isEmpty()){
			edges[index] = hull.pop();
			index ++;
		}
		return edges;
	}
	
	public static void getLowestY(Point[] points){
		int min = 0;
		for(int i = 1; i < points.length; i ++){
			if(Point.isLower(points[i], points[min]) > 0){
				min = i;
			}
		}
		//swap min and 0
		Point temp = points[0];
		points[0] = points[min];
		points[min] = temp;
	}
	
	
	private static Point[] generateTestPoint() {
		Point[] testPoint = new Point[10];
		//String pointStr = "0,0#1,0.5#1,1#2,1.5#0.5,1.5#1,2#0,2#0,1#-0.5,1";
		//String pointStr = "7,1#0,4#8,8#3,6#5,3#6,5#4,0#9,9#2,7#1,2";
		String pointStr = "8,4#9,2#4,9#1,5#0,6#7,7#5,8#3,1#6,3#2,0";
		String[] points = pointStr.split("#");
		for(int i = 0; i < points.length; i ++){
			String[] coords = points[i].split(",");
			double x = Double.parseDouble(coords[0]);
			double y = Double.parseDouble(coords[1]);
			testPoint[i] = new Point(x, y);
		}
		return testPoint;
	}
	
	public static void main(String[] args){
		Point[] testPoint = generateTestPoint();
		for(Point p : testPoint){
			System.out.print(p.toString() + ", ");
		}
		System.out.println();
		Point[] edges = grahamScan(testPoint);
		for(Point p : edges){
			System.out.print(p.toString() + ", ");
		}
	}

}
