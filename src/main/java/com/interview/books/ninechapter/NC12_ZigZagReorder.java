package com.interview.books.ninechapter;

import com.interview.utils.ArrayUtil;
import com.interview.utils.ConsoleWriter;

/**
 * Created_By: stefanie
 * Date: 14-12-12
 * Time: 下午5:44
 */
public class NC12_ZigZagReorder {
    public void reorder(int[] array){
        int flag = getInitFlag(array);
        int front = 0, back = 1;
        while(back < array.length){
            if(array[front] * flag > 0){
                front++; back++;
            } else {
                while(back < array.length && array[back] * flag < 0) back++;
                if(back > array.length) break;
                ArrayUtil.swap(array, front++, back++);
            }
            flag = -1 * flag;
        }
    }

    public void reorderKeep(int[] array){
        int flag = getInitFlag(array);
        int front = 0, back = 1;
        while(back < array.length){
            if(array[front] * flag > 0){
                front++; back++;
            } else {
                while(back < array.length && array[back] * flag < 0) back++;
                if(back > array.length) break;
                rotate(array, front++, back++);
            }
            flag = -1 * flag;
        }
    }

    private int getInitFlag(int[] array){
        int positive = 0;
        int negative = 0;
        for(int i = 0; i < array.length; i++){
            if(array[i] > 0) positive++;
            else negative++;
        }
        return positive >= negative? 1 : -1;
    }

    private void rotate(int[] array, int start, int end){
        ArrayUtil.reverse(array, start, end - 1);
        ArrayUtil.reverse(array, start, end);
    }

    public static void main(String[] args){
        NC12_ZigZagReorder reorder = new NC12_ZigZagReorder();
        int[] array = new int[]{1, 2, 3, -4};
        reorder.reorder(array);
        ConsoleWriter.printIntArray(array);  //1, -4, 3, 2

        array = new int[]{1,-3,2,-4,-5};
        reorder.reorder(array);
        ConsoleWriter.printIntArray(array); //-3, 1, -4, 2, -5

        array = new int[]{1, 2, 3, -4};
        reorder.reorderKeep(array);
        ConsoleWriter.printIntArray(array);  //1, -4, 2, 3
    }
}
