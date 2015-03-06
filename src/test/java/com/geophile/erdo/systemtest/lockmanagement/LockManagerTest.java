/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.systemtest.lockmanagement;

import com.geophile.erdo.map.Factory;
import com.geophile.erdo.transaction.LockManager;
import com.geophile.erdo.transaction.Transaction;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class LockManagerTest
{
    public static void main(String[] args) throws InterruptedException
    {
        new LockManagerTest(args).run();
    }

    private LockManagerTest(String[] args)
    {
        int a = 0;
        this.accounts = new AtomicLong[Integer.parseInt(args[a++])];
        int lockManagerBuckets = Integer.parseInt(args[a++]);
        this.lockManager = new LockManager(lockManagerBuckets);
        this.factory = new TestFactory(lockManager);
        this.threads = new LockManagerTestThread[Integer.parseInt(args[a++])];
        this.transactionsPerThread = Integer.parseInt(args[a++]);
        System.out.println(String.format("accounts: %s", accounts.length));
        System.out.println(String.format("buckets: %s", lockManagerBuckets));
        System.out.println(String.format("threads: %s", threads.length));
        System.out.println(String.format("transactionsPerThread: %s", transactionsPerThread));
    }

    private void run() throws InterruptedException
    {
        setupTransactionManagement();
        createAccounts();
        createThreads();
        startThreads();
        waitForCompletion();
        verify();
    }

    private void createAccounts()
    {
        for (int a = 0; a < accounts.length; a++) {
            accounts[a] = new AtomicLong(0);
        }
    }

    private void createThreads()
    {
        for (int t = 0; t < threads.length; t++) {
            threads[t] = new LockManagerTestThread(t,
                                                   factory.transactionManager(),
                                                   lockManager,
                                                   accounts,
                                                   transactionsPerThread);
        }
    }

    private void setupTransactionManagement()
    {
        Transaction.initialize(factory);
        ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(
                1,
                new ThreadFactory()
                {
                    public Thread newThread(Runnable runnable)
                    {
                        Thread thread = new Thread(runnable);
                        thread.setDaemon(true);
                        thread.setName("DEADLOCK_DETECTOR");
                        return thread;
                    }
                });
        scheduledExecutorService.scheduleWithFixedDelay(
            new Runnable()
            {
                public void run()
                {
                    try {
                        lockManager.killDeadlockVictims();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },
            20,
            20,
            TimeUnit.MILLISECONDS);
    }

    private void startThreads()
    {
        for (LockManagerTestThread thread : threads) {
            thread.start();
        }
    }

    private void waitForCompletion() throws InterruptedException
    {
        for (LockManagerTestThread thread : threads) {
            thread.join();
        }
    }

    private void verify()
    {
        long sum = 0;
        for (int a = 0; a < accounts.length; a++) {
            long balance = accounts[a].get();
            System.out.println(String.format("%s: %s", a, balance));
            sum += balance;
        }
        
        for (AtomicLong account : accounts) {
            long balance = account.get();
            sum += balance;
        }
        if (sum == 0) {
            System.out.println("OK!");
        } else {
            System.out.println(String.format("Test failed, sum = %s", sum));
        }
    }

    private final Factory factory;
    private final AtomicLong[] accounts;
    private final LockManager lockManager;
    private final LockManagerTestThread[] threads;
    private final int transactionsPerThread;
}
