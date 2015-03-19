package com.miriamlaurel.hollywood.frankenstein;

import org.junit.Test;
import org.junit.Assert;
import com.miriamlaurel.hollywood.annotations.State;
import com.miriamlaurel.hollywood.Actor;
import com.miriamlaurel.hollywood.Hollywood;

/**
 * @author Alexander Temerev, Alexander Kuklev
 * @version $Id$
 */
public class ProxyStateTest {

    public @Test void testAutoProxy() throws Exception {
        ScaryState state = Hollywood.loadStateInstance(ScaryState.class, false);
        Assert.assertNotNull(state);
        try {
            int scareLeval = state.getScareLevel();
            Assert.fail("Pidarasy!");
        } catch (UnsupportedOperationException e) {
            System.out.println("Cool! " + e.getMessage());
        }
        Assert.assertEquals(10, state.getWarmLevel());
    }

    public @State static abstract class ScaryState extends Actor {

        public abstract int getScareLevel();

        public int getWarmLevel() {
            return 10;
        }
    }
}
