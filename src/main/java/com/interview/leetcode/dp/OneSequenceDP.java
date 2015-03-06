package com.interview.leetcode.dp;

import java.util.Set;

/**
 * Created_By: stefanie
 * Date: 14-11-25
 * Time: 上午10:06
 */
public class OneSequenceDP {

    /**
     * Given a sequence of integers, find the longest increasing subsequence (LIS).
     */
    public int longestIncreasingSubsequence(int[] nums) {
        int[] len = new int[nums.length];
        for(int i = 0; i < nums.length; i++){
            len[i] = 1;  //INIT
            for(int j = 0; j < i; j++){
                if(nums[j] <= nums[i]) {
                    len[i] = Math.max(len[j] + 1, len[i]);
                }
            }
        }
        int max = 0;
        for(int i = 0; i < nums.length; i++){
            max = Math.max(len[i], max);
        }
        return max;
    }

    /**
     * A message containing letters from A-Z is being encoded to numbers, A -> 1 B -> 2 ... Z -> 26
     * Given an encoded message containing digits, determine the total number of ways to decode it.
     * When the digits is in-valid, return 0
     */
    static class DecodeWays{
//        Time: O(N), Space: O(1)
//        ways[i]       how many ways to decode s.substring(0, i)
//        initialize:   ways[0] = 1 and ways[0] = 1   if s.charAt(0) == '0' return 0
//        function:     if chars[i] == 0
//                          if( chars[i-1] > 2 || chars[i-1] == '0' ) return 0
//                          else state[i] = state[i-2]
//                      else
//                          if (chars[i-1] < 3 and chars[i] < 7) state[i] = state[i-2] + state[i-1]
//                          else state[i] = state[i-1]
//        result:       ways[s.length()]
        public int decodeWays(String s) {
            if(s == null || s.length() == 0 || s.charAt(0) == '0') return 0;
            int[] ways = new int[3];
            ways[0] = 1;
            ways[1] = 1;

            for(int i = 2; i <= s.length(); i++){
                char ch = s.charAt(i - 1);
                char pre = s.charAt(i - 2);
                if(ch == '0'){
                    if(pre == '0' || pre > '2') return 0;
                    else ways[i%3] = ways[(i - 2)%3];
                } else {
                    int num = (pre - '0') * 10 + ch - '0';
                    if(num < 10 || num > 26) ways[i%3] = ways[(i - 1)%3];
                    else ways[i%3] = ways[(i - 1)%3] + ways[(i - 2)%3];
                }
            }
            return ways[s.length()%3];
        }
    }

    /**
     * Given a word dict, and a sens, figure out if the sens can be segmented into words in the dict
     */
    static class WordBreak {
        //Time: O(N^2)  Space: O(N)
        //canSegment[i]     if s.substring(0, i) can be segmented or not
        //initialize:   canSegment[0] = true
        //function:     canSegment[i] = true, if can find j canSegment[j] == true and s.substring(j, i) in the dict
        //result:       canSegment[s.length()]
        public static boolean couldBreak(String s, Set<String> dict) {
            if (s == null || s.length() == 0) return false;
            int maxLen = maxLength(dict);
            boolean[] canSegment = new boolean[s.length() + 1];
            canSegment[0] = true;
            for (int i = 1; i <= s.length(); i++) {
                canSegment[i] = false;
                for(int j = i - 1; j >= 0 && i - j <= maxLen; j--){
                    if (!canSegment[j]) continue;
                    String word = s.substring(j, i);
                    if (dict.contains(word)) {
                        canSegment[i] = true;
                        break;
                    }
                }
            }
            return canSegment[s.length()];
        }

        public static int maxLength(Set<String> dict) {
            int length = 0;
            for (String word : dict) length = Math.max(length, word.length());
            return length;
        }
    }

