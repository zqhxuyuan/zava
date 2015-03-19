package com.github.code4craft.helloworld.creational.abstract_factory;

import com.github.code4craft.helloworld.SplitHelloWorld;

/**
 * @author yihua.huang@dianping.com
 */
public interface SplitHelloWorldFactory {

    public SplitHelloWorld.HelloWorldInterjection createHelloWorldInterjection();

    public SplitHelloWorld.HelloWorldObject createHelloWorldObject();
}
