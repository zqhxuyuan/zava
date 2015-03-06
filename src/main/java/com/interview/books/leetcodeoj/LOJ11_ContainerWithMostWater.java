package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 下午2:33
 */
public class LOJ11_ContainerWithMostWater {
    //area = Math.min(height[i], height[j]) * (j - i);
    //width = (j-i) not (j-i+1)
    public int maxArea(int[] height) {
        if(height.length <= 1) return 0;
        int max = 0;
        int left = 0;
        int right = height.length - 1;
        while(left < right){
            int area = Math.min(height[left], height[right]) * (right - left);
            max = Math.max(max, area);
            if(height[left] < height[right]){
                int prev = height[left];
                left++;
                while(left < right && height[left] < prev) left++;
            } else {
                int prev = height[right];
                right--;
                while(left < right && height[right] < prev) right--;
            }
        }
        return max;
    }
}
