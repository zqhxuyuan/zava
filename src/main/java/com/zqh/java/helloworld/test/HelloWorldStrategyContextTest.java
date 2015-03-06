package com.zqh.java.helloworld.test;

import com.zqh.java.helloworld.behavioral.strategy.DesignPatternHelloWorldStrategy;
import com.zqh.java.helloworld.behavioral.strategy.HelloWorldStrategyContext;
import com.zqh.java.helloworld.behavioral.strategy.JavaHelloWorldStrategy;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldStrategyContextTest {

    @Test
    public void testHelloWorldStrategyContext(){
        HelloWorldStrategyContext helloWorldStrategyContext = new HelloWorldStrategyContext(new JavaHelloWorldStrategy());
        MatcherAssert.assertThat(helloWorldStrategyContext.helloWorld(), Matchers.is("Hello Java!"));
        helloWorldStrategyContext = new HelloWorldStrategyContext(new DesignPatternHelloWorldStrategy());
        MatcherAssert.assertThat(helloWorldStrategyContext.helloWorld(), Matchers.is("Hello Strategy!"));
    }
}
