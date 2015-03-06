package com.interview.books.question300;

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
public class TQ15_NSieves {

    //p[s][k]: the how many times to get sum = s using k sieves
    //initialize: p[i][1] = 1, p[i][i] = 1; p[6*i][i] = 1;
    //function: p[s][k] = p[s-6][k-1] + p[s-5][k-1] + .. + p[s-1][k-1]
    //result: p[s][k]/6^k
    //Time: O(6K^2), Space: O(6K)
    public void sumPossibility(int K){
        int max = K * 6;
        int[] p = new int[max + 1];

        for(int s = 1; s <= 6; s++) p[s] = 1;

        for(int k = 2; k <= K; k++){
            int[] q = new int[max + 1];
            q[k] = 1;
            q[6 * k] = 1;
            for(int s = k + 1; s < 6 * k; s++){
                for(int i = (s - 6 < k - 1)? k - 1 : s - 6; i <= s - 1; i++){
                    q[s] += p[i];
                }
            }
            p = q;
        }

        double p6 = Math.pow(6, K);
        double sum = 0;
        for(int i = K; i <= max; i++){
            System.out.printf("P(S=%d)=%3f\n", i, (p[i] / p6));
            sum += (p[i] / p6);
        }
        System.out.printf("Total=%3f\n", sum);
    }

    public static void main(String[] args){
        TQ15_NSieves sieves = new TQ15_NSieves();
        sieves.sumPossibility(2);
        sieves.sumPossibility(3);
    }
}
