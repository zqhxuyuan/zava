/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import com.geophile.erdo.TestFactory;
import com.geophile.erdo.TestKey;
import org.junit.*;

import java.util.Set;

import static org.junit.Assert.*;

@Ignore // LockManagerBucket flag to turn off waiting has been discontinued
public class DeadlockDetectorTest
{
    @BeforeClass
    public static void beforeClass()
    {
        Transaction.initialize(FACTORY);
    }

    @Before
    public void before()
    {
        lockManager = new LockManager();
        lockManager.waitOnConflict(false);
        a = new TestThread(lockManager);
        b = new TestThread(lockManager);
        c = new TestThread(lockManager);
        d = new TestThread(lockManager);
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testEmpty()
    {
        DeadlockDetector deadlockDetector = new DeadlockDetector(lockManager);
        assertTrue(deadlockDetector.victims().isEmpty());
    }

    @Test
    public void testOneWaiter() throws Exception
    {
        a.lock(key(0));
        DeadlockDetector deadlockDetector = new DeadlockDetector(lockManager);
        assertTrue(deadlockDetector.victims().isEmpty());
    }

    @Test
    public void testDeadlockOf2() throws Exception
    {
        TestKey k0 = key(0);
        TestKey k1 = key(1);
        a.lock(k0);
        b.lock(k1);
        a.lock(k1);
        b.lock(k0);
        DeadlockDetector deadlockDetector = new DeadlockDetector(lockManager);
        Set<Transaction> victims = deadlockDetector.victims();
        assertEquals(1, victims.size());
        assertEquals(latestStart(a, b).transaction(), victims.iterator().next());
    }
    
    @Test
    public void testDeadlockOf3() throws Exception
    {
        TestKey k0 = key(0);
        TestKey k1 = key(1);
        TestKey k2 = key(2);
        a.lock(k0);
        b.lock(k1);
        c.lock(k2);
        a.lock(k1);
        b.lock(k2);
        c.lock(k0);
        DeadlockDetector deadlockDetector = new DeadlockDetector(lockManager);
        Set<Transaction> victims = deadlockDetector.victims();
        assertEquals(1, victims.size());
        assertEquals(latestStart(a, b, c).transaction(), victims.iterator().next());
    }

    @Test
    public void testDeadlockWithPileup() throws Exception
    {
        TestKey k1 = key(1);
        TestKey k2 = key(2);
        b.lock(k1);
        c.lock(k2);
        b.lock(k2);
        c.lock(k1);
        a.lock(k1);
        DeadlockDetector deadlockDetector = new DeadlockDetector(lockManager);
        Set<Transaction> victims = deadlockDetector.victims();
        assertEquals(1, victims.size());
        assertEquals(latestStart(b, c).transaction(), victims.iterator().next());
    }
    
    @Test
    public void testTwoDeadlocks() throws Exception
    {
        TestKey k0 = key(0);
        TestKey k1 = key(1);
        TestKey k2 = key(2);
        TestKey k3 = key(3);
        a.lock(k0);
        b.lock(k1);
        a.lock(k1);
        b.lock(k0);
        c.lock(k2);
        d.lock(k3);
        c.lock(k3);
        d.lock(k2);
        DeadlockDetector deadlockDetector = new DeadlockDetector(lockManager);
        Set<Transaction> victims = deadlockDetector.victims();
        int count = 0;
        for (Transaction victim : victims) {
            if (victim == latestStart(a, b).transaction() || victim == latestStart(c, d).transaction()) {
                count++;
            } else {
                fail();
            }
        }
        assertEquals(2, count);
    }

    private TestThread latestStart(TestThread... threads)
    {
        TestThread earliest = null;
        for (TestThread thread : threads) {
            if (earliest == null ||
                thread.transaction().startTime() > earliest.transaction().startTime()) {
                earliest = thread;
            }
        }
        return earliest;
    }

    private TestKey key(int key)
    {
        return new TestKey(key);
    }

    private static final TestFactory FACTORY = new TestFactory();

    private LockManager lockManager;
    private TestThread a;
    private TestThread b;
    private TestThread c;
    private TestThread d;
}
