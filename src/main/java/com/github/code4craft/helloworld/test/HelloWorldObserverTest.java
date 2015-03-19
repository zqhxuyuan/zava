package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.behavioral.observer.HelloWorldObserver;
import com.github.code4craft.helloworld.behavioral.observer.Subject;
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
