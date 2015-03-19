package com.zqh.crash;

/**
 * Created by zqhxuyuan on 15-3-3.
 *
 * java.lang.ExceptionInInitializerError
 * Caused by: java.lang.SecurityException: Unsafe
 */
import sun.misc.Unsafe;

public class UnsafeCrash {

    private static final Unsafe unsafe = Unsafe.getUnsafe();

    public static void crash() {
        unsafe.putAddress(0, 0);
    }

    public static void main(String[] args) {
        crash();
    }
}