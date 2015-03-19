package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.HelloWorld;
import com.github.code4craft.helloworld.structural.flyweight.HelloWorldFlyWeightFactory;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldFlyWeightTest {

    @Test
    public void testHelloWorldFlyWeight(){
        HelloWorld helloWorld = HelloWorldFlyWeightFactory.instance().createHelloWorld("Hello Flyweight!");
        MatcherAssert.assertThat(helloWorld.helloWorld(), Matchers.is("Hello Flyweight!"));
        helloWorld = HelloWorldFlyWeightFactory.instance().createHelloWorld("Hello Flyweight!");
        MatcherAssert.assertThat(helloWorld.helloWorld(), Matchers.is("Hello Flyweight!"));
    }
}
