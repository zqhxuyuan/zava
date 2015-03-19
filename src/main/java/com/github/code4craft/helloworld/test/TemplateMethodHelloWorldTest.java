package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.behavioral.template_method.TemplateMethodHelloWorld;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class TemplateMethodHelloWorldTest {

    @Test
    public void testTemplateMethodHelloWorld(){
        TemplateMethodHelloWorld templateMethodHelloWorld = new TemplateMethodHelloWorld();
        assertThat(templateMethodHelloWorld.helloWorld(), Matchers.is("Hello Template Method!"));
    }
}
