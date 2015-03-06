package com.interview.books.question300;

import com.interview.leetcode.utils.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/11/14
 * Time: 5:12 PM
 */

public class TQ9_AmicableNumbersFinder {

    public static List<Pair<Integer>> find(int range){
        List<Pair<Integer>> numbers = new ArrayList<>();

        int N = range + 1;
        int[] sum = new int[N];

        for(int i = 0; i < N; i++) sum[i] = 1;

        for(int i = 2; i < N / 2; i++){
            for(int j = i + i; j < N; j += i){
                sum[j]+= i;
            }
        }

        for(int i = 1; i < N; i++){
           if(sum[i] < N && sum[i] > i && i == sum[sum[i]])
                numbers.add(new Pair<>(i, sum[i]));
        }

        return numbers;
    }

    public static void main(String[] args){
        List<Pair<Integer>> pairs = TQ9_AmicableNumbersFinder.find(10000);
        for(Pair<Integer> pair : pairs){
            System.out.println(pair.x + ", " + pair.y);
        }
    }
}
