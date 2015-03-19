package com.github.code4craft.helloworld.behavioral.visitor;

import com.github.code4craft.helloworld.HelloWorld;

/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldCharacterVisitor implements HelloWorldVisitor, HelloWorld {

    private StringBuffer accum;

    public HelloWorldCharacterVisitor(){
        this.accum = new StringBuffer();
    }

    @Override
    public void visit(HelloWorldElement helloWorldElement) {
        if (helloWorldElement instanceof HelloWorldCharacterElement){
            HelloWorldCharacterElement helloWorldCharacterElement = (HelloWorldCharacterElement) helloWorldElement;
            accum.append(helloWorldCharacterElement.getCh());
        }
    }

    @Override
    public String helloWorld() {
        return accum.toString();
    }
}
