package com.zqh.java.helloworld.test;

import com.zqh.java.helloworld.behavioral.template_method.TemplateMethodHelloWorld;
import org.hamcrest.MatcherAssert;
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
        MatcherAssert.assertThat(templateMethodHelloWorld.helloWorld(), Matchers.is("Hello Template Method!"));
    }
}
