package com.github.code4craft.helloworld.behavioral.mediator;

import com.github.code4craft.helloworld.SplitHelloWorld;
import com.github.code4craft.helloworld.HelloWorld;

/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldObject implements SplitHelloWorld.HelloWorldObject, HelloWorld {

    private HelloWorldMediator helloWorldMediator;

    private static final String separator = " ";

    private static final String terminator = "!";

    public void setHelloWorldMediator(HelloWorldMediator helloWorldMediator) {
        this.helloWorldMediator = helloWorldMediator;
    }

    @Override
    public String object() {
        return "Mediator";
    }

    @Override
    public String helloWorld() {
        return helloWorldMediator.interjection() + separator + object() + terminator;
    }
}
