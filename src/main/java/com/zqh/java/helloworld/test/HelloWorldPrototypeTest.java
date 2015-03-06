package com.zqh.java.helloworld.test;

import com.zqh.java.helloworld.HelloWorld;
import com.zqh.java.helloworld.creational.prototype.HelloWorldPrototype;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldPrototypeTest {

    @Test
    public void testHelloWorldPrototype(){
        HelloWorld helloWorld = HelloWorldPrototype.PROTOTYPE.clone();
        MatcherAssert.assertThat(helloWorld.helloWorld(), Matchers.is("Hello Prototype!"));
    }
}
