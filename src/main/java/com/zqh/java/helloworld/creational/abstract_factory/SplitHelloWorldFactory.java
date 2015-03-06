package com.zqh.java.helloworld.creational.abstract_factory;

import com.zqh.java.helloworld.SplitHelloWorld;

/**
 * @author yihua.huang@dianping.com
 */
public interface SplitHelloWorldFactory {

    public SplitHelloWorld.HelloWorldInterjection createHelloWorldInterjection();

    public SplitHelloWorld.HelloWorldObject createHelloWorldObject();
}
