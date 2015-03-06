package com.zqh.java.helloworld.test;

import com.zqh.java.helloworld.HelloWorld;
import com.zqh.java.helloworld.structural.adapter.HelloAdapterDesignPattern;
import com.zqh.java.helloworld.structural.adapter.HelloWorldAdapter;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldAdapterTest {

    @Test
    public void testHelloWorldAdapter(){
        HelloWorld adapterHelloWorld = new HelloWorldAdapter(new HelloAdapterDesignPattern());
        MatcherAssert.assertThat(adapterHelloWorld.helloWorld(), Matchers.is("Hello Adapter!"));
    }
}
