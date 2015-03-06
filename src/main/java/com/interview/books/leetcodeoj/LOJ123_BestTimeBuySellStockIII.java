package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-26
 * Time: 下午8:26
 */
public class LOJ123_BestTimeBuySellStockIII {
    //do two DP process
    //1. scan forward with tracking min, left[i] = maxProfit can get make one transaction in [0, i]
    //2. scan backward with tracking max, right[i] = maxProfit can get make one transaction in [i, prices.length - 1]
    //result is max(left[i] + right[i])
    public int maxProfit(int[] prices) {
        if(prices.length <= 1) return 0;
        //DP: scan forward with tracking min, left[i] = maxProfit can get make one transaction in [0, i]
        int[] left = new int[prices.length];
        int min = prices[0];
        left[0] = 0;
        for(int i = 1; i < prices.length; i++){
            min = Math.min(min, prices[i]);
            left[i] = Math.max(left[i-1], prices[i] - min);
        }
        //DP: scan backward with tracking max, right[i] = maxProfit can get make one transaction in [i, prices.length - 1]
        int[] right = new int[prices.length];
        int max = prices[prices.length - 1];
        right[prices.length - 1] = 0;
        for(int i = prices.length - 2; i >= 0; i--){
            max = Math.max(max, prices[i]);
            right[i] = Math.max(right[i + 1], max - prices[i]);
        }
        int maxProfit = 0;
        for(int i = 0; i < prices.length; i++){
            maxProfit = Math.max(maxProfit, left[i] + right[i]);
        }
        return maxProfit;
    }
}
