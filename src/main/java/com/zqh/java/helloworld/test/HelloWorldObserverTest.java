package com.zqh.java.helloworld.test;

import com.zqh.java.helloworld.behavioral.observer.HelloWorldObserver;
import com.zqh.java.helloworld.behavioral.observer.Subject;
import org.junit.Test;

import java.io.PrintStream;

import static org.mockito.Mockito.*;

/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldObserverTest {

    @Test
    public void testHelloWorldObserver(){
        HelloWorldObserver observer = new HelloWorldObserver();
        PrintStream mockPrinter = mock(PrintStream.class);
        observer.setPrinter(mockPrinter);
        Subject subject = new Subject().attach(observer);
        subject.notifyObservers();
        verify(mockPrinter,times(1)).println("Hello Observer!");
    }
}
