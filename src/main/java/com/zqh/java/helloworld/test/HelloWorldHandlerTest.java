package com.zqh.java.helloworld.test;

import com.zqh.java.helloworld.behavioral.chain_of_responsibility.HelloWorldHandler;
import com.zqh.java.helloworld.behavioral.chain_of_responsibility.HelloWorldInterjectionHandler;
import com.zqh.java.helloworld.behavioral.chain_of_responsibility.HelloWorldObjectHandler;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldHandlerTest {

    @Test
    public void testHelloWorldHandler(){
        HelloWorldHandler helloWorldChainOfResponsibility = new HelloWorldInterjectionHandler().setNext(new HelloWorldObjectHandler());
        MatcherAssert.assertThat(helloWorldChainOfResponsibility.helloWorld(), Matchers.is("Hello Chain of Responsibility!"));
    }
}
