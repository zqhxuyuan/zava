package com.zqh.java.helloworld.test;

import com.zqh.java.helloworld.HelloWorld;
import com.zqh.java.helloworld.structural.bridge.DesignPatternWorldImpl;
import com.zqh.java.helloworld.structural.bridge.HelloWorldBridge;
import com.zqh.java.helloworld.structural.bridge.JavaHelloWorldImpl;
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
