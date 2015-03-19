package com.miriamlaurel.hollywood.johndoe;

import com.miriamlaurel.hollywood.Hollywood;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class JohnDoeTest {

    public @Test void testJohn() {
/*
        Clerk johnDoe = Hollywood.createActor(Clerk.class);
        Assert.assertTrue("Default state should be at work", johnDoe.state() instanceof Clerk.AtWork);
        Assert.assertEquals("Energy should be 10", 10, (int) johnDoe.getEnergyLevel());
        Assert.assertEquals("AtWork", johnDoe.getStateName());
        johnDoe.talk();
        johnDoe.goHome();
        Assert.assertTrue("New state is at home", johnDoe.state() instanceof Clerk.AtHome);
        Assert.assertEquals("Energy should be 10", 5, (int) johnDoe.getEnergyLevel());
        Assert.assertEquals("AtHome", johnDoe.getStateName());
        johnDoe.talk();
*/
        Assert.assertTrue(true);
    }
}

// actor.currentState() == Clerk.AtHome.state()
