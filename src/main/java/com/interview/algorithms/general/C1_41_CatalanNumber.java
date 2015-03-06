package com.interview.algorithms.general;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/30/14
 * Time: 12:26 PM
 *
 * There is k parenthesis, write code to calculate how many permutations could have.
 * For 2 parenthesis, there is 2 permutations: ()() and (()).
 * This problem is the same as:
 *      1. there is N non-duplicate number, how many different sequences when pushing these numbers to a stack.
 *      2. given N non-duplicate number, how many different binary tree could be built.
 *      3. given an N edge convex polygon, how many different way to using non-crossProduct diagonal line to cut polygon into triangle.
 * It's the Catalan number: h(0)=1,h(1)=1, the recursive definition is：
 *      h(n)= h(0)*h(n-1)+h(1)*h(n-2) + ... + h(n-1)h(0) (n>=2)
 */
public class C1_41_CatalanNumber {

    public static long calc(int n){
        if(n >= 0 && n <= 1) return 1;
        long[] f = new long[n+1];
        f[0] = 1;
        f[1] = 1;
        for(int i = 2; i <= n; i++){
            for(int j = 0; j < i; j++){
                f[i] += f[j]*f[i-j-1];
            }
        }
        return f[n];
    }
}
