package com.interview.books.ninechapter;

import com.interview.leetcode.utils.Point;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 14-12-12
 * Time: 下午3:55
 */
public class NC9_MinDistancePoint {
    public Point minDistance(Point[] points){
        Point center = new Point(0, 0);
        Comparator<Point> xComparator = new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return o1.x - o2.x;
            }
        };
        Arrays.sort(points, xComparator);
        if(points.length % 2 == 1){
            center.x = points[points.length/2].x;
        } else {
            center.x = (points[points.length/2 - 1].x + points[points.length/2].x)/2;
        }

        Comparator<Point> yComparator = new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return o1.y - o2.y;
            }
        };
        Arrays.sort(points, yComparator);
        if(points.length % 2 == 1){
            center.y = points[points.length/2].y;
        } else {
            center.y = (points[points.length/2 - 1].y + points[points.length/2].y)/2;
        }
        return center;
    }

    class DistancePoint extends Point implements Comparable<DistancePoint>{
        int distance = 0;
        public DistancePoint(int x, int y, Point[] points) {
            super(x, y);
            distance(points);
        }
        private void distance(Point[] points){
            for(Point point : points){   //|x0-x1| + |y0-y1|.
               distance += Math.abs(x - point.x) + Math.abs(y - point.y);
            }
        }
        @Override
        public int compareTo(DistancePoint o) {
            return distance - o.distance;
        }
    }

    public Point minDistanceNoOverlap(Point[] points){
        Set<String> set = new HashSet<>();
        for(Point point : points) set.add(point.x + "-" +point.y);

        PriorityQueue<DistancePoint> heap = new PriorityQueue<>();
        Point center = minDistance(points);
        while(set.contains(center.x + "-" + center.y)){
            heap.add(new DistancePoint(center.x + 1, center.y, points));
            heap.add(new DistancePoint(center.x - 1, center.y, points));
            heap.add(new DistancePoint(center.x, center.y + 1, points));
            heap.add(new DistancePoint(center.x, center.y - 1, points));
            center = heap.poll();
        }
        return center;
    }

    public static void main(String[] args){
        NC9_MinDistancePoint finder = new NC9_MinDistancePoint();
        Point[] points = new Point[4];
        points[0] = new Point(1, 2);
        points[1] = new Point(2, 3);
        points[2] = new Point(3, 1);
        points[3] = new Point(1, 4);
        Point center = finder.minDistance(points);
        System.out.println(center.x + ", " + center.y);  //(1,2)

        center = finder.minDistanceNoOverlap(points);
        System.out.println(center.x + ", " + center.y);  //(2,2)
    }

}
