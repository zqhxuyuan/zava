package com.interview.books.question300;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 下午5:04
 */
public class TQ11_MatchRanking {
    public int[] getRank(int[][] scores, int[] order){
        int[] rank = new int[order.length];

        int step = order.length;
        while(step > 1){
            for(int k = 0; k < order.length; k += step){
                for(int i = 0; i < step; i += 2){
                    int first = order[k + i];
                    int second = order[k + i + 1];
                    int winner = scores[first][second];
                    int loser = winner == first? second : first;
                    rank[k + i/2] = winner;
                    rank[k + i/2 + step/2] = loser;
                }
            }
            step /= 2;
            order = rank;
        }

        return rank;
    }

    public static void main(String[] args){
        TQ11_MatchRanking ranker = new TQ11_MatchRanking();
        /**
         *      0       0       2       0
         *      0       1       1       1
         *      2       1       2       2
         *      0       1       2       3
         *
         */
        int[][] w = new int[][] {{0, 0, 2, 0}, {0, 1, 1, 3}, {2, 1, 2, 2}, {0, 3, 2, 3}};

        int[] order = new int[] {0, 1, 2, 3};
        int[] rank = ranker.getRank(w, order);
        ConsoleWriter.printIntArray(rank);   //{2, 0, 1, 3};

        order = new int[] {3, 0, 2, 1};
        rank = ranker.getRank(w, order);
        ConsoleWriter.printIntArray(rank);  //0, 1, 2, 3
    }
}
