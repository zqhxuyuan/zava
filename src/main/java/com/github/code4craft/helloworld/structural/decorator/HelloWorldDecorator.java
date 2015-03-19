package com.github.code4craft.helloworld.structural.decorator;

import com.github.code4craft.helloworld.HelloWorld;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldDecorator implements HelloWorld {

    private HelloWorld helloWorld;

    public HelloWorldDecorator(HelloWorld helloWorld) {
        this.helloWorld = helloWorld;
    }

    @Override
    public String helloWorld() {
        return helloWorld.helloWorld();
    }

    public String helloDecorator(){
        return StringUtils.substringBefore(helloWorld.helloWorld()," ")+" Decorator!";
    }

    public static class DefaultHelloWorld implements HelloWorld {

        @Override
        public String helloWorld() {
            return "Hello World!";
        }
    }
}
