package com.interview.books.ccinterview;


import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/1/14
 * Time: 1:17 PM
 *
 * Numbers are randomly generated and stored in an array. How would you keep track of the median.
 *
 */
public class CC36_TrackingMedian {
    int median = 0;
    int size = 0;
    Comparator<Integer> maxComparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    };

    PriorityQueue<Integer> rightMinHeap = new PriorityQueue<Integer>(10);
    PriorityQueue<Integer> leftMaxHeap = new PriorityQueue<Integer>(10, maxComparator);

    public void add(int number){
        if(size == 0) median = number;
        else {
            if(number < median){ //put in left max heap
                if(rightMinHeap.size() - leftMaxHeap.size() >= 0){
                    leftMaxHeap.add(number);
                } else {
                    rightMinHeap.add(median);
                    median = number;
                }
            } else {
                if(leftMaxHeap.size() - rightMinHeap.size() >= 0){
                    rightMinHeap.add(number);
                } else {
                    leftMaxHeap.add(median);
                    median = number;
                }
            }
        }
        size++;
    }

    public int median(){
        return median;
    }


    public static void main(String[] args){
        CC36_TrackingMedian tracker = new CC36_TrackingMedian();
        tracker.add(1);
        System.out.println(tracker.median());   //1

        tracker.add(10);
        System.out.println(tracker.median());   //1

        tracker.add(5);
        System.out.println(tracker.median());   //5

        tracker.add(2);
        tracker.add(3); //1,2,3,5,10
        System.out.println(tracker.median());   //3
    }
}
