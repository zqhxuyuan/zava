package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-11-7
 * Time: 下午11:26
 *
 * The idea is :
 *  to compute area, we need to take min(height[i],height[j]) as our height.
 *  Thus if height[i] < height[j], then the expression min(height[i],height[j])
 *  will always lead to at maximum height[i] for all other j(i being fixed),
 *  hence no point checking them.
 *
 *  Similarly when height[i] > height[j] then all the other i's can be ignored for that particular j.
 */
public class C1_79_MostWater {
    public static int maxArea(int[] height) {
        int max = 0;
        int left = 0;
        int right = height.length - 1;
        while(left < right){
            max = Math.max(max, (right - left) * Math.min(height[left], height[right]));
            if(height[left] < height[right]){
                left++;
                while(left < right && height[left] <= height[left - 1]) left++;
            } else {
                right--;
                while(left < right && height[right] <= height[right + 1]) right--;
            }
        }
        return max;
    }
}
