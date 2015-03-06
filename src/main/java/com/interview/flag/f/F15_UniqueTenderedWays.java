package com.interview.flag.f;

/**
 * Created_By: stefanie
 * Date: 15-1-29
 * Time: 下午3:44
 */
public class F15_UniqueTenderedWays {
    public long uniqueWays(int amount){
        long[] memo = new long[amount + 1];
        return uniqueWays(amount, memo) ;
    }

    private long uniqueWays(int amount, long[] memo){

        if (amount <= 0)
            return amount == 0 ? 1 : 0;
        if(memo[amount] != 0) return memo[amount];

        long count = uniqueWays(amount - 1, memo)
                + uniqueWays(amount - 5, memo)
                + uniqueWays(amount - 20, memo)
                + uniqueWays(amount - 50, memo);
        memo[amount] = count;
        return count;
    }

    public static void main(String[] args){
        F15_UniqueTenderedWays finder = new F15_UniqueTenderedWays();
        System.out.println(finder.uniqueWays(4)); //1
        System.out.println(finder.uniqueWays(6)); //3
        System.out.println(finder.uniqueWays(7)); //4
        System.out.println(finder.uniqueWays(20)); //141
        System.out.println(finder.uniqueWays(100)); //954515231698
    }
}
