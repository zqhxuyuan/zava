/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.systemtest.lockmanagement;

import com.geophile.erdo.transaction.*;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LockManagerTestThread extends Thread
{
    public void run()
    {
        int deadlockAborts = 0;
        int conflictAborts = 0;
        try {
            for (int i = 0; i < transactions; i++) {
                boolean committed = false;
                do {
                    transaction = transactionManager.currentTransaction();
                    int from;
                    int to;
                    do {
                        from = random.nextInt(accounts.length);
                        to = random.nextInt(accounts.length);
                    } while (from == to);
                    int amount = MIN_AMOUNT + random.nextInt(MAX_AMOUNT);
                    AccountId fromAccountId = new AccountId(from);
                    AccountId toAccountId = new AccountId(to);
                    try {
                        lock(fromAccountId);
                        lock(toAccountId);
                        accounts[from].set(accounts[from].get() - amount);
                        accounts[to].set(accounts[to].get() + amount);
                        transactionManager.commitTransaction(null, null);
                        committed = true;
                    } catch (DeadlockException e) {
                        LOG.log(Level.INFO, "{0}: deadlock", threadId);
                        deadlockAborts++;
                    } catch (TransactionRolledBackException e) {
                        LOG.log(Level.FINE, "{0}: conflict", threadId);
                        conflictAborts++;
                    }
                } while (!committed);
            }
        } catch (Throwable th) {
            LOG.log(Level.WARNING,
                    "Test thread ended by exception while working on {0}",
                    transaction);
            LOG.log(Level.WARNING, "Cause", th);
            termination = th;
        } finally {
            System.out.println(
                String.format("%s transactions, %s deadlock aborts, %s conflict aborts, termination: %s",
                              transactions,
                              deadlockAborts,
                              conflictAborts,
                              (termination == null ? null : termination.getMessage())));
        }
    }
    
    public Throwable termination()
    {
        return termination;
    }
    
    public LockManagerTestThread(int threadId,
                                 TransactionManager transactionManager,
                                 LockManager lockManager,
                                 AtomicLong[] accounts,
                                 int transactions) 
    {
        this.threadId = threadId;
        this.transactionManager = transactionManager;
        this.lockManager = lockManager;
        this.accounts = accounts;
        this.transactions = transactions;
        this.random = new Random(System.nanoTime());
    }
    
    private void lock(AccountId accountId) 
        throws DeadlockException,
               TransactionRolledBackException,
               InterruptedException
    {
        transaction.waitingFor(accountId);
        try {
            lockManager.lock(accountId, transaction);
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING,
                    "Caught InterruptedException while locking {0}",
                    new Object[]{accountId, e});
        } catch (DeadlockException | TransactionRolledBackException e) {
            transactionManager.rollbackTransaction();
            throw e;
        } finally {
            transaction.doneWaitingForKey();
        }
    }

    private static final Logger LOG = Logger.getLogger(LockManagerTestThread.class.getName());
    private static final int MIN_AMOUNT = 1;
    private static final int MAX_AMOUNT = 100;

    private final int threadId;
    private final int transactions;
    private final AtomicLong[] accounts;
    private final TransactionManager transactionManager;
    private final LockManager lockManager;
    private Transaction transaction;
    private final Random random;
    private Throwable termination;
}
