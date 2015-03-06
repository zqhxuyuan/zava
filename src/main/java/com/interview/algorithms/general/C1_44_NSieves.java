package com.interview.algorithms.general;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 8/4/14
 * Time: 1:00 PM
 *
 *
 * Given N sieves, write code to calculate the possibility of each sum of all the sieves number.
 * For n<=S<=6n, the number of events is f(S, n)
 *      f(S,n) = f(S-6, n-1) + f(S-5, n-1) + ... + f(S-1, n-1)
 */
public class C1_44_NSieves {

    public static void sumPossibility(int n){
        int max = n * 6 + 1;
        int[][] p = new int[max][n+1];

        for (int i=1; i<=6; i++) { //only one sieves
            p[i][1] = 1;
        }
        for (int i=1; i<=n; i++) { //i sieves which sum is i or 6*i
            p[i][i] = 1;
            p[6*i][i] = 1;
        }

        for(int i = 2; i <= n; i++){//i sieves
            for(int j = i+1; j < 6*i; j++){//each sum
                for (int k=(j-6<i-1)?i-1:j-6; k<=j-1; k++) //f(S,n) = f(S-6, n-1) + f(S-5, n-1) + ... + f(S-1, n-1)
                    p[j][i] += p[k][i-1];
            }
        }

        double p6 = Math.pow(6, n);
        for (int i=n; i<max; i++) {
            System.out.printf("P(S=%d)=%3f\n", i, (p[i][n]/p6));
        }
    }
}
