package com.zqh.java.FLRMI.test;

import com.zqh.java.FLRMI.FLRMIALImpl.FLRMI;

public class Client {
    public static void main(String args[]) {
        HelloWorld helloWorld = (HelloWorld) FLRMI.getFLRMIService("hello", "localhost", 1223,
                new Class[]{HelloWorld.class});
        System.out.println(helloWorld.hello("jeff"));
    }
}