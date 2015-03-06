package com.interview.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by chenting on 2014/6/27.
 */
public class ArrayUtil {

    public static Integer[] sort(Integer[] numbers){
        List<Integer> input = Arrays.asList(numbers);
        Collections.sort(input);
        Integer[] sorted = input.toArray(new Integer[input.size()]);
        return sorted;
    }

    public static Character[] getCharArray(String str){
        Character[] chars = new Character[str.length()];
        for(int i = 0; i < str.length(); i++) chars[i] = str.charAt(i);
        return chars;
    }

    public static String getString(Character[] chars){
        char[] str = new char[chars.length];
        for(int i = 0; i < chars.length; i++) str[i] = chars[i];
        return String.copyValueOf(str);
    }

    public static void swap(int[] array, int i, int j){
        int tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    public static void insert(int[] array, int i, int j){
        int tmp = array[j];
        int k = j-1;
        while(k >= i && array[k] > tmp){
            array[k+1] = array[k];
            k--;
        }
        array[k+1] = tmp;
    }

    public static void insertBefore(int[] array, int i, int j){
        int tmp = array[j];
        for(int k = j; k > i; k--){
            array[k] = array[k-1];
        }
        array[i] = tmp;
    }

    public static void swap(char[] array, int i, int j){
        char tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    public static String swap(String str, int i, int j){
        char[] chars = str.toCharArray();
        swap(chars, i, j);
        return String.copyValueOf(chars);
    }

    public static void swap(Integer[][] matrix, int x1, int y1, int x2, int y2){
        int temp = matrix[x1][y1];
        matrix[x1][y1] = matrix[x2][y2];
        matrix[x2][y2] = temp;
    }

    public static void swap(Object[] array, int i, int j){
        Object tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    public static void reverse(int[] array, int s, int e){
        while(s < e){
            swap(array, s++, e--);
        }
    }

    public static void reverse(Object[] array, int s, int e) {
        while (s < e) {
            swap(array, s++, e--);
        }
    }

    public static int sum(int[] array, int s, int e){
        int sum = 0;
        for(int i = s; i <= e; i++){
            sum += array[i];
        }
        return sum;
    }

    public static int max(int[] array){
        int max = Integer.MIN_VALUE;
        for(int i = 0; i < array.length; i++) max = Math.max(max, array[i]);
        return max;
    }
}
