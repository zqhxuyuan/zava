package com.interview.books.geeksforgeeks;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 15-1-8
 * Time: 下午2:47
 */
public class GFG1_CoinsInLine {

//    money[i][j] = the max money I can get in the coin sequence num[i] ~ num[j]
//    initialize:
//          money[i][i] = coins[i];
//          money[i][i+1] == max(coins[i], coins[i+1]);
//    function: money[i][j] = max of
//          num[i] + min(money[i+2][j], money[i+1][j-1])
//          num[j] + min(money[i+1][j-1], money[i][j-2])
//          same as palindrome, need loop on len and start point
//    result: money[0][num.length-1]

    public int[][] max(int[] coins){
        int[][] money = new int[coins.length][coins.length];
        for(int i = 0; i+1 < coins.length; i++){
            money[i][i] = coins[i];
            money[i][i+1] = Math.max(coins[i], coins[i+1]);
        }
        for(int len = 2; len < coins.length; len++){
            for(int i = 0; i + len < coins.length; i++){
                int j = i + len;
                money[i][j] = Math.max(
                    coins[i] + Math.min(money[i+2][j], money[i+1][j-1]),
                    coins[j] + Math.min(money[i+1][j-1], money[i][j-2])
                );
            }
        }
        //return money[0][coins.length - 1];
        return turns(money, coins);
    }

    public int[][] turns(int[][] money, int[] coins){
        int[] first  = new int[coins.length/2];
        int[] second = new int[coins.length/2];
        int front = 0;
        int back = coins.length - 1;
        int[] current = first;
        int turn = 0;
        while(front < back){
            if(money[front + 1][back] < money[front][back - 1]){
                current[turn/2] = coins[front];
                front++;
            } else {
                current[turn/2] = coins[back];
                back--;
            }
            turn++;
            current = current == first? second : first;
        }
        current[turn/2] = coins[front];
        return new int[][]{first, second};
    }

    public static void main(String[] args){
        GFG1_CoinsInLine game = new GFG1_CoinsInLine();
        int[] coins = new int[]{5, 3, 7, 10};
        //System.out.println(game.max(coins));//15
        int[][] turns = game.max(coins);
        for(int i = 0; i < turns.length; i++)
            ConsoleWriter.printIntArray(turns[i]);
        coins = new int[]{8, 15, 3, 7};
        //System.out.println(game.max(coins));//22
        turns = game.max(coins);
        for(int i = 0; i < turns.length; i++)
            ConsoleWriter.printIntArray(turns[i]);

    }
}
