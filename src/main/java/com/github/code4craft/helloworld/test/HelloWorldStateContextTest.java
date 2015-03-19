package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.behavioral.state.HelloWorldStateContext;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldStateContextTest {

    @Test
    public void testHelloWorldStateContext(){
        HelloWorldStateContext helloWorldStateContext = new HelloWorldStateContext();
        helloWorldStateContext.appendWord("Hello");
        MatcherAssert.assertThat(helloWorldStateContext.helloWorld(), Matchers.is("Hello "));
        helloWorldStateContext.appendWord("State");
        MatcherAssert.assertThat(helloWorldStateContext.helloWorld(), Matchers.is("Hello State!"));
        helloWorldStateContext.appendWord("Whatever");
        MatcherAssert.assertThat(helloWorldStateContext.helloWorld(), Matchers.is("Hello State!"));
    }
}
