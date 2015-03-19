package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.behavioral.strategy.JavaHelloWorldStrategy;
import com.github.code4craft.helloworld.behavioral.strategy.DesignPatternHelloWorldStrategy;
import com.github.code4craft.helloworld.behavioral.strategy.HelloWorldStrategyContext;
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
