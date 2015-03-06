/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.TestKey;

class TestThread extends Thread
{
    // Thread interface
    
    public void run()
    {
        transaction = transactionManager.currentTransaction();
        while (true) {
            synchronized (this) {
                while (action == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                exception = null;
                try {
                    switch (action) {
                        case LOCK:
                            transaction.waitingFor(key);
                            lockManager.lock(key, transaction);
                            transaction.doneWaitingForKey();
                            break;
                        case COMMIT:
                            transactionManager.commitTransaction(null, null);
                            break;
                        case ABORT:
                            transactionManager.rollbackTransaction();
                            break;
                    }
                } catch (Exception e) {
                    exception = e;
                }
                action = null;
                // Because commit and rollback start new transactions
                transaction = transactionManager.currentTransaction();
                notify();
            }
        }
    }
    
    public synchronized void lock(TestKey key) throws Exception
    {
        action = Action.LOCK;
        this.key = key;
        notify();
        while (this.action != null) {
            wait();
        }
        if (exception != null) {
            throw exception;
        }
    }

    public synchronized void commit() throws Exception
    {
        action = Action.COMMIT;
        notify();
        while (this.action != null) {
            wait();
        }
        if (exception != null) {
            throw exception;
        }
    }

    public synchronized void abort() throws Exception
    {
        action = Action.ABORT;
        notify();
        while (this.action != null) {
            wait();
        }
        if (exception != null) {
            throw exception;
        }
    }
    
    public Transaction transaction()
    {
        return transaction;
    }
    
    public TestThread(LockManager lockManager)
    {
        this.lockManager = lockManager;
        setDaemon(true);
        start();
    }

    static TransactionManager transactionManager;

    private final LockManager lockManager;
    private volatile AbstractKey key;
    private volatile Action action;
    private volatile Exception exception;
    private volatile Transaction transaction;

    enum Action
    {
        LOCK, COMMIT, ABORT
    }
}
