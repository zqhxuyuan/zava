package com.interview.books.topcoder.geometry;

import com.interview.leetcode.utils.Point;

import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created_By: stefanie
 * Date: 15-1-12
 * Time: 上午11:28
 */
public class TC_G14_ClosestPair {
    static Comparator<Point> HORIZONTAL_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            if(o1.x == o2.x) return o1.y - o2.y;
            else return o1.x - o2.x;
        }
    };

    static Comparator<Point> VERTICAL_COMPARATOR = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            if(o1.y == o2.y) return o1.x - o2.x;
            else return o1.y - o2.y;
        }
    };

    public Point[] closest(Point[] points){
        Point[] closestPair = new Point[2];

        double minDistance = 1000.0;
        Arrays.sort(points, HORIZONTAL_COMPARATOR);

        //When we start the left most candidate is the first one
        int mostLeft = 0;

        //Vertically sorted set of candidates
        SortedSet<Point> candidates = new TreeSet(VERTICAL_COMPARATOR);

        //For each point from left to right
        for (Point current : points){

            //Shrink the candidates based on X axis distance
            while (current.x - points[mostLeft].x > minDistance) {
                candidates.remove(points[mostLeft]);
                mostLeft++;
            }

            //Shrink the searching area as the rectangle by Y axis distance
            Point upper = new Point(current.x, (int) (Math.ceil(current.y - minDistance)));
            Point lower = new Point(current.x, (int) (Math.floor(current.y + minDistance)));

            //We take only the interesting candidates in the y axis
            for (Point point : candidates.subSet(upper, lower)) {
                double distance = distance(current, point);

                //Simple min computation
                if (distance < minDistance) {
                    minDistance = distance;

                    closestPair[0] = current;
                    closestPair[1] = point;
                }
            }

            //The current point is now a candidate
            candidates.add(current);
        }

        return closestPair;
    }

    public double distance(Point p1, Point p2){
        int x = p1.x - p2.x;
        int y = p1.y - p2.y;
        return Math.sqrt(x * x + y * y);
    }

    public static void main(String[] args){
        TC_G14_ClosestPair finder = new TC_G14_ClosestPair();
        Point[] points = new Point[6];
        points[0] = new Point(1,2);
        points[1] = new Point(3,1);
        points[2] = new Point(4,2);
        points[3] = new Point(5,2);
        points[4] = new Point(6,4);
        points[5] = new Point(7,3);

        Point[] closest = finder.closest(points);
        System.out.println(closest[0].x + ", " + closest[0].y); //4,2
        System.out.println(closest[1].x + ", " + closest[1].y); //5,2
    }
}
