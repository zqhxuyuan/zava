package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.creational.abstract_factory.AbstractFactory;
import com.github.code4craft.helloworld.creational.abstract_factory.SplitHelloWorldFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * @author yihua.huang@dianping.com
 */
public class AbstractFactoryTest {

    @Test
    public void testHelloWorld() throws InstantiationException, IllegalAccessException {
        SplitHelloWorldFactory splitHelloWorldFactory = AbstractFactory.select(AbstractFactory.Type.Java);
        MatcherAssert.assertThat(splitHelloWorldFactory.createHelloWorldObject().object(), Matchers.is("Java"));
        MatcherAssert.assertThat(splitHelloWorldFactory.createHelloWorldInterjection().interjection(), Matchers.is("Hello"));
        splitHelloWorldFactory = AbstractFactory.select(AbstractFactory.Type.DesignPattern);
        MatcherAssert.assertThat(splitHelloWorldFactory.createHelloWorldInterjection().interjection(), Matchers.is("Hello"));
        MatcherAssert.assertThat(splitHelloWorldFactory.createHelloWorldObject().object(), Matchers.is("Abstract Factory"));
    }

}
