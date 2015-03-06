/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import com.geophile.erdo.TestFactory;
import com.geophile.erdo.TestKey;
import org.junit.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;

@Ignore // LockManagerBucket flag to turn off waiting has been discontinued
public class DeadlockTest
{
    @BeforeClass
    public static void beforeClass()
    {
        Transaction.initialize(FACTORY);
        TestThread.transactionManager = TRANSACTION_MANAGER;
    }

    @Before
    public void before()
    {
        lockManager = new LockManager(1);
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void test() throws InterruptedException, IOException
    {
        final AtomicBoolean deadlockFixed = new AtomicBoolean(false);
        Thread thread =
            new Thread()
            {
                @Override
                public void run()
                {
                    try {
                        lockManager.waitOnConflict(false);
                        TestThread t1 = new TestThread(lockManager);
                        TestThread t2 = new TestThread(lockManager);
                        TestKey k1 = key(1);
                        TestKey k2 = key(2);
                        t1.lock(k1);
                        t2.lock(k2);
                        t1.lock(k2);
                        lockManager.waitOnConflict(true);
                        t2.lock(k1);
                    } catch (DeadlockException e) {
                        deadlockFixed.set(true);
                    } catch (Exception e) {
                    }
                }
            };
        thread.start();
        // How to ensure that t2.lock(k1) has definitely happened, since it blocks. This is iffy.
        Thread.sleep(100); 
        lockManager.killDeadlockVictims();
        thread.join();
        assertTrue(deadlockFixed.get());
    }

    private TestKey key(int key)
    {
        return new TestKey(key);
    }

    private static final TestFactory FACTORY = new TestFactory();
    private static final TransactionManager TRANSACTION_MANAGER = new TransactionManager(FACTORY);

    private LockManager lockManager;
}
