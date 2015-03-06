package com.zqh.java.helloworld.test;

import com.zqh.java.helloworld.behavioral.visitor.HelloWorldCharacterElements;
import com.zqh.java.helloworld.behavioral.visitor.HelloWorldCharacterVisitor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldVisitorTest {

    @Test
    public void testHelloWorldVisitor(){
        HelloWorldCharacterElements helloWorldCharacterElements = new HelloWorldCharacterElements("Hello Visitor!".toCharArray());
        HelloWorldCharacterVisitor helloWorldCharacterVisitor = new HelloWorldCharacterVisitor();
        helloWorldCharacterElements.accept(helloWorldCharacterVisitor);
        MatcherAssert.assertThat(helloWorldCharacterVisitor.helloWorld(), Matchers.is("Hello Visitor!"));
    }
}