    /**
     * Given a string containing just the characters '(' and ')',
     * find the length of the longest valid (well-formed) parentheses substring.
     */
    static class LongestValidParentheses{
        //State: len[i] the longest valid parentheses end with (i-1)-th char
        //Transfer: if s.charAt(i) == '('   len[i] = 0
        //          if s.charAt(i) == ')' && i - len[i-1] - 1 >= 0 and S.charAt(i-len[i-1] - 1) == ‘('
        //              len[i] = len[i-1] + 2                              if i - len[i-1] - 2 < 0
        //              len[i] = len[i-1] + 2 + len[i-len[i-1] - 2]        if i - len[i-1] - 2 >= 0
        //Init: len[0] = 0
        //Answer: max of len[*]
        public int longestValidParentheses(String s) {
            int[] len = new int[s.length() + 1];
            len[0] = 0;
            for(int i = 1; i <= s.length(); i++){
                if(s.charAt(i - 1) == '(') len[i] = 0;
                else if(i - len[i-1] - 1 > 0 && s.charAt(i - len[i-1] - 2) == '(') {
                    len[i] = len[i - 1] + 2;
                    if(i - len[i-1] - 2 >= 0) len[i] += len[i - len[i-1] - 2];
                }
            }
            int max = 0;
            for(int i = 1; i <= s.length(); i++){
                max = Math.max(max, len[i]);
            }
            return max;
        }
    }

    /**
     * Find the contiguous subarray within an array (containing at least one number) which has the largest product.
     */
    static class MaximalProductSubarray{
        public int maxProduct(int[] A) {
            if(A.length == 1) return A[0];
            int max = 0;
            int forward = 1;
            int backward = 1;
            for(int i = 0; i < A.length; i++){
                forward = forward * A[i];
                backward = backward * A[A.length - 1 - i];
                int bigger = Math.max(forward, backward);
                max = Math.max(max, bigger);
                if(forward == 0) forward = 1;
                if(backward == 0) backward = 1;
            }
            return max;
        }
    }

    /**
     * Given n items with size A[i], an integer m denotes the size of a backpack.
     * How full you can fill this backpack?
     */
    static class BooleanKnapsack{

//        State: values[i]: the max values can get when Knapsack size is i
//               selected[i][j]: items[j] is put in Knapsack size is i
//        Transfer:
//               values[i] = Math.max(items[j] + values[left]), which j (left >= 0) && !selected[left][j])
//                           left = i - items[j], which is the left size if items[j] is selected
//        Init:  values[0] = 0
//               values[i] == values[i-1] && selected[i][*] = selected[i-1][*]
//        Result:values[m]
        public int backPack(int m, int[] items) {
            int[] values = new int[m + 1];
            boolean[][] selected = new boolean[m + 1][items.length];
            values[0] = 0;
            for(int i = 1; i <= m; i++){
                for(int j = 0; j < items.length; j++){
                    int left = i - items[j];
                    if(left >= 0 && !selected[left][j] && values[left] + items[j] > values[i]){
                        values[i] = values[left] + items[j];
                        for(int k = 0; k < items.length; k++) selected[i][k] = selected[left][k];   //copy the solution
                        selected[i][j] = true;
                    }
                }
                if(i < m){  //copy the solution to next
                    values[i + 1] = values[i];
                    for(int k = 0; k < items.length; k++) selected[i + 1][k] = selected[i][k];
                }
            }
            return values[m];
        }
    }

   /**
    * Given a list of N coins, their values (V1, V2, ... , VN), and the total sum S.
    * Find the minimum number of coins the sum of which is S (we can use as many coins of one type as we want),
    * or report that it's not possible to select coins in such a way that they sum up to S.
    */
    static class CoinsSum{
       //count[i]: the min count of coins which sum if i
       //initialize: count[*] = -1
       //function:   count[i] = count[i - values[j]] + 1
       //            if(values[j] <= i && count[i - values[j]] > -1 && count[i - values[j]] + 1 < count[i])
       //result: count[S]
       public int getMinNumberOfCoints(int S, int[] values) {
           int[] count = new int[S + 1];
           for(int i = 1; i < count.length; i ++) {
               count[i] = -1;
           }
           for(int s = 1; s <= S; s ++)
               for(int j = 0; j < values.length; j++) {
                   if(values[j] <= s && count[s - values[j]] + 1 > 0 && count[s - values[j]] + 1 < count[s])
                       count[s] = count[s - values[j]] + 1;
               }

           return count[S];
       }
   }

