/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.systemtest.lockmanagement;

import com.geophile.erdo.Database;
import com.geophile.erdo.OrderedMap;
import com.geophile.erdo.TransactionCallback;
import com.geophile.erdo.TransactionRolledBackException;
import com.geophile.erdo.apiimpl.DatabaseImpl;
import com.geophile.erdo.transaction.Transaction;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ErdoTestThread extends Thread
{
    public void run()
    {
        int deadlockAborts = 0;
        int conflictAborts = 0;
        try {
            for (int i = 0; i < nTransactions; i++) {
                boolean committed = false;
                StringBuilder description = new StringBuilder();
                do {
                    transaction = transaction();
                    int from;
                    int to;
                    do {
                        from = random.nextInt(nAccounts);
                        to = random.nextInt(nAccounts);
                    } while (from == to);
                    int amount = MIN_AMOUNT + random.nextInt(MAX_AMOUNT);
                    AccountId fromAccountId = new AccountId(from);
                    AccountId toAccountId = new AccountId(to);
                    try {
                        Account fromAccount = account(fromAccountId);
                        Account toAccount = account(toAccountId);
                        if (LOG.isLoggable(Level.INFO)) {
                            LOG.log(Level.INFO,
                                    "{0}: Attempt transfer of {1} from {2} to {3}",
                                     new Object[]{transaction, amount, fromAccountId, toAccountId});
                        }
                        description.setLength(0);
                        description.append(String.format("%s, %s", fromAccount, toAccount));
                        fromAccount.balance(fromAccount.balance() - amount);
                        toAccount.balance(toAccount.balance() + amount);
                        description.append(String.format(" -> %s, %s", fromAccount, toAccount));
                        accounts.ensurePresent(fromAccount);
                        accounts.ensurePresent(toAccount);
                        db.commitTransactionAsynchronously(transactionCallback);
                        committed = true;
                        if (LOG.isLoggable(Level.INFO)) {
                            LOG.log(Level.INFO,
                                    "{0}: Transferred {1}: {2}",
                                    new Object[]{transaction, amount, description});
                        }
                    } catch (com.geophile.erdo.transaction.DeadlockException e) {
                        LOG.log(Level.INFO,
                                "{0}: transfer of {1} from {2} to {3} failed due to deadlock",
                                new Object[]{transaction, amount, fromAccountId, toAccountId});
                        deadlockAborts++;
                    } catch (TransactionRolledBackException e) {
                        LOG.log(Level.INFO,
                                "{0}: transfer of {1} from {2} to {3} failed due to conflict",
                                new Object[]{transaction, amount, fromAccountId, toAccountId});
                        conflictAborts++;
                    }
                } while (!committed);
            }
        } catch (Throwable th) {
            LOG.log(Level.SEVERE, "Caught exception", th);
            termination = th;
        } finally {
            LOG.log(Level.INFO,
                    "{0} transactions, {1} deadlock aborts, {2} conflict aborts, termination: {3}",
                    new Object[]{nTransactions,
                                 deadlockAborts,
                                 conflictAborts,
                                 (termination == null ? null : termination.getMessage())});
        }
    }

    public Throwable termination()
    {
        return termination;
    }

    public ErdoTestThread(int threadId, 
                          Database db, 
                          OrderedMap accounts, 
                          int nTransactions, 
                          int nAccounts)
    {
        this.threadId = threadId;
        this.db = db;
        this.accounts = accounts;
        this.nTransactions = nTransactions;
        this.nAccounts = nAccounts;
        this.random = new Random(System.nanoTime());
    }
    
    private Account account(AccountId accountId) throws IOException, InterruptedException
    {
        return (Account) accounts.find(accountId);
    }
    
    private Transaction transaction()
    {
        return ((DatabaseImpl)db).factory().transactionManager().currentTransaction();
    }
    
    private static final int MIN_AMOUNT = 1;
    private static final int MAX_AMOUNT = 100;
    private static final Logger LOG = Logger.getLogger(ErdoTestThread.class.getName());

    private final int threadId;
    private final int nTransactions;
    private final int nAccounts;
    private final Database db;
    private final OrderedMap accounts;
    private final Random random;
    private Throwable termination;
    private Transaction transaction;
    private volatile int commitCount = 0;
    private final TransactionCallback transactionCallback =
        new TransactionCallback()
        {
            public void whenDurable(Object commitInfo)
            {
            }

            public void whenSuspect(Object commitInfo)
            {
                LOG.log(Level.SEVERE, "Suspect transaction!");
            }
        };
}
