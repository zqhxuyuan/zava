package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-7-25
 * Time: 下午11:31
 */
public class C1_38_MatchRanking {

    public static int[] result(int[][] w, int[] order){
        int[] rank = new int[order.length];

        int step = 1;
        int index = order.length - 1;
        while(step < order.length){
            for(int i = 0; i + step < order.length; i = i+2*step){
               int winner = w[order[i]][order[i+step]];
               rank[index] = winner == order[i]? order[i+step] : order[i];
               order[i] = winner;
               index--;
            }
            step = step * 2;
        }
        rank[0] = order[0];
        return rank;
    }
}
