/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.TransactionCallback;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.transactionalmap.TransactionalMap;

import java.io.IOException;
import java.util.List;

public class TransactionManager
{
    public final Transaction currentTransaction()
    {
        Transaction transaction = THREAD_TRANSACTION.get();
        if (transaction == null) {
            synchronized (this) {
                transaction = Transaction.newTransaction(this, newTransactionalMap());
                transactionRegistry.transactionStarted(transaction);
            }
            THREAD_TRANSACTION.set(transaction);
        }
        return transaction;
    }

    public final void lock(AbstractKey key)
        throws InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        Transaction transaction = currentTransaction();
        key.transaction(transaction);
        transaction.waitingFor(key);
        try {
            lockManager().lock(key, transaction);
        } catch (DeadlockException | TransactionRolledBackException e) {
            rollbackTransaction();
            throw e;
        } finally {
            transaction.doneWaitingForKey();
        }
    }

    // TODO: What sort of cleanup should be done in case of exceptions thrown during
    // TODO: commitTransactionAsynchronously and rollbackTransaction?

    public final void commitTransaction(TransactionCallback transactionCallback, Object commitInfo)
        throws IOException, InterruptedException
    {
        Transaction transaction = THREAD_TRANSACTION.get();
        if (transaction != null) {
            List<Transaction> irrelevantTransactions;
            synchronized (this) {
                transaction.commit(transactionCallback, commitInfo);
                makeUpdatesPublic(transaction);
                irrelevantTransactions = transactionRegistry.transactionCommitted(transaction);
            }
            lockManager().transactionCommitted(transaction, irrelevantTransactions);
            destroyIrrelevantTransactions(irrelevantTransactions);
            THREAD_TRANSACTION.remove();
        }
        // else: A transaction was not started
    }

    public final void rollbackTransaction()
    {
        Transaction transaction = THREAD_TRANSACTION.get();
        if (transaction != null) {
            List<Transaction> irrelevantTransactions;
            synchronized (this) {
                transaction.rollback();
                irrelevantTransactions = transactionRegistry.transactionAborted(transaction);
            }
            lockManager().transactionAborted(transaction, irrelevantTransactions);
            destroyIrrelevantTransactions(irrelevantTransactions);
            THREAD_TRANSACTION.remove();
        }
        // else: A transaction was not started
    }

    public Factory factory()
    {
        return factory;
    }

    public void makeUpdatesPublic(Transaction transaction)
        throws IOException, InterruptedException
    {
    }

    public TransactionalMap newTransactionalMap()
    {
        return null;
    }

    public TransactionManager(Factory factory)
    {
        this.factory = factory;
        THREAD_TRANSACTION.remove();
    }

    // For testing
    public void clearThreadState()
    {
        THREAD_TRANSACTION.remove();
        transactionRegistry.reset();
    }

    protected LockManager lockManager()
    {
        return factory.lockManager();
    }

    // For use by this class

    private void destroyIrrelevantTransactions(List<Transaction> irrelevantTransactions)
    {
        for (Transaction transaction : irrelevantTransactions) {
            transaction.destroy();
        }
    }

    // Class state

    protected static final ThreadLocal<Transaction> THREAD_TRANSACTION = new ThreadLocal<>();

    // Object state

    protected final Factory factory;
    protected final TransactionRegistry transactionRegistry = new TransactionRegistry();
}
