package com.github.atemerev.hollywood.office;

import com.github.atemerev.hollywood.Hollywood;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class OfficeTest {

    private Secretary secretary;

    public @Before void init() {
        //1.创建Actor: Actor是对某种事件做出响应的实体. 比如人会对特定事件做出特定反应.
        //这里举例邮政公司的秘书为例. 秘书要处理各种各样的事件.
        //在处理事件前中后,都会有特定的状态,而导致其他事件无法进行.
        //事件状态发生变化都要能够及时通知为外部事件.
        this.secretary = Hollywood.createActor(Secretary.class);

        //初始化计数器=2
        PostOffice.instance().setLatch(new CountDownLatch(2));
    }

    public @Test void testLifecycle() throws Exception {
        secretary.goToWork();
        Assert.assertTrue(secretary.state() instanceof Secretary.Working);

        //接收2个新建
        Letter letter = new Letter("Dear Mary...");
        secretary.acceptLetter(letter);

        letter = new Letter("Mr. Davies, I am very disappointed...");
        secretary.acceptLetter(letter);

        //第一个电话打进来, 状态由上班中-->正在通话中. 上班中才可以接听新电话. 正在通话中不可以接听新的电话
        //因为Actor是消息传递机制,所以后面2个电话打进来时,都会占线
        Call call = new Call("Hello! I'm your new client...");
        secretary.processMessage(call);
        //第一个电话接听完毕,状态变为上班中,可以接收新的电话
        Assert.assertTrue(secretary.state() instanceof Secretary.Working);

        //第二个电话中包含fax关键词,接完电话后,还要去发送传真,才可以开始接听第三个电话
        call = new Call("Can you send me your price list by fax?");
        secretary.processMessage(call);

        call = new Call("The work day is over. You can go home now.");
        secretary.processMessage(call);

        //等待计数器归零. 什么时候计数器会减少: 发送信件的时候! <--Working状态要退出前
        PostOffice.instance().getLatch().await();
        //通过计数器确保最后的2行代码最后才执行! 验证一天的工作量是否符合.
        Assert.assertEquals(2, PostOffice.instance().getSendLettersCount());
        Assert.assertEquals(3, Phone.instance().getCallCount());
    }
}
