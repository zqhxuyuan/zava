package com.github.atemerev.hollywood.johndoe;

import com.github.atemerev.hollywood.Hollywood;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alexander Temerev
 * @version $Id$
 */
public class JohnDoeTest {

    public @Test void testJohn() {
        Clerk johnDoe = Hollywood.createActor(Clerk.class);
        johnDoe.goToJob();

        Assert.assertTrue("Default state should be at work", johnDoe.state() instanceof Clerk.AtWork);
        Assert.assertEquals("Energy should be 10", 10, (int) johnDoe.getEnergyLevel());
        Assert.assertEquals("AtWork", johnDoe.state());
        johnDoe.talk();
        johnDoe.goHome();
        Assert.assertTrue("New state is at home", johnDoe.state() instanceof Clerk.AtHome);
        Assert.assertEquals("Energy should be 10", 5, (int) johnDoe.getEnergyLevel());
        Assert.assertEquals("AtHome", johnDoe.state());
        johnDoe.talk();

        Assert.assertTrue(true);
    }
}