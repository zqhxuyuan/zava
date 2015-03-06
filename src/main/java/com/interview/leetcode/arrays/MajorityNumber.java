package com.interview.leetcode.arrays;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 14-11-18
 * Time: 上午7:52
 *
 * Game from LintCode:
 *  http://lintcode.com/en/problem/majority-number-i/
 *  http://lintcode.com/en/problem/majority-number-ii/
 *  http://lintcode.com/en/problem/majority-number-iii/
 *
 */
public class MajorityNumber {

    public static int majorityNumber2(int[] nums) {
        if(nums == null || nums.length <= 0) return -1;
        int candidate = 0;
        int count = 0;
        for(int i = 0; i < nums.length; i++){
            int current = nums[i];
            if(count == 0) candidate = current;
            if(current == candidate) count++;
            else count--;
        }
        return candidate;
    }

    public static int majorityNumber3(int[] nums) {
        // write your code
        int one = 0; int countOne = 0;
        int two = 0; int countTwo = 0;
        for(int i = 0; i < nums.length; i++){
            int current = nums[i];
            if(countOne == 0)   one = current;
            else if(countTwo == 0)  two = current;

            if(current == one) countOne++;
            else if(current == two) countTwo++;
            else {
                countOne--;
                countTwo--;
            }
        }

        countOne = 0; countTwo = 0;
        for(int i = 0; i < nums.length; i++){
            int current = nums[i];
            if(current == one) countOne++;
            else if(current == two) countTwo++;
        }

        return countOne >= countTwo? one : two;
    }

    public int majorityNumber(ArrayList<Integer> nums, int k) {
        // write your code
        HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>();
        for(int i = 0; i < nums.size(); i++){
            int current = nums.get(i);
            if(counter.containsKey(current)){
                counter.put(current, counter.get(current) + 1);
            } else if(counter.size() < k){
                counter.put(current, 1);
            } else {  //decrease all the candidates counter by one, and remove candidate if it's counter == 0
                Iterator<Map.Entry<Integer, Integer>> iterator = counter.entrySet().iterator();
                while(iterator.hasNext()){
                    Map.Entry<Integer, Integer> item = iterator.next();
                    if(item.getValue() == 1) iterator.remove();
                    else item.setValue(item.getValue() - 1);
                }
            }
        }

        for(Integer key : counter.keySet())     counter.put(key, 0);
        for(int i = 0; i < nums.size(); i++){
            int current = nums.get(i);
            if(counter.containsKey(current))    counter.put(current, counter.get(current) + 1);
        }

        int max = 0;
        int maxCount = 0;
        Iterator<Map.Entry<Integer, Integer>> iterator = counter.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<Integer, Integer> item = iterator.next();
            if(item.getValue() > maxCount){
                max = item.getKey();
                maxCount = item.getValue();
            }
        }
        return max;
    }
}
