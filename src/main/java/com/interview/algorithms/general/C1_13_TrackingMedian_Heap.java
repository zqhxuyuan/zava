package com.interview.algorithms.general;

import com.interview.basics.model.collection.heap.BinaryArrayHeap;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/1/14
 * Time: 1:17 PM
 *
 * Numbers are randomly generated and stored in an array. How would you keep track of the median.
 *
 */
public class C1_13_TrackingMedian_Heap {
    int median = 0;
    int size = 0;

    BinaryArrayHeap<Integer> rightMinHeap = new BinaryArrayHeap<Integer>();
    BinaryArrayHeap<Integer> leftMaxHeap = new BinaryArrayHeap<Integer>();

    public void add(int number){
        if(size > 0){
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
        } else {
            median = number;
        }
        size ++;
    }

    public int median(){
        return median;
    }
}
