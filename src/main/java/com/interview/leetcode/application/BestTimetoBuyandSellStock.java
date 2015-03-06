package com.interview.leetcode.application;

/**
 * Created_By: stefanie
 * Date: 14-11-12
 * Time: 下午8:54
 *
 * Say you have an array prices[] for which the ith element is the price of a given stock on day i.
 * Design an algorithm to find the maximum profit.
 *
 * 1: You may complete only one transaction.  {@link #oneTrans(int[])}
 * 2: You may complete multiple transactions. {@link #multiTrans(int[])}
 * 3: You may complete at most two transactions. {@link #oneOrTwoTrans(int[])}
 *
 * From LeetCode
 *      https://oj.leetcode.com/problems/best-time-to-buy-and-sell-stock-i/
 *      https://oj.leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/
 *      https://oj.leetcode.com/problems/best-time-to-buy-and-sell-stock-iii/
 *
 * Tricks:
 *   1. define a simplest and clear calculation method for the variable you interested (profit)
 *   2. transform the problem to a simpler question:
 *          find a break point day i, make one transaction before day i and one after day i.
 *   3. keeping left and right sols, and using left and right to create whole solution.
 *   3. using dynamic programming to solve duplicate cases.
 */
public class BestTimetoBuyandSellStock {

    /**
     * You may complete only one transaction. O(N)
     *
     * Keep tracking min from beginning, at day i, the max profit could get is prices[i] - min.
     *
     */
    public static int oneTrans(int[] prices){
        if(prices.length < 1) return 0;
        int min = prices[0];
        int profit = 0;
        for(int i = 1; i < prices.length; i++){
            if(prices[i] < min) min = prices[i];    //tracking min
            else if(prices[i] - min > profit) profit = prices[i] - min;  //calculate the profit and update max profit
        }
        return profit;
    }

    /**
     *  You may complete multiple transactions.   O(N)
     *
     *  Scan the prices to find a buy time and sell time
     *      buy time: the prices go up in the next day
     *      sell time: the prices go down in the next day
     *  profit is the sum of each transaction
     */
    public static int multiTrans(int[] prices){
        int maxProfit = 0;
        int buyPrice = -1;
        for(int i = 0; i < prices.length - 1; i++){
            if(buyPrice == -1 && prices[i] < prices[i + 1]) {  //found a time to buy
                buyPrice = prices[i];
            } else if(buyPrice != -1 && prices[i] > prices[i + 1]){  //found a time to sell
                maxProfit += prices[i] - buyPrice; //add the profit of this transaction
                buyPrice = -1;
            }
        }
        if(buyPrice != -1) maxProfit += prices[prices.length - 1] - buyPrice;
        return maxProfit;
    }

    /**
     * You may complete at most two transactions.  O(N)
     *
     * We need find a break point to have 2 transactions with max profit.
     *  left[i] = the max profit could make before day i with one transaction
     *          left[i] = Math.max(left[i-1], prices[i] - min)      min is between 0 ~ i
     *  right[i] = the max profit could make after day i with one transaction
     *          right[i] = Math.max(right[i+1], max - prices[i])    max is between length - 1 ~ i
     *  when day i is the break point, profit = left[i] + right[i];
     */
    public static int oneOrTwoTrans(int[] prices){
        if(prices == null || prices.length == 0) return 0;

        int[] left = new int[prices.length];
        int[] right = new int[prices.length];

        left[0] = 0;
        int min = prices[0];
        for(int i = 1; i < prices.length; i++){    //DP on the left part
            if(prices[i] < min) min = prices[i];
            left[i] = Math.max(left[i - 1], prices[i] - min);
        }

        right[prices.length - 1] = 0;
        int max = prices[prices.length - 1];
        for(int i = prices.length - 2; i >= 0; i--){ //DP on the right part
            if(prices[i] > max) max = prices[i];
            right[i] = Math.max(right[i + 1], max - prices[i]);
        }

        int profit = 0;
        for(int i = 0; i < prices.length; i++){    //find the max profit
            profit = Math.max(profit, left[i] + right[i]);
        }
        return profit;
    }
}
