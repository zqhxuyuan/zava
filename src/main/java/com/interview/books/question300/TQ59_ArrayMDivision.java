package com.interview.books.question300;

import com.interview.utils.ArrayUtil;
import com.interview.utils.ConsoleWriter;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-7-28
 * Time: 下午8:44
 *
 * Given an array, write code to divide the array into M sub array (find largest M), make sure the sum of all sub array are the same.
 *
 * Clue:
 *      1. M should 1 <= M <= N
 *      2. sum(array) mod M == 0
 * Solution:
 *      1. Loop on M, check if M conform to Clue 2
 *      2. Find the M division, if could find a solution return m.
 *
 */
public class TQ59_ArrayMDivision {
    static class Division{
        int M;
        int[] groups;
        public Division(int M, int[] groups){
            this.M = M;
            this.groups = groups;
        }
    }

    public Division divide(int[] array){
        int sum = ArrayUtil.sum(array, 0, array.length - 1);
        int[] mark = new int[array.length];
        for(int m = array.length; m >= 2; m--){
            if(sum % m != 0) continue;
            if(canDivide(array, sum, m, mark)) return new Division(m, mark);
        }
        Arrays.fill(mark, 1);
        return new Division(1, mark);
    }

    private boolean canDivide(int[] array, int sum, int m, int[] mark){
        int averageSum = sum / m;
        Arrays.fill(mark, 0);
        for(int i = 0; i < array.length; i++){     //if any array[i] > averageSum, can't be divided
            if(array[i] > averageSum) return false;
        }

        for(int groupID = 1; groupID <= m; groupID++){   //try to divide M group
            if(!canDivide(array, averageSum, groupID, mark, 0, 0)) return false;
        }
        return true;
    }

    private boolean canDivide(int[] array, int target, int groupID, int[] mark, int currentSum, int begin){
        for(int i = begin; i < array.length; i++){
            if(mark[i] == 0 && currentSum + array[i] <= target){
                mark[i] = groupID;
                if(currentSum + array[i] == target) return true;
                else if(canDivide(array, target, groupID, mark, currentSum + array[i], i+1))  return true;
                else mark[i] = 0;
            }
        }
        return false;
    }

    public static void main(String[] args){
        TQ59_ArrayMDivision divisor = new TQ59_ArrayMDivision();
        int[] array = new int[] {2,3,4,6,3};
        Division division = divisor.divide(array);
        System.out.println(division.M);              //3
        ConsoleWriter.printIntArray(division.groups);   //1, 2, 1, 3, 2,

        array = new int[] {6,3,4,6,3};
        division = divisor.divide(array);
        System.out.println(division.M);              //1
        ConsoleWriter.printIntArray(division.groups);   //1, 1, 1, 1, 1,
    }
}
