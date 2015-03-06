/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// synchronization is done by Forest.

public class TransactionRegistry
{
    // TransactionRegistry interface
    
    public void transactionStarted(Transaction transaction)
    {
        assert !transaction.hasTerminated();
        // Monotonically increasing startTimes is guaranteed by synchronization in Forest.
        // Without this property, the min active startTime could go backward, rendering
        // incorrect a decision to remove a transaction from committed. I.e., once a transaction
        // is removed from committed, there must never be a new transaction with a startTime
        // less that the commit timestamp of the removed transaction.
        assert transaction.startTime() > minStartTime();
        LOG.log(Level.INFO, "{0} is starting", transaction);
        Transaction replaced = active.put(transaction.startTime(), transaction);
        assert replaced == null : replaced;
    }
    
    // transactionCommitted/Aborted mark transactions that have become irrelevant by the end
    // of the given transaction, (possibly including the transaction itself) A transaction is 
    // irrelevant if its commit timestamp is less than the minimum start timestamp of all active 
    // transactions.

    public List<Transaction> transactionCommitted(Transaction transaction)
    {
        List<Transaction> irrelevantTransactions;
        assert transaction.hasCommitted() : transaction;
        long previousMinStart = minStartTime();
        Transaction removed = active.remove(transaction.startTime());
        assert removed == transaction : removed;
        committed.put(transaction.commitTime(), transaction);
        if (transaction.startTime() == previousMinStart) {
            // We're removing the active transaction with the min timestamp. Some transactions
            // may be irrelevant as a result.
            irrelevantTransactions = markIrrelevantTransactions(transaction);
        } else {
            if (LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO,
                        "{0} is committing, minStartTime = {1}",
                        new Object[]{transaction, previousMinStart});
            }
            irrelevantTransactions = Collections.emptyList();
        }
        return irrelevantTransactions;
    }

    public List<Transaction> transactionAborted(Transaction transaction)
    {
        List<Transaction> irrelevantTransactions;
        assert transaction.hasAborted() : transaction;
        assert transaction.irrelevant() : transaction;
        long previousMinStart = minStartTime();
        Transaction removed = active.remove(transaction.startTime());
        assert removed == transaction : removed;
        if (transaction.startTime() == previousMinStart) {
            // We're removing the active transaction with the min timestamp. Some transactions
            // may be irrelevant as a result.
            irrelevantTransactions = markIrrelevantTransactions(transaction);
        } else {
            if (LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO,
                        "{0} is aborting, minStartTime = {1}",
                        new Object[]{transaction, previousMinStart});
            }
            irrelevantTransactions = new ArrayList<>();
        }
        irrelevantTransactions.add(transaction);
        return irrelevantTransactions;
    }

    // For testing
    public void reset()
    {
        active.clear();
        committed.clear();
    }

    // For use by this class

    private List<Transaction> markIrrelevantTransactions(Transaction endingTransaction)
    {
        List<Transaction> irrelevantTransactions = new ArrayList<>();
        StringBuilder buffer = null;
        boolean logging = LOG.isLoggable(Level.INFO); 
        if (logging) {
            buffer = new StringBuilder();
        }
        if (active.isEmpty()) {
            for (Transaction transaction : committed.values()) {
                transaction.markIrrelevant();
                irrelevantTransactions.add(transaction);
                if (logging) {
                    buffer.append(transaction.toString());
                    buffer.append(' ');
                }
            }
            committed.clear();
        } else {
            long minStartTime = minStartTime();
            boolean done = false;
            for (Iterator<Map.Entry<Long, Transaction>> committedScan = committed.entrySet().iterator();
                 !done && committedScan.hasNext();) {
                Map.Entry<Long, Transaction> entry = committedScan.next();
                long commitTime = entry.getKey();
                Transaction committedTransaction = entry.getValue();
                if (commitTime < minStartTime) {
                    committedScan.remove();
                    committedTransaction.markIrrelevant();
                    irrelevantTransactions.add(committedTransaction);
                    if (logging) {
                        buffer.append(committedTransaction.toString());
                        buffer.append(' ');
                    }
                } else {
                    done = true;
                }
            }
        }
        if (logging) {
            LOG.log(Level.INFO,
                    "Irrelevant transactions following end of {0}: {1}",
                    new Object[]{endingTransaction, buffer});
        }
        return irrelevantTransactions;
    }

    private long minStartTime()
    {
        return active.isEmpty() ? Long.MIN_VALUE : active.firstKey();
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(TransactionRegistry.class.getName());
    
    // Transactions that have not yet terminated.
    private final SortedMap<Long, Transaction> active = new TreeMap<>();
    // Transactions that have committed, but whose commitTime > min startTime
    // of active transactions.
    private final SortedMap<Long, Transaction> committed = new TreeMap<>();
}
