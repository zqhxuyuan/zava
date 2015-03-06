/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.Cursor;
import com.geophile.erdo.TransactionCallback;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.transactionalmap.TransactionalMap;
import com.geophile.erdo.util.IdentitySet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

// Transaction times and timestamps:
//
// startTime and commitTime are used to manage the TransactionRegistry.
// timestamp is the timestamp of a committed transactions, indicates serialization order,
// and is used for record versioning. commitTime order must be consistent with timestamp order.
// However, commitTime cannot substitute for timestamp. Manifests store timestamps. Because
// timestamps are dense, they compress better.
//
// commitTime order is consistent with timestamp order because Transaction.commit is called by
// TransactionManager.commitTransactionAsynchronously inside the TransactionManager's monitor.

public class Transaction
{
    // Object interface

    @Override
    public String toString()
    {
        StringBuilder buffer = new StringBuilder();
        buffer.append("txn(");
        buffer.append(times.startTime);
        buffer.append(')');
        if (termination != null) {
            switch (termination) {
                case COMMITTED:
                    buffer.append('(');
                    buffer.append(times.commitTime);
                    buffer.append(")ts");
                    buffer.append(times.timestamp);
                    break;
                case ABORTED:
                    buffer.append('A');
                    break;
                case DEADLOCK_VICTIM:
                    buffer.append('D');
                    break;
            }
        }
        if (irrelevant) {
            buffer.append('i');
        }
        return buffer.toString();
    }

    @Override
    public int hashCode()
    {
        long h = startTime() * 9987001;
        return (int) (h >> 32) | (int) h;
    }

    @Override
    public boolean equals(Object o)
    {
        return o != null && o instanceof Transaction && startTime() == ((Transaction) o).startTime();
    }

    // Transaction interface

    public long startTime()
    {
        return times.startTime();
    }

    public long commitTime()
    {
        return times.commitTime();
    }
    
    public long timestamp()
    {
        return times.timestamp();
    }

    public synchronized void markDurable()
    {
        if (callback != null) {
            try {
                callback.whenDurable(commitInfo);
            } finally {
                commitInfo = null;
            }
        }
    }

    public boolean synchronousCommit()
    {
        return callback == null;
    }

    public void markDeadlockVictim()
    {
        LOG.log(Level.INFO, "Marking {0} as DEADLOCK_VICTIM", this);
        assert termination == null : this;
        termination = Termination.DEADLOCK_VICTIM;
        irrelevant = true;
    }

    public void markIrrelevant()
    {
        LOG.log(Level.INFO, "Marking {0} as irrelevant", this);
        assert termination != null : this;
        assert hasTerminated() : this;
        irrelevant = true;
    }

    public boolean endedTragically()
    {
        return termination == Termination.ABORTED || termination == Termination.DEADLOCK_VICTIM;
    }

    public boolean hasAborted()
    {
        return termination != null && termination != Termination.COMMITTED;
    }
    
    public boolean hasCommitted()
    {
        return termination == Termination.COMMITTED;
    }

    public boolean hasTerminated()
    {
        return termination != null;
    }
    
    public boolean irrelevant()
    {
        return irrelevant;
    }
    
    public void waitingFor(AbstractKey key)
    {
        assert waitingFor == null : waitingFor;
        waitingFor = key;
    }
    
    public void doneWaitingForKey()
    {
        if (!hasTerminated()) {
            lockedForWrite.add(waitingFor);
        }
        waitingFor = null;
    }
    
    public Set<AbstractKey> lockedForWrite()
    {
        return lockedForWrite;
    }

    public void registerCursor(Cursor cursor)
    {
        Cursor replaced = openCursors.add(cursor);
        assert replaced == null;
    }

    public void unregisterCursor(Cursor cursor)
    {
        if (!ending) {
            Cursor removed = openCursors.remove(cursor);
            assert removed == cursor;
        }
    }

    // For testing
    public IdentitySet<Cursor> openCursors()
    {
        return openCursors;
    }

    public void throwExceptionDueToTragicEnding()
        throws DeadlockException, TransactionRolledBackException
    {
        if (termination == Termination.DEADLOCK_VICTIM) {
            throw new DeadlockException(this);
        } else if (termination == Termination.ABORTED) {
            throw new TransactionRolledBackException(this);
        } else {
            assert false : this;
        }
    }
    
    public TransactionalMap transactionalMap()
    {
        return transactionalMap;
    }

