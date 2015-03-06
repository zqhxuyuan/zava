package com.interview.algorithms.random;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/27/14
 * Time: 11:24 AM
 */
public class PrefectShuffle<T> {
    public void shuffleBySwap(T[] array){
        Random random = new Random();
        for(int i = 1; i < array.length; i++){
            int rand = random.nextInt(i + 1);
            swap(array, rand, i);
        }
    }

    public void shuffleByPick(T[] array){
        Random random = new Random();
        for(int i = 0; i < array.length; i++){
            int rand = i + random.nextInt(array.length - i);
            swap(array, rand, i);
        }
    }

    private void swap(T[] array, int i, int j){
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
