package com.github.code4craft.helloworld.behavioral.mediator;

import com.github.code4craft.helloworld.SplitHelloWorld;

/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldMediator {

    private SplitHelloWorld.HelloWorldInterjection helloWorldInterjection;

    private SplitHelloWorld.HelloWorldObject helloWorldObject;

    public HelloWorldMediator(SplitHelloWorld.HelloWorldInterjection helloWorldInterjection, SplitHelloWorld.HelloWorldObject helloWorldObject) {
        this.helloWorldInterjection = helloWorldInterjection;
        this.helloWorldObject = helloWorldObject;
    }

    public String interjection() {
        return helloWorldInterjection.interjection();
    }

    public String object() {
        return helloWorldObject.object();
    }

}
