package com.github.code4craft.helloworld.creational.prototype;

import com.github.code4craft.helloworld.HelloWorld;

/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldPrototype implements HelloWorld {

    private String helloWorld;

    public HelloWorldPrototype(String helloWorld) {
        this.helloWorld = helloWorld;
    }

    public HelloWorld clone() {
        return new HelloWorldPrototype(this.helloWorld);
    }

    @Override
    public String helloWorld() {
        return helloWorld;
    }

    public final static HelloWorldPrototype PROTOTYPE = new HelloWorldPrototype("Hello Prototype!");
}
