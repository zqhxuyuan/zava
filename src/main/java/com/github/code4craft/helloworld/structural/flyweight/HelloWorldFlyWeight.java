package com.github.code4craft.helloworld.structural.flyweight;

import com.github.code4craft.helloworld.HelloWorld;

/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldFlyWeight implements HelloWorld {

    private final String value;

    public HelloWorldFlyWeight(String value) {
        this.value = value;
    }

    @Override
    public String helloWorld() {
        return value;
    }

}
