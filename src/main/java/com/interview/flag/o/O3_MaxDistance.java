package com.interview.flag.o;

/**
 * Created_By: stefanie
 * Date: 14-12-5
 * Time: 上午10:39
 *
 * http://leetcode.com/2011/05/a-distance-maximizing-problem.html
 *
 * Generally, we want to choose only starting points with no such lines that are shorter to its left side.
 */
public class O3_MaxDistance {
    public static int maxDistance(int[] array) {
        boolean[] isLeftMin = new boolean[array.length];
        int min = 0;
        isLeftMin[0] = true;
        for(int i = 1; i < array.length; i++){
            if(array[i] < array[min]){
                isLeftMin[i] = true;
                min = i;
            }
        }

        int max = 0;
        int back = array.length - 1;
        int front = min;
        while(front >= 0 && back >= 0){
            if(array[front] < array[back]){
                max = Math.max(max, back - front);
                do front--;
                while(front >= 0 && !isLeftMin[front]);
            } else {
                back--;
            }
        }
        return max;
    }

    public static void main(String[] args){
        int[] array = new int[]{4,3,5,2,1,3,2,3};
        System.out.println(maxDistance(array));  //4
    }
}
