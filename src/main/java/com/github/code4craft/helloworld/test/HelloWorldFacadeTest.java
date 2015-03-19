package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.HelloWorld;
import com.github.code4craft.helloworld.structural.facade.HelloWorldFacade;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldFacadeTest {

    @Test
    public void testHelloWorldFacade(){
        HelloWorld facadeHelloWorld = HelloWorldFacade.instance().facadeHelloWorld();
        MatcherAssert.assertThat(facadeHelloWorld.helloWorld(), Matchers.is("Hello Facade!"));
    }
}
