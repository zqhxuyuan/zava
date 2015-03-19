package com.github.yuriyao.FLRMI.test;

public class HelloWorldImpl implements HelloWorld {

    private static final long serialVersionUID = -8229738828935464460L;

    @Override
    public String hello(String name) {
        return "Hello " + name;
    }

}