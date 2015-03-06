package com.interview.algorithms.random;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/27/14
 * Time: 11:50 AM
 */
public class RandomPick<T> {
    public void pick(T[] array, T[] elements){
        int m = elements.length;
        Random random = new Random();
        for(int i = 0; i < m; i++) elements[i] = array[i];
        for(int i = m; i < array.length; i++){
            int rand = random.nextInt(i + 1);
            if(rand < m) elements[rand] = array[i];
        }
    }
}
