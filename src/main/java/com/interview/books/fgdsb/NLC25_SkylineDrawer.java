package com.interview.books.fgdsb;

import com.interview.utils.models.Point;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 15-2-2
 * Time: 下午4:12
 */
public class NLC25_SkylineDrawer {
    static class Building{
        int left;
        int right;
        int height;
        public Building(int left, int right, int height){
            this.left = left;
            this.right = right;
            this.height = height;
        }
    }

    Comparator<Building> sortComparator = new Comparator<Building>() {
        @Override
        public int compare(Building o1, Building o2) {
            if(o1.left == o2.left) return o1.right - o2.right;
            else return o1.left - o2.left;
        }
    };

    Comparator<Building> heapComparator = new Comparator<Building>() {
        @Override
        public int compare(Building o1, Building o2) {
            if(o1.right == o2.right) return o2.height - o1.height;
            return o1.right - o2.right;
        }
    };

    public List<Point> draw(Building[] buildings){
        Arrays.sort(buildings, sortComparator);
        PriorityQueue<Building> heap = new PriorityQueue(buildings.length, heapComparator);

        List<Point> points = new ArrayList();
        int maxHeight = 0;
        for(int i = 0; i <= buildings.length; i++){
            int bound = i < buildings.length? buildings[i].left : Integer.MAX_VALUE;
            while(!heap.isEmpty() && heap.peek().right <= bound){
                Building pre = heap.poll();
                if(pre.height == maxHeight) {
                    points.add(new Point(pre.right, maxHeight));
                    maxHeight = heap.isEmpty()? 0 : heap.peek().height;
                    points.add(new Point(pre.right, maxHeight));
                }
            }
            if(i < buildings.length){
                Building current = buildings[i];
                if(current.height > maxHeight){
                    points.add(new Point(current.left, maxHeight));
                    maxHeight = current.height;
                    points.add(new Point(current.left, maxHeight));
                }
                heap.add(current);
            }
        }
        return points;
    }

    public static void main(String[] args){
        NLC25_SkylineDrawer drawer = new NLC25_SkylineDrawer();
        Building[] buildings = new Building[4];
        buildings[0] = new Building(0,10,5);
        buildings[1] = new Building(5,15,10);
        buildings[2] = new Building(7,12,8);
        buildings[3] = new Building(11,20,3);
        List<Point> points = drawer.draw(buildings);
        for(Point point : points) System.out.print(point.toString() + ", ");
        //(0, 0), (0, 5), (5, 5), (5, 10), (15, 10), (15, 3), (20, 3), (20, 0)

    }
}
