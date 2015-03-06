package com.interview.algorithms.array;

import com.interview.utils.ArrayUtil;

/**
 * Created_By: stefanie
 * Date: 14-11-12
 * Time: 下午5:41
 */
public class C4_81_FirstMissingPositive {

    public static int firstMissingPositive(int[] num) {
        if(num.length == 0) return 1;
        for(int i = 0; i < num.length;){
            int rightPlace = num[i] - 1;  //the right place to put num[i]
            //if meet the all following condition, do the swap
            //1. the right place is in range of array,  >= 0 && < num.length
            //2. the current place is not the right place
            //3. the number in right place is not the right number
            if(rightPlace >= 0 && rightPlace < num.length && rightPlace != i && num[i] != num[rightPlace]){
                ArrayUtil.swap(num, i, rightPlace);
            } else i++;
        }
        for(int i = 0; i < num.length; i++){
            if(num[i] != i + 1) return i + 1;
        }
        return num.length + 1;
    }
}
