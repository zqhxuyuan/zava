package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-11-10
 * Time: 下午10:26
 */
public class C1_80A_MinJumpStep {
    public static int minSteps(int[] nums) {
        int[] steps = new int[nums.length];
        steps[nums.length - 1] = 0;
        for(int i = nums.length - 2; i >= 0; i--){
            if(i + nums[i] >= nums.length - 1) {
                steps[i] = 1;
                continue;
            }
            steps[i] = Integer.MAX_VALUE;
            for(int j = nums[i]; j >= 1; j--){
                if(steps[i+j] < steps[i]) steps[i] = steps[i+j] + 1;
                if(steps[i] == 2) break;
            }
        }
        return steps[0];
    }
}
