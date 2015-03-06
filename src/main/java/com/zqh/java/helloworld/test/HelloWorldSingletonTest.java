package com.zqh.java.helloworld.test;

import com.zqh.java.helloworld.HelloWorld;
import com.zqh.java.helloworld.creational.singleton.HelloWorldSingleton;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldSingletonTest {

    @Test
    public void testHelloWorldSingleton(){
        HelloWorld helloWorld = HelloWorldSingleton.instance();
        MatcherAssert.assertThat(helloWorld.helloWorld(), Matchers.is("Hello Singleton!"));
    }
}
