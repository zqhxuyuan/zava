package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午7:56
 */
public class LOJ60_PermutationSequence {
    //permutation with n digits will have n! elements, based on this rule find K-th element
    //1. calculate factors from [0, n], factors[0] = 1;
    //2. for initialize: k-- and k = k % factors[n];
    //3. for every offset: k -= cur * factors[n - 1] and n--;
    public String getPermutation(int n, int k) {
        int[] factors = factors(n);
        if(n == 0 || k <= 0) return "";
        k--;
        k = k % factors[n];
        StringBuffer buffer = new StringBuffer();
        StringBuffer option = new StringBuffer("123456789");
        while(n > 0){
            int cur = k / factors[n - 1];
            buffer.append(option.charAt(cur));
            option.deleteCharAt(cur);
            k -= cur * factors[n - 1];
            n--;
        }
        return buffer.toString();
    }

    public int[] factors(int n){
        int[] factors = new int[n + 1];
        factors[0] = 1;
        for(int i = 1; i <= n; i++) factors[i] = factors[i - 1] * i;
        return factors;
    }
}
