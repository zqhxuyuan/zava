package com.interview.algorithms.string;

import com.interview.algorithms.general.C1_14_GCDSolver;

/**
 * Created_By: stefanie
 * Date: 14-7-7
 * Time: 下午10:39
 *
 * Write a method to rotate a given string by K offset.
 *
 * Solution 1:
 *      BA = (A^T+B^T^T
 * Solution 2:
 *      1. Find the GCD of the length of str and K
 *      2. every (i+j*K)%M (i = 0-gcd(M,K) and j = 0-m-1) will create a loop
 *          Example "abcd" K = 3, M = 4, gcd = 1
 *          ch[0]->temp, ch[3]->ch[0], ch[2]->ch[3],ch[1]->ch[2],temp->ch[1]
 *          Result = "dabc"
 *         when gcd != 1, need loop 0-gcd cycle.
 */
public class C11_13_RotateKOffset {

    public static String rotateByReserve(String str, int K){
        K = K % str.length();
        char[] chars = str.toCharArray();
        reserve(chars, 0, K - 1);
        reserve(chars, K, str.length() - 1);
        reserve(chars, 0, str.length() - 1);
        return String.copyValueOf(chars);
    }

    private static void reserve(char[] chars, int start, int end){
        int M = end - start + 1;
        for(int i = 0; i < M / 2; i++){
            char temp = chars[start + i];
            chars[start + i] = chars[end - i];
            chars[end - i] = temp;
        }
    }

    public static String rotationByGCD(String str, int K){
        if(K == 0) return str;

        char[] chars = str.toCharArray();
        int M = chars.length;
        int loop = C1_14_GCDSolver.gcd(M, K);
        int number = M / loop; //insure number should be full loop

        for(int i = 0; i < loop; i++){
            char tmp = chars[i];
            for(int j = 0; j < number - 1; j++){
                chars[ i + (j*K) % M] = chars[(i + (j+1)*K) % M];
            }
            chars[(i+(number-1)*K) % M] = tmp;
        }
        return String.valueOf(chars);
    }
}
