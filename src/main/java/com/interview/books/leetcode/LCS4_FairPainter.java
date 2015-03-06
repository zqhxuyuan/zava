package com.interview.books.leetcode;

import com.interview.utils.ArrayUtil;
import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 上午9:27
 */
public class LCS4_FairPainter {
    //time[i][k] is the min time paint walls from i using K painter.
    //initialize: time[i][1] = sum(walls[walls.length - 1] - walls[i]
    //function: time[i][k] = min(for j > i and j < walls.length: max(sum(walls[i][j], time[j+1][k-1])
    //result: time[0][K]
    //Time: O(KN^2) Space: O(KN)
    public int minTime(int[] walls, int K) {
        if (walls.length == 0) return 0;
        int[][] time = new int[walls.length][K + 1];
        //init
        int sum = 0;
        for (int i = walls.length - 1; i >= 0; i--) {
            sum += walls[i];
            time[i][1] = sum;
        }
        //function
        for (int k = 2; k <= K; k++) {
            for (int i = 0; i < walls.length; i++) {
                sum = 0;
                time[i][k] = Integer.MAX_VALUE;
                for (int j = i; j < walls.length; j++) {
                    sum += walls[j];
                    int cost = sum;
                    if (j < walls.length - 1) cost = Math.max(cost, time[j + 1][k - 1]);
                    time[i][k] = Math.min(time[i][k], cost);
                }
            }
        }
        ConsoleWriter.printIntArray(time);
        //result
        return time[0][K];
    }

    public static void main(String[] args){
        LCS4_FairPainter painter = new LCS4_FairPainter();
        int[] walls = new int[]{1,2,4,5,6,3,1,2,7,8,2,5};
        System.out.println(ArrayUtil.sum(walls, 0, walls.length - 1));
        System.out.println(painter.minTime(walls, 4));
    }
}
