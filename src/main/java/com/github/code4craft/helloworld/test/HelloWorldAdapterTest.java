package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.structural.adapter.HelloAdapterDesignPattern;
import com.github.code4craft.helloworld.HelloWorld;
import com.github.code4craft.helloworld.structural.adapter.HelloWorldAdapter;
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
