package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.structural.decorator.HelloWorldDecorator;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldDecoratorTest {

    @Test
    public void testHelloWorldDecorator(){
        HelloWorldDecorator helloWorldDecorator = new HelloWorldDecorator(new HelloWorldDecorator.DefaultHelloWorld());
        MatcherAssert.assertThat(helloWorldDecorator.helloWorld(), Matchers.is("Hello World!"));
        MatcherAssert.assertThat(helloWorldDecorator.helloDecorator(), Matchers.is("Hello Decorator!"));
        helloWorldDecorator = new HelloWorldDecorator(helloWorldDecorator);
        MatcherAssert.assertThat(helloWorldDecorator.helloWorld(), Matchers.is("Hello World!"));
    }
}
