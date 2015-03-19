package com.miriamlaurel.hollywood.office;

import com.miriamlaurel.hollywood.Hollywood;
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
        this.secretary = Hollywood.createActor(Secretary.class);
        PostOffice.instance().setLatch(new CountDownLatch(2));
    }

    public @Test void testLifecycle() throws Exception {
        secretary.goToWork();
        Assert.assertTrue(secretary.state() instanceof Secretary.Working);
        Letter letter = new Letter("Dear Mary...");
        secretary.acceptLetter(letter);
        letter = new Letter("Mr. Davies, I am very disappointed...");
        secretary.acceptLetter(letter);
        Call call = new Call("Hello! I'm your new client...");
        secretary.processMessage(call);
        Assert.assertTrue(secretary.state() instanceof Secretary.Working);
        call = new Call("Can you send me your price list by fax?");
        secretary.processMessage(call);
        call = new Call("The work day is over. You can go home now.");
        secretary.processMessage(call);
        PostOffice.instance().getLatch().await();
        Assert.assertEquals(2, PostOffice.instance().getSendLettersCount());
        Assert.assertEquals(3, Phone.instance().getCallCount());
    }
}
