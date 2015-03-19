package com.github.yuriyao.FLRMI.test;

import com.github.yuriyao.FLRMI.FLRMIALImpl.FLRMI;

public class Client {
    public static void main(String args[]) {
        HelloWorld helloWorld = (HelloWorld) FLRMI.getFLRMIService("hello", new Class[]{HelloWorld.class});
        System.out.println(helloWorld.hello("jeff"));
    }
}