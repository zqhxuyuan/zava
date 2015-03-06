package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-11-10
 * Time: 下午1:11
 *
 * Solution:
 *  scan from the beginning, if i-th element could reach from 0, reachable[i] is true;
 *  after scan return reachable[length - 1];
 *  1. init reachable[0] = true;
 *  2. for next 1 - length - 1
 *      if previous reachable one can reach this element, reachable mark to true; scan from 0;
 */
public class C1_80_JumpGame {

    public static boolean canJump(int[] A) {
        boolean[] reachable = new boolean[A.length];
        reachable[0] = true;
        for (int i = 1; i < A.length; i++) {
            for (int j = 0; j < i; j++) {
                if (reachable[j] && j + A[j] >= i) {
                    reachable[i] = true;
                    break;
                }
            }
        }
        return reachable[A.length - 1];
    }
}
