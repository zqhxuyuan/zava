package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.HelloWorld;
import com.github.code4craft.helloworld.creational.prototype.HelloWorldPrototype;
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
