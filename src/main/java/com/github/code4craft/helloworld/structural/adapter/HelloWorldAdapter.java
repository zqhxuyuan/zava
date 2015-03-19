package com.github.code4craft.helloworld.structural.adapter;

import com.github.code4craft.helloworld.HelloWorld;

/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldAdapter implements HelloWorld {

    private HelloAdapterDesignPattern helloDesignPattern;

    public HelloWorldAdapter(HelloAdapterDesignPattern helloDesignPattern) {
        this.helloDesignPattern = helloDesignPattern;
    }

    @Override
    public String helloWorld() {
        return helloDesignPattern.helloDesignPattern();
    }
}
