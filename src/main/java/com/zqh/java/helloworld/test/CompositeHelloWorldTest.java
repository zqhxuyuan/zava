package com.zqh.java.helloworld.test;

import com.zqh.java.helloworld.HelloWorld;
import com.zqh.java.helloworld.structural.composite.CompositeHelloWorld;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class CompositeHelloWorldTest {

    @Test
    public void testCompositeHelloWorld(){
        HelloWorld emptyCompositeHelloWorld = new CompositeHelloWorld();
        MatcherAssert.assertThat(emptyCompositeHelloWorld.helloWorld(), Matchers.isEmptyString());
        HelloWorld compositeHelloWorld = new CompositeHelloWorld(new CompositeHelloWorld.DefaultHelloWorld());
        MatcherAssert.assertThat(compositeHelloWorld.helloWorld(), Matchers.is("Hello Composite!"));
    }
}
