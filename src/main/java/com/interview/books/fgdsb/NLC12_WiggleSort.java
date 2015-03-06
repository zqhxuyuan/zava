package com.interview.books.fgdsb;

import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 15-2-2
 * Time: 上午10:55
 */
public class NLC12_WiggleSort {
    public void sort(int[] array){
        if(array == null || array.length <= 1) return;

        boolean flag = true;

        int current = array[0];
        for(int i = 0; i < array.length - 1; i++){
            //save array[i] save to current or array[i-1].
            if ((flag && current > array[i+1]) || (!flag && current < array[i+1])) {
                array[i] = array[i+1];
            } else {
                array[i] = current;
                current = array[i+1];
            }
            flag = !flag;
        }
        array[array.length-1] = current;

//        simple solution with swap
//        for(int i = 0; i < array.length - 1; i++){
//            if((flag && array[i] > array[i + 1]) || (!flag && array[i] < array[i + 1])){
//                int temp = array[i];
//                array[i] = array[i + 1];
//                array[i + 1] = temp;
//            }
//            flag = !flag;
//        }
    }

    public static void main(String[] args){
        NLC12_WiggleSort sorter = new NLC12_WiggleSort();
        int[] array = new int[]{1,2,3,4};
        sorter.sort(array);
        ConsoleWriter.printIntArray(array);

        array = new int[]{3,1,2,4};
        sorter.sort(array);
        ConsoleWriter.printIntArray(array);

    }
}
