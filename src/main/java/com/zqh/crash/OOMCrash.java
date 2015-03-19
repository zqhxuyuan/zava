package com.zqh.crash;

/**
 * Created by zqhxuyuan on 15-3-3.
 *
 * Exception in thread "main" java.lang.OutOfMemoryError: Requested array size exceeds VM limit
 */
public class OOMCrash {

    public static void main ( String[] args ) {
        Object[] obj = new Object[Integer.MAX_VALUE];
    }

    public static void stackOverflow(){

    }

}