    public void commit(TransactionCallback callback, Object commitInfo)
        throws IOException, InterruptedException
    {
        assert Thread.holdsLock(transactionManager) : this;
        closeOpenScans();
        assert termination == null : this;
        assert waitingFor == null
            : String.format("Can''t commit %s because it is waiting for %s", this, waitingFor);
        this.termination = Termination.COMMITTED;
        this.callback = callback;
        this.commitInfo = commitInfo;
        this.times.setCommitTimeAndTimestamp();
        if (LOG.isLoggable(Level.INFO)) {
            if (commitInfo == null) {
                LOG.log(Level.INFO, "Committing transaction {0}", this);
            } else {
                LOG.log(Level.INFO, "Committing transaction {0}: {1}", new Object[]{this, commitInfo});
            }
        }
    }

    public void rollback()
    {
        // DON'T assert Thread.holdsLock(transactionManager). rollback can be initiated by
        // another transaction noticing that this transaction can't succeed, e.g. due to a
        // lock conflict.
        //
        // Why test termination: There is a race between:
        // - A transaction committing and aborting its waiters, and
        // - Another transaction requesting a lock owned by this transaction, seeing
        //   the commit and aborting itself.
        // So termination could already be set to ABORTED.
        if (termination == null) {
            closeOpenScans();
            termination = Termination.ABORTED;
            irrelevant = true;
            if (LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO, "Aborting transaction {0}: {1}", new Object[]{this, termination});
            }
        } else {
            assert
                termination == Termination.ABORTED || termination == Termination.DEADLOCK_VICTIM
                : this;
        }
    }

    public void destroy()
    {
        LOG.log(Level.INFO, "Destroying {0}", this);
        if (transactionalMap() != null) {
            transactionalMap.cleanup();
        } // else: Tests of transaction management (only) don't use transactionalMaps.
        transactionalMap = null;
        lockedForWrite = null;
        waitingFor = null;
    }

    public static synchronized void initialize(Factory factory)
    {
        Transaction.factory = factory;
    }

    public static Transaction newTransaction(TransactionManager transactionalManager,
                                             TransactionalMap transactionalMap)
    {
        assert Thread.holdsLock(transactionalManager);
        return new Transaction(transactionalManager, transactionalMap);
    }
    
    // For use by this class

    private void closeOpenScans()
    {
        ending = true;
        for (Cursor cursor : openCursors.values()) {
            cursor.close();
        }
        openCursors = null;
    }

    private Transaction(TransactionManager transactionManager, TransactionalMap transactionalMap)
    {
        // transactionalMap is null in some tests
        assert transactionManager != null;
        this.transactionManager = transactionManager;
        this.transactionalMap = transactionalMap;
        this.times = new Times(factory.nextTransactionTime());
    }
    
    // Class state

    // Why UNCOMMITTED_TIMESTAMP is Long.MAX_VALUE: timestamp is initialized to this value, and keeps this
    // value until the transaction is committed. This value will cause the uncommitted transaction,
    // part of a PrivateMap, to be ranked after all committed timestamps.
    public static final long UNCOMMITTED_TIMESTAMP = Long.MAX_VALUE;
    private static final Logger LOG = Logger.getLogger(Transaction.class.getName());
    private static Factory factory;

    // Object state

    private final TransactionManager transactionManager;
    private TransactionalMap transactionalMap;
    private Set<AbstractKey> lockedForWrite = new HashSet<>();
    private final Times times;
    private volatile TransactionCallback callback;
    private volatile Object commitInfo;
    private volatile AbstractKey waitingFor;
    private volatile Termination termination;
    private volatile boolean irrelevant = false;
    private IdentitySet<Cursor> openCursors = new IdentitySet<>();
    // unregisterCursor is called from CursorImpl.close. If CursorImpl.close is called from Transaction.closeOpenScans,
    // then we get a ConcurrentModificationException on openCursors. If we are in closeOpenScans, then openCursors is
    // about to become irrelevant. This ending variable is used to skip doing the openCursors maintenance in
    // unregisterCursor.
    private boolean ending = false;

    // Inner classes
    
    private enum Termination 
    {
        COMMITTED,
        ABORTED,
        DEADLOCK_VICTIM
    }

    public static class Times
    {
        public String toString()
        {
            return String.format("Times(start = %s, commit = %s, timestamp = %s)", 
                                 startTime, commitTime, timestamp);
        }
        
        public long startTime()
        {
            return startTime;
        }
        
        public long commitTime()
        {
            return commitTime;
        }
        
        public long timestamp()
        {
            return timestamp;
        }
        
        public void setCommitTimeAndTimestamp()
        {
            assert commitTime == UNCOMMITTED_TIMESTAMP : this;
            assert timestamp == UNCOMMITTED_TIMESTAMP : this;
            commitTime = factory.nextTransactionTime();
            timestamp = factory.nextTransactionTimestamp();
        }

        public Times(long startTime)
        {
            this.startTime = startTime;
        }
        
        private final long startTime;
        private long commitTime = UNCOMMITTED_TIMESTAMP;
        private long timestamp = UNCOMMITTED_TIMESTAMP;
    }
}
