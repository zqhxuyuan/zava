package com.interview.flag.g;

import com.interview.leetcode.utils.Point;

import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 14-12-31
 * Time: 下午9:12
 */
public class G13_ChargeRobot {
    class DistancePoint extends Point implements Comparable<DistancePoint>{
        int distance;

        public DistancePoint(int x, int y, Point[] points) {
            super(x, y);
            for(Point point : points){   //|x0-x1| + |y0-y1|.
                distance += Math.abs(x - point.x) + Math.abs(y - point.y);
            }
        }

        @Override
        public int compareTo(DistancePoint o) {
            return distance - o.distance;
        }
    }

    public Point getPosition(Point[] machines){
        Set<String> set = new HashSet<>();
        for(int i = 0; i < machines.length; i++)
            set.add(machines[i].x + "-" + machines[i].y);
        Point center = minDistancePoint(machines);
        PriorityQueue<DistancePoint> heap = new PriorityQueue<>();
        while(set.contains(center.x + "-" + center.y)){
            heap.add(new DistancePoint(center.x + 1, center.y, machines));
            heap.add(new DistancePoint(center.x - 1, center.y, machines));
            heap.add(new DistancePoint(center.x, center.y + 1, machines));
            heap.add(new DistancePoint(center.x, center.y - 1, machines));
            center = heap.poll();
        }
        return center;
    }

    private Point minDistancePoint(Point[] machines){
        int[] xAxis = new int[machines.length];
        int[] yAxis = new int[machines.length];
        for(int i = 0; i < machines.length; i++){
            xAxis[i] = machines[i].x;
            yAxis[i] = machines[i].y;
        }
        return new Point(getMedian(xAxis), getMedian(yAxis));
    }

    private int getMedian(int[] numbers){
        Arrays.sort(numbers);
        if(numbers.length % 2 == 1){
            return numbers[numbers.length / 2];
        } else {
            return (numbers[numbers.length / 2 - 1] + numbers[numbers.length/2])/2;
        }
    }

    public static void main(String[] args){
        Point[] machines = new Point[4];
        machines[0] = new Point(1, 2);
        machines[1] = new Point(2, 3);
        machines[2] = new Point(3, 1);
        machines[3] = new Point(1, 4);
        G13_ChargeRobot placer = new G13_ChargeRobot();
        Point position = placer.getPosition(machines);
        System.out.println(position.x + "-" + position.y);  //2-2
    }
}
