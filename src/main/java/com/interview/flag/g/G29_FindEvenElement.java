package com.interview.flag.g;

import java.util.HashSet;

/**
 * Created_By: stefanie
 * Date: 15-1-26
 * Time: 下午12:24
 */
public class G29_FindEvenElement {
    public int find(int[] array){
        HashSet<Integer> distinct = new HashSet();
        int total = 0;
        int unique = 0;
        for(int i = 0; i < array.length; i++){
            total ^= array[i];
            if(!distinct.contains(array[i])) {
                unique ^= array[i];
                distinct.add(array[i]);
            }
        }
        return total ^ unique;
    }

    public static void main(String[] args){
        G29_FindEvenElement finder = new G29_FindEvenElement();
        System.out.println(finder.find(new int[]{3,1,3}));//3
        System.out.println(finder.find(new int[]{3,2,3}));//3

    }
}
