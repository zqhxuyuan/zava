package com.interview.algorithms.general;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 11/4/14
 * Time: 2:19 PM
 */
public class C1_71_MaxProfit {
    public static int find(int[] prices){
        int maxProfit = 0;
        int buyPrice = -1;
        for(int i = 0; i < prices.length - 1; i++){
            if(buyPrice != -1 && prices[i] > prices[i + 1]){
                maxProfit += prices[i] - buyPrice;
                buyPrice = -1;
            } else if(buyPrice == -1 && prices[i] < prices[i + 1]){
                buyPrice = prices[i];
            }
        }
        if(buyPrice != -1) maxProfit += prices[prices.length - 1] - buyPrice;
        return maxProfit;
    }
}
