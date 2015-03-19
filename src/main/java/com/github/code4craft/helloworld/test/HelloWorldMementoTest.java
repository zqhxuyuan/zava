package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.behavioral.memento.HelloWorldMementoOriginator;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldMementoTest {

    @Test
    public void testHelloWorldMediator(){
        HelloWorldMementoOriginator helloWorldMementoOriginator = new HelloWorldMementoOriginator();
        HelloWorldMementoOriginator.Memento memento = helloWorldMementoOriginator.set("Hello Memento!").saveToMemento();
        helloWorldMementoOriginator.set("Hello Whatever!");
        MatcherAssert.assertThat(helloWorldMementoOriginator.helloWorld(), Matchers.is("Hello Whatever!"));
        helloWorldMementoOriginator.restoreFromMemento(memento);
        MatcherAssert.assertThat(helloWorldMementoOriginator.helloWorld(), Matchers.is("Hello Memento!"));
    }
}
