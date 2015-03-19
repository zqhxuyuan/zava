package com.miriamlaurel.hollywood.frankenstein;

import org.junit.Test;
import org.junit.Assert;
import com.miriamlaurel.hollywood.johndoe.Clerk;
import com.miriamlaurel.hollywood.Hollywood;

/**
 * @author Alexander Temerev, Alexander Kuklev
 * @version $Id$
 */
public class ActorTest {
    public @Test void testActor() throws Exception {
        Clerk clerk = Hollywood.createActor(Clerk.class);
        Assert.assertNotNull(clerk);
    }
}
