package com.interview.flag.o;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 15-1-7
 * Time: 下午2:24
 */
public class O1_TwoDifference {
    public int[] find(int[] num, int target){
        int first = num.length - 1;
        int second = num.length - 1;
        while(first >= 0 && second >= 0){
            int result = num[first] - num[second];
            if(result == target) return new int[]{first, second};
            else if(result < target) second--;
            else first --;
        }
        return new int[]{-1,-1};
    }

    public static void main(String[] args){
        O1_TwoDifference finder = new O1_TwoDifference();
        int[] num = new int[]{1,3,4,7,10,12};
        int[] pair = finder.find(num, 4);
        ConsoleWriter.printIntArray(pair);  //{3,1}
    }
}
