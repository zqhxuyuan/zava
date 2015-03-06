package com.interview.leetcode.application;

/**
 * Created_By: stefanie
 * Date: 14-11-15
 * Time: 下午2:35
 *
 * https://oj.leetcode.com/problems/container-with-most-water/
 *
 * Given n non-negative integers a1, a2, ..., an, where each represents a point at coordinate (i, ai).
 * n vertical lines are drawn such that the two endpoints of line i is at (i, ai) and (i, 0). Find two lines,
 * which together with x-axis forms a container, such that the container contains the most water.
 * Note: You may not slant the container.
 *
 * Sol:
 *    the container space: container(i,j) = min(height[i], height[j]) * (j - i + 1)
 *    to maximal the space, should start with wider container, i = 0, j = length - 1.
 *    the container space is determined by min, so if i < j, need move to find a bigger i, and when i > j, need move to find a bigger j.
 *    so scan the height, always move the smaller one to a bigger one than the other vertical lines.
 *
 * Tricks:
 *    1. define how to calculate the variable needed, and how to maximize or minimize.
 *    2. consider when will get a bigger or smaller case, and use it to simply the search process.
 *    3. use two pointer to binarysearch from the two end to center.
 */
public class ContainerWithMostWater {

    public int maxArea(int[] height) {
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
