package com.interview.books.svinterview;

import com.interview.utils.ConsoleWriter;
import com.interview.utils.DataGenerator;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午4:21
 */
public class SV14_MinCostPrinting {
    public int minCostGreedy(int h, int[] color){
        if(h <= 0 || color.length < 2) return 0;
        Arrays.sort(color);
        int cost = h / 2 * color[1];
        cost += (h - (h / 2)) * color[0];
        return cost;
    }
    public int minCostDP(int h, int[] color){
        if(h <= 0 || color.length < 2) return 0;
        int[][] cost = new int[h][color.length];
        //init
        for(int j = 0; j < color.length; j++) cost[0][j] = color[j];
        //function
        for(int i = 1; i < h; i++){
            for(int j = 0; j < color.length; j++){
                int min = Integer.MAX_VALUE;
                for(int k = 0; k < color.length; k++){
                    if(j == k) continue;
                    min = Math.min(min, cost[i-1][k]);
                }
                cost[i][j] = min + color[j];
            }
        }
        ConsoleWriter.printIntArray(cost);
        int min = Integer.MAX_VALUE;
        for(int j = 0; j < color.length; j++){
            min = Math.min(min, cost[h-1][j]);
        }
        return min;
    }

    public static void main(String[] args){
        int[] colors = DataGenerator.generateIntArray(4, 10, 1, false);
        ConsoleWriter.printIntArray(colors);
        SV14_MinCostPrinting planner = new SV14_MinCostPrinting();
        System.out.println(planner.minCostDP(5, colors));
        System.out.println(planner.minCostGreedy(5, colors));
    }
}