    /**
     * Given an integer array, adjust each integers so that the difference of every adjcent integers are not greater than a given number target.
     * If the array before adjustment is A, the array after adjustment is B, you should minimize the sum of |A[i]-B[i]|
     */
   static class MinimalAdjustCost{
       //cost[i][j] is the total cost to change i-th number to j
       //initialize: cost[0][j] = j - a[0];
       //function:   cost[i][j] = min(cost[i-1][j], cost[i-1][j-r], cost[i-1][j+r]) + Math.abs(a[i]-j), r is 1 ~ target
       //result:     min of cost[A.size() - 1][*]
       //Time: O(N * range * 2 * target) Space: O(range)
       public int MinAdjustmentCost(int[] A, int target) {
           int max = 0;
           for(int i = 0; i < A.length; i++) max = Math.max(A[i], max);
           int range = max + target + 1;

           int[] cost = new int[range];

           for(int i = 0; i < range; i++){
               cost[i] = Math.abs(i - A[0]);
           }

           for(int i = 1; i < A.length; i++){
               int[] newCost = new int[range];
               for(int j = 0; j < range; j++){
                   newCost[j] = cost[j];
                   for(int r = 1; r <= target; r++){
                       if(j-r >= 0) newCost[j] = Math.min(newCost[j], cost[j-r]);
                       if(j+r < range) newCost[j] = Math.min(newCost[j], cost[j+r]);
                   }
                   newCost[j] += Math.abs(A[i] - j);
               }
               cost = newCost;
           }
           int min = Integer.MAX_VALUE;
           for(int j = 0; j < range; j++){
               min = Math.min(min, cost[j]);
           }
           return min;
       }
   }

    /**
     * Given a sequence of integers, sequence, return the length of the longest subsequence of sequence
     */
   static class ZigZagSequence{
        //len[i]: the longest zigzag sequence end at i
        //selected[i][k]: k-th element is in the longest zigzag sequence end at i
        //initialize: len[*] = 1; len[1] = 2 if nums[1] != nums[0]
        //function:   k = first [i - 2, 0] which selected[i-1][k]
        //            len[i] = len[i - 1] + 1    if (nums[i] - nums[i-1]) * (nums[i-1] - nums[k]) < 0
        //            len[i] = len[i - 1         otherwise
        //            copy selected[i-1][*] to select[i][*]
        //result:     len[nums.length() - 1]
        public int longest(int[] nums){
            if(nums.length < 1) return nums.length;
            int[] len = new int[nums.length];
            boolean[][] selected = new boolean[nums.length][nums.length];
            for(int i = 0; i < nums.length; i++){
                len[i] = 1;
                selected[i][i] = true;
            }
            if(nums[0] != nums[1]){
                len[1] = 2;
                selected[1][0] = true;
            }

            for(int i = 2; i < nums.length; i++){
                for(int k = i - 2; k >= 0; k--){
                    if(selected[i - 1][k]){
                        if((nums[i] - nums[i - 1]) * (nums[i - 1] - nums[k]) < 0){
                            for(int m = 0; m < i; m++) selected[i][m] = selected[i-1][m];
                            selected[i][i] = true;
                            len[i] = len[i - 1] + 1;
                        } else {
                            for(int m = 0; m < i; m++) selected[i][m] = selected[i-1][m];
                            selected[i][i - 1] = false;
                            selected[i][i] = true;
                            len[i] = len[i - 1];
                        }
                        break;
                    }
                }
            }
            return len[nums.length - 1];
        }
   }
}
