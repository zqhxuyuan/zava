package com.zqh.crash;

/**
 * Created by zqhxuyuan on 15-3-3.
 */
public class JVMCrash {

    public static void main ( String[] args ) {
        Object[] o = null;

        while (true) {
            o = new Object[] {o};
        }
    }
}