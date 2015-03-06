package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 15-1-25
 * Time: 下午6:22
 */
public class TQ64_MinDistanceCyclicArray {
    int[] sum;

    public TQ64_MinDistanceCyclicArray(int[] distance){
        int N = distance.length;
        sum = new int[N + 1];
        sum[0] = 0;
        for(int i = 1; i < N; i++) sum[i] = sum[i - 1] + distance[i];
        sum[N] = sum[N - 1] + distance[0];
    }

    public int distance(int i, int j){
        if(i == j) return 0;
        else if(i > j) return distance(j, i);
        int forward = sum[j] - sum[i];
        int backward = sum[sum.length - 1] - sum[j] + sum[i];
        return Math.min(forward, backward);
    }

    public static void main(String[] args){
        int[] array = new int[]{6,1,2,3,4,5};
        TQ64_MinDistanceCyclicArray calculator = new TQ64_MinDistanceCyclicArray(array);

        System.out.println(calculator.distance(2,4));//7
        System.out.println(calculator.distance(1,5));//7
    }
}
