package com.zqh.java.helloworld.test;

import com.zqh.java.helloworld.HelloWorld;
import com.zqh.java.helloworld.creational.factory_method.FactoryMethodHelloWorldFactory;
import com.zqh.java.helloworld.creational.factory_method.HelloWorldFactory;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class FactoryMethodHelloWorldFactoryTest {

    @Test
    public void testFactoryMethodHelloWorldFactory(){
        HelloWorldFactory helloWorldFactory = new HelloWorldFactory();
        HelloWorld helloWorld = helloWorldFactory.createHelloWorld();
        MatcherAssert.assertThat(helloWorld.helloWorld(), Matchers.is("Hello World!"));
        FactoryMethodHelloWorldFactory factoryMethodHelloWorldFactory = new FactoryMethodHelloWorldFactory();
        helloWorld = factoryMethodHelloWorldFactory.createHelloWorld();
        MatcherAssert.assertThat(helloWorld.helloWorld(), Matchers.is("Hello Factory Method!"));
    }
}
