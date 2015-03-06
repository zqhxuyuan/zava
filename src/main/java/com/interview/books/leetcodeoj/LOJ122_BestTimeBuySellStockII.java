package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午8:16
 */
public class LOJ122_BestTimeBuySellStockII {
    //find the buy point and sell point
    //buy point is i:  prices[i] <= prices[i-1] && prices[i] < prices[i+1];
    //sell point is i: prices[i] >= prices[i-1] && prices[i] > prices[i+1];
    //profit += prices[sell] - prices[buy];
    //treat i == 0 and i == prices.length - 1 separately
    //if(prices[0] < prices[1]) buy = 0;
    //if(buy != -1) profit += prices[prices.length - 1] - prices[buy];
    public int maxProfit(int[] prices) {
        if(prices.length <= 1) return 0;
        int profit = 0;
        int buy = -1;
        if(prices[0] < prices[1]) buy = 0;
        for(int i = 1; i < prices.length - 1; i++){
            if(buy == -1 && prices[i] <= prices[i-1] && prices[i] < prices[i+1]) buy = i;
            else if(buy != -1 && prices[i] >= prices[i-1] && prices[i] > prices[i+1]){
                profit += prices[i] - prices[buy];
                buy = -1;
            }
        }
        if(buy != -1) profit += prices[prices.length - 1] - prices[buy];
        return profit;
    }
}
