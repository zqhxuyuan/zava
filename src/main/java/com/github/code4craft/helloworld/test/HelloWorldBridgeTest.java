package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.HelloWorld;
import com.github.code4craft.helloworld.structural.bridge.DesignPatternWorldImpl;
import com.github.code4craft.helloworld.structural.bridge.HelloWorldBridge;
import com.github.code4craft.helloworld.structural.bridge.JavaHelloWorldImpl;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldBridgeTest {

    @Test
    public void testHelloWorldAdapter(){
        HelloWorld bridgeHelloWorld = new HelloWorldBridge(new JavaHelloWorldImpl());
        MatcherAssert.assertThat(bridgeHelloWorld.helloWorld(), Matchers.is("Hello Java!"));
        bridgeHelloWorld = new HelloWorldBridge(new DesignPatternWorldImpl());
        MatcherAssert.assertThat(bridgeHelloWorld.helloWorld(), Matchers.is("Hello Bridge!"));
    }
}
