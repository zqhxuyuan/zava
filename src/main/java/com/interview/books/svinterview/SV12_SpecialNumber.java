package com.interview.books.svinterview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午2:58
 */
public class SV12_SpecialNumber {
    public List<Integer> find(int[] array) {
//        List<Integer> P = new ArrayList<>();
//        if(P == null || array.length == 0) return P;
//
//        int[] maxLeft = new int[array.length];
//        maxLeft[0] = array[0];
//        for(int i = 1; i < array.length; i++){
//            maxLeft[i] = Math.max(maxLeft[i - 1], array[i]);
//        }
//
//        int min = Integer.MAX_VALUE;
//        for(int i = array.length - 1; i >= 0; i--){
//            if(array[i] <= min){
//                min = array[i];
//                if(maxLeft[i] == array[i]) P.add(i);
//            }
//        }
//        return P;
        List<Integer> result = new ArrayList();
        if(array == null || array.length == 0) return result;

        int[] min = new int[array.length];
        int[] max = new int[array.length];

        int maxNumberOffset = 0;
        for(int i = 1; i < array.length; i ++) {
            if(array[i] >= array[maxNumberOffset]) {
                maxNumberOffset = i;
                max[i] = i;
            } else {
                max[i] = maxNumberOffset;
            }
        }

        int minNumberOffset = array.length - 1;
        for(int i = array.length - 2; i >= 0; i --) {
            if(array[i] <= array[minNumberOffset]) {
                minNumberOffset = i;
                min[i] = i;
            } else {
                min[i] = minNumberOffset;
            }
        }

        for(int i = 0; i < array.length; i ++)
            if(min[i] == i && max[i] == i) result.add(i);

        return result;
    }

    public static void main(String[] args){
        SV12_SpecialNumber finder = new SV12_SpecialNumber();
        int[] arrays = new int[]{1,0,1,0,1,2,3};
        for(Integer index : finder.find(arrays))
            System.out.println(index);
    }
}
