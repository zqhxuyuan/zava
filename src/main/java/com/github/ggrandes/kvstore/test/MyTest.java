package com.github.ggrandes.kvstore.test;

import com.github.ggrandes.kvstore.utils.PrimeFinder;

/**
 * Created by zqhxuyuan on 15-3-30.
 */
public class MyTest {

    public static void main(String[] args) {
        float loadFactor = 0.75f;
        int capacity = 128;
        int defaultSize = PrimeFinder.nextPrime(capacity);
        int threshold = (int)(capacity * loadFactor);

        System.out.println(defaultSize);
        System.out.println(threshold);
    }
}
