package com.miriamlaurel.hollywood.jekyll;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Alexander Temerev
 */
public class JekyllTest {
    public @Test void testCreate() {
        JekyllHyde jekyll = JekyllHyde.create();
        Assert.assertNotNull("Actor must not be null", jekyll);
        Assert.assertTrue("Initial state must be active", jekyll.state() instanceof JekyllHyde.Jekyll);
        Assert.assertTrue("Duplicate state works", jekyll.state() instanceof JekyllHyde.Jekyll);
        try {
            ((JekyllHyde) jekyll.state()).say();
            Assert.fail("Should throw exception!");
        } catch (UnsupportedOperationException e) {
            // OK!
        }
    }

    public @Test void testPublic() {
        JekyllHyde jekyll = JekyllHyde.create();
        Assert.assertTrue("Initial state must be active", jekyll.state() instanceof JekyllHyde.Jekyll);
        String say = jekyll.say();
        Assert.assertEquals("My name is Jekyll, I am a Ph.D. in chemistry.", say);
        Assert.assertEquals("test", jekyll.say("TEst"));
        jekyll.transform();
        Assert.assertTrue("Hyde state must be now active", jekyll.state() instanceof JekyllHyde.Hyde);
        say = jekyll.say();
        Assert.assertEquals("AAAAAAARRRRRRRGGMMMHHH!!!!11", say);
        Assert.assertEquals("TEST", jekyll.say("TEst"));
        jekyll.transform();
        say = jekyll.say();
        Assert.assertEquals("My name is Jekyll, I am a Ph.D. in chemistry.", say);
        Assert.assertTrue("Jekyll state must be now active", jekyll.state() instanceof JekyllHyde.Jekyll);
        try {
            jekyll.killAllHumans();
            Assert.fail("Should throw exception!");
        } catch (UnsupportedOperationException e) {
            // OK!
        }
    }
}
