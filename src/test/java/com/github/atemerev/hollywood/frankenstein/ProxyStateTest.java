package com.github.atemerev.hollywood.frankenstein;

import org.junit.Test;
import org.junit.Assert;
import com.github.atemerev.hollywood.annotations.State;
import com.github.atemerev.hollywood.Actor;
import com.github.atemerev.hollywood.Hollywood;

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
