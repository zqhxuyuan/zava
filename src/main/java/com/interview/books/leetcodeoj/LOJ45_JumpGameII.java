package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午2:39
 */
public class LOJ45_JumpGameII {
    //scan from begin to last, find the min step from the first element to i-th element
    //only scan the point is reachable from the first element
    //if j, k position both can reach i, step[j] < step[k], so scan from left to right, when found 1st valid break
    //use a maxJump to control the beginning of scan, int begin = i - maxStep < 0? 0 : i - maxStep; since element before begin can't directly jump to i.
    public int jump(int[] A) {
        if(A == null || A.length <= 1) return 0;
        int maxJump = A[0];
        int[] steps = new int[A.length];
        for(int i = 1; i < A.length; i ++) {
            steps[i] = Integer.MAX_VALUE;
            int begin = i - maxJump < 0? 0 : i - maxJump;
            for(int j = begin; j < i; j ++) {
                if(steps[j] != Integer.MAX_VALUE && j + A[j] >= i) {
                    steps[i] = Math.min(steps[i], steps[j] + 1);
                    break;
                }
            }
            maxJump = Math.max(maxJump, A[i]);
        }
        return steps[A.length - 1];
    }
}
