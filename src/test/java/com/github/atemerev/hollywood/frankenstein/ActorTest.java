package com.github.atemerev.hollywood.frankenstein;

import com.github.atemerev.hollywood.Hollywood;
import org.junit.Test;
import org.junit.Assert;
import com.github.atemerev.hollywood.johndoe.Clerk;

import static com.github.atemerev.hollywood.Hollywood.createActor;

/**
 * @author Alexander Temerev, Alexander Kuklev
 * @version $Id$
 */
public class ActorTest {
    public @Test void testActor() throws Exception {
        Clerk clerk = createActor(Clerk.class);
        Assert.assertNotNull(clerk);
    }
}
