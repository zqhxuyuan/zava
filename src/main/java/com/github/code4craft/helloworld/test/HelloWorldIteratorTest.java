package com.github.code4craft.helloworld.test;

import com.github.code4craft.helloworld.behavioral.iterator.HelloWorldCharacterIterator;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.hamcrest.Matchers;
/**
 * @author yihua.huang@dianping.com
 */
public class HelloWorldIteratorTest {

    @Test
    public void testHelloWorldIterator(){
        final String helloIterator = "Hello Iterator!";
        HelloWorldCharacterIterator helloWorldCharacterIterator = new HelloWorldCharacterIterator(helloIterator.toCharArray());
        StringBuffer stringBuffer = new StringBuffer();
        while (helloWorldCharacterIterator.hasNext()){
            stringBuffer.append(helloWorldCharacterIterator.next());
        }
        MatcherAssert.assertThat(stringBuffer.toString(), Matchers.is(helloIterator));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testHelloWorldIteratorRemove(){
        final String helloIterator = "Hello Iterator!";
        HelloWorldCharacterIterator helloWorldCharacterIterator = new HelloWorldCharacterIterator(helloIterator.toCharArray());
        helloWorldCharacterIterator.remove();
    }
}
