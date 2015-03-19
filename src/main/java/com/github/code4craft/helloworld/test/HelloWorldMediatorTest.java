package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.behavioral.mediator.HelloWorldInterjection;
import com.github.code4craft.helloworld.behavioral.mediator.HelloWorldMediator;
import com.github.code4craft.helloworld.behavioral.mediator.HelloWorldObject;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldMediatorTest {

    @Test
    public void testHelloWorldMediator(){
        HelloWorldInterjection helloWorldInterjection = new HelloWorldInterjection();
        HelloWorldObject helloWorldObject = new HelloWorldObject();
        HelloWorldMediator helloWorldMediator = new HelloWorldMediator(helloWorldInterjection,helloWorldObject);
        helloWorldInterjection.setHelloWorldMediator(helloWorldMediator);
        helloWorldObject.setHelloWorldMediator(helloWorldMediator);
        MatcherAssert.assertThat(helloWorldInterjection.helloWorld(), Matchers.is("Hello Mediator!"));
        MatcherAssert.assertThat(helloWorldObject.helloWorld(), Matchers.is("Hello Mediator!"));
    }
}
