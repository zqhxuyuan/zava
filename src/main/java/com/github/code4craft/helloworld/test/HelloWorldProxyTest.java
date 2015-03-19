package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.structural.proxy.HelloWorldProxy;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldProxyTest {

    @Test
    public void testHelloWorldFacade(){
        HelloWorldProxy helloWorldProxy = new HelloWorldProxy(new HelloWorldProxy.DefaultHelloWorld());
        MatcherAssert.assertThat(helloWorldProxy.helloWorld(), Matchers.is("Hello Proxy!"));
    }
}
