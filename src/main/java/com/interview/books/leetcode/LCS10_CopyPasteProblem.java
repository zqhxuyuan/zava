package com.interview.books.leetcode;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 下午3:39
 */
public class LCS10_CopyPasteProblem {

    //count[i]: the max number of A get by press i keys
    //initialize: count[0] = 0;
    //function:  count[i] = max(count[i], count[i - 1] + 1) //count[i] maybe already have value by populate by previous elements
    //           count[j] = max(count[i] * (j - i - 2)): j = [i + 4, i + 9])  populate count[i] to count[j]
    //           only populate to i + 9 because, for i + 10:
    //           "select, copy, paste * 8" is equivalent to "select, copy, paste * 2, select, copy, paste * 4"
    //           and the latter is better since it leaves us with more in the clipboard.
    //result: count[times]
    //Time: O(N), Loop of j is constants time: 6
    public int maxNumber(int times){
        int[] count = new int[times +  1];
        count[0] = 0;
        for(int i = 1; i <= times; i++){
            count[i] = Math.max(count[i], count[i - 1] + 1); // press 'A' once
            for(int j = i + 4; j < Math.min(i + 10, times + 1); j++){
                count[j] = Math.max(count[j], count[i] * (j - i - 2));  // press select all, copy, paste (j-i-1) times
            }
        }
        return count[times];
    }

    public static void main(String[] args){
        LCS10_CopyPasteProblem finder = new LCS10_CopyPasteProblem();
        for(int i = 1; i < 22; i++){
            System.out.println(i + ": " + finder.maxNumber(i));
        }
    }
}
