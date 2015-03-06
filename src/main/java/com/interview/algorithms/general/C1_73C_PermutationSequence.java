package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-11-6
 * Time: 上午7:52
 */
public class C1_73C_PermutationSequence {
    public static String permutation(int n, int k){
        if(n > 9 || k <= 0) return "";
        if(n == 1) return "1";

        int[] factors = new int[n + 1];
        factors[0] = 1;
        for(int i = 1; i <= n; i++) factors[i] = factors[i - 1] * i;

        k = k - 1;
        k = k % factors[n];

        StringBuilder nums = new StringBuilder("123456789");
        StringBuilder permutation = new StringBuilder();
        for(int i = n - 1; i >= 0; i--){
            int curNum = k / factors[i];
            permutation.append(nums.charAt(curNum));
            nums.deleteCharAt(curNum);
            k = k - curNum * factors[i];
        }
        return permutation.toString();
    }
}
