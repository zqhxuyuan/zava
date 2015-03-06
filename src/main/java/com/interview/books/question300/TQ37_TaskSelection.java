package com.interview.books.question300;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-9-20
 * Time: 下午7:08
 *
 * There is N task, each one need use a un-shared resource. Given the start time and end time of each task, it will use
 * the resource in duration of [start-time, end-time), write code to select the maximal set of un-conflict tasks.
 * Assume the input data about the tasks are sorted by end-time
 *
 * Greedy Assumption:
 *   The earlier ended task always in the maximal set of un-conflict tasks.
 *
 */
public class TQ37_TaskSelection {

    public static List<Integer> select(int[] start, int[] end){
        List<Integer> tasks = new ArrayList<>();

        int time = end[0];
        tasks.add(1);

        for(int i = 1; i < end.length; i++) {
            if(start[i] >= time){
                tasks.add(i + 1);
                time = end[i];
            }
        }

        return tasks;
    }

}
