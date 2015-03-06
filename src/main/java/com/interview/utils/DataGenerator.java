package com.interview.utils;

import java.util.Random;

/**
 * Created_By: stefanie
 * Date: 14-12-1
 * Time: 下午9:59
 */
public class DataGenerator {
    static Random RAND = new Random();
    public static int[] generateIntArray(int size, boolean hasNeg){
        return generateIntArray(size, 100, 0, hasNeg);
    }
    public static int[] generateIntArray(int size, int max, int min, boolean hasNeg){
        int[] array = new int[size];
        for(int i = 0; i < size; i++){
            int randomNum = RAND.nextInt((max - min) + 1) + min;
            if(hasNeg){
                int flag = generateInt(1);
                if(flag == 1){
                    array[i] = randomNum;
                } else {
                    array[i] = 0 - randomNum;
                }
            } else {
                array[i] = randomNum;
            }
        }
        return array;
    }

    public static int generateInt(int size){
        return RAND.nextInt(size + 1);
    }
}
