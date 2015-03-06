package com.interview.books.topcoder.geometry;

import com.interview.leetcode.utils.Point;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 15-1-12
 * Time: 下午2:39
 */
public class TC_G15_LineSegmentIntersection_Aligned {
    static class Line{
        Point[] points = new Point[2];

        public Line(Point p1, Point p2){
            if(p1.x != p2.x){
                points[0] = p1.x <= p2.x? p1 : p2;
                points[1] = p1.x <= p2.x? p2 : p1;
            } else {
                points[0] = p1.y <= p2.y? p1 : p2;
                points[1] = p1.y <= p2.y? p2 : p1;
            }
        }

        public boolean isVertical(){
            if(points[0].x == points[1].x) return true;
            else return false;
        }
    }

    static Comparator<Line> START_HORIZONTAL_COMPARATOR = new Comparator<Line>() {
        @Override
        public int compare(Line o1, Line o2) {
            if(o1.points[0].x == o2.points[0].x){
                return o1.isVertical()? 1 : -1;
            } else {
                return o1.points[0].x - o2.points[0].x;
            }
        }
    };

    static Comparator<Line> END_HORIZONTAL_COMPARATOR = new Comparator<Line>() {
        @Override
        public int compare(Line o1, Line o2) {
            return o1.points[1].x - o2.points[1].x;
        }
    };


    public List<Point> intersection(Line[] lines){
        List<Point> intersections = new ArrayList();
        Arrays.sort(lines, START_HORIZONTAL_COMPARATOR);
        PriorityQueue<Line> horizonals = new PriorityQueue(lines.length, END_HORIZONTAL_COMPARATOR);
        SortedSet<Integer> verticals = new TreeSet();


        for(int i = 0; i < lines.length; i++){
            Line current = lines[i];
            while(!horizonals.isEmpty() && horizonals.peek().points[1].x < current.points[0].x){
                Line line = horizonals.poll();
                verticals.remove(line.points[0].y);
            }
            if(current.isVertical()){
                for(Integer point : verticals.subSet(current.points[0].y, current.points[1].y)){
                    intersections.add(new Point(current.points[0].x, point));
                }
            } else {
                verticals.add(current.points[0].y);
                horizonals.add(current);
            }
        }
        return intersections;
    }

    public static void main(String[] args){
        TC_G15_LineSegmentIntersection_Aligned finder = new TC_G15_LineSegmentIntersection_Aligned();
        Line[] lines = new Line[7];
        lines[0] = new Line(new Point(0,2), new Point(2,2));
        lines[1] = new Line(new Point(1,1), new Point(1,3));
        lines[2] = new Line(new Point(3,4), new Point(5,4));
        lines[3] = new Line(new Point(4,3), new Point(7,3));
        lines[4] = new Line(new Point(4,1), new Point(4,5));
        lines[5] = new Line(new Point(4,7), new Point(5,7));
        lines[6] = new Line(new Point(6,4), new Point(6,6));

        List<Point> intersections = finder.intersection(lines);
        for(Point point : intersections){
            System.out.println(point.x + ", " + point.y); //(1,2),(4,3),(4,4)
        }
    }
}
