package com.interview.books.question300;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/17/14
 * Time: 2:30 PM
 */
public class TQ65_IncreasingSubArray {

    public int find(int[] array){
        int count = 0;
        int[] counts = new int[array.length];
        counts[0] = 0;
        for(int i = 1; i < array.length; i++){
            for(int j = 0; j < i; j++){
                if(array[j] < array[i]) counts[i] += counts[j] + 1;
            }
            count += counts[i];
        }
        return count;
    }

    public static void main(String[] args){
        TQ65_IncreasingSubArray finder = new TQ65_IncreasingSubArray();
        System.out.println(finder.find(new int[]{1,2,3})); //4: {1,2},{1,3},{2,3},{1,2,3}
        System.out.println(finder.find(new int[]{2,1,3})); //2: {1,3},{2,3}
        System.out.println(finder.find(new int[]{1,3,2})); //2: {1,3},{1,2}
    }

}
