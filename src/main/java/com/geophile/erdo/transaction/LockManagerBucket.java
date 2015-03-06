/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import com.geophile.erdo.AbstractKey;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

// About synchronization: All the public methods here are synchronized. However, synchronization
// only covers the bucket's state. Transaction state can change at any time:
// active -> committed/aborted/deadlocked -> irrelevant.

class LockManagerBucket
{
    @Override
    public String toString()
    {
        return String.format("b%s", bucketNumber);
    }

    // lock() returns normally when the lock on key has been granted to transaction.
    // A TransactionException is thrown if the transaction cannot be granted the lock.
    // This can occur in the following situations:
    // - The key was locked by a transaction that is now committed, but was active when the
    //   current transaction started.
    // - The key was locked by an active transaction when this transaction requested
    //   the lock. When that transaction committed, it marked this transaction one as a conflict
    //   victim.
    // - This transaction was selected as a deadlock victim.
    // Whoever catches the TransactionException needs to initiate the rollback. Rollback
    // will lock buckets, so initiating it here will lead to java deadlock (not transaction
    // deadlock).
    public synchronized void lock(AbstractKey key, Transaction transaction)
        throws InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO,
                    "{0}: {1} locking {2}",
                    new Object[]{this, transaction, key});
        }
        Transaction lockOwner = locks.get(key);
        if (lockOwner == null) {
            locks.put(key, transaction);
        } else if (lockOwner == transaction) {
            // transaction already has the lock
        } else {
            // there is a conflict
            boolean firstAttempt = true;
            do {
                if (lockOwner.hasCommitted()) {
                    transaction.rollback();
                    transaction.throwExceptionDueToTragicEnding();
                } else {
                    LockConflict conflict;
                    if (firstAttempt) {
                        conflict = conflicts.get(key);
                        if (conflict == null) {
                            // First conflict for key
                            conflict = new LockConflict(key, lockOwner);
                            conflicts.put(key, conflict);
                        }
                        conflict.addWaiter(transaction);
                        if (LOG.isLoggable(Level.INFO)) {
                            LOG.log(Level.INFO,
                                    "{0}: {1} waiting for {2}: conflict: {3}",
                                    new Object[]{this, transaction, key, conflict});
                        }
                    }
                    // else: conflict was set following the wait. If we stayed in the loop, then
                    // we didn't throw (following transaction ending tragically), and lockOwner !=
                    // transaction. The assertion in the else below implies that conflict != null.
                    // Also, if we waited once, then we don't want to do conflict.addWaiter again.
                    wait();
                    firstAttempt = false;
                    if (transaction.endedTragically()) {
                        if (LOG.isLoggable(Level.INFO)) {
                            LOG.log(Level.INFO,
                                    "{0}: {1} can''t lock {2} and will abort",
                                    new Object[]{this, transaction, key});
                        }
                        // Remove assuming that there is usually just one waiter, (and it's
                        // being removed). If there are more waiters, conflict will be put back.
                        conflict = conflicts.remove(key);
                        if (conflict != null) {
                            conflict.removeWaiter(transaction);
                            if (conflict.hasWaiters()) {
                                conflicts.put(key, conflict);
                            }
                        }
                        transaction.throwExceptionDueToTragicEnding();
                    } else {
                        // transaction was waiting for the lock. So if the transaction didn't
                        // end tragically (deadlock or conflict with committed), then it must
                        // now be the owner or it must still be waiting. So someone must own
                        // the lock, (lockOwner != null).
                        lockOwner = locks.get(key);
                        assert
                            lockOwner != null
                            : String.format("%s notices no owner for %s", transaction, key);
                        conflict = conflicts.get(key);
                        assert
                            lockOwner == transaction ||
                            conflict != null && conflict.hasWaiter(transaction) 
                            : String.format("transaction: %s, lockOwner: %s, key: %s",
                                            lockOwner, transaction, key);
                    }
                }
            } while (lockOwner != transaction);
        }
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO,
                    "{0}: {1} locked {2}",
                    new Object[]{this, transaction, key});
        }
        LockConflict conflict = conflicts.get(key);
        assert
            conflict == null || !conflict.hasWaiter(transaction)
            : String.format("transaction: %s, key: %s, conflict: %s", transaction, key, conflict);
    }

    public synchronized void abortAllWaiters(Transaction transaction)
    {
        boolean log = !conflicts.isEmpty();
        if (log && LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO,
                    "{0}: abort all waiters for {1}: {2}",
                    new Object[]{this, transaction, conflictsDescription()});
        }
        assert transaction.hasCommitted() : transaction;
        // Clear out all the waiters of every conflict owned by transaction.
        // But leave locks alone until the transaction becomes irrelevant, (commit timestamp
        // less than the start timestamp of all active transactions).
        for (Iterator<Map.Entry<AbstractKey, LockConflict>> conflictScan =
                 conflicts.entrySet().iterator();
             conflictScan.hasNext(); ) {
            LockConflict conflict = conflictScan.next().getValue();
            if (conflict.lockOwner() == transaction) {
                markAllWaitersAsConflictVictims(transaction, conflict);
                conflictScan.remove();
            }
        }
        if (log && LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO,
                    "{0}: aborted all waiters for {1}: {2}",
                    new Object[]{this, transaction, conflictsDescription()});
        }
    }

    public synchronized void releaseLocks(List<AbstractKey> keys)
    {
        for (AbstractKey key : keys) {
            Transaction formerLockOwner = locks.remove(key);
            LockConflict conflict = conflicts.remove(key);
            Transaction formerConflictOwner = conflict == null ? null : conflict.lockOwner();
            assert
                formerConflictOwner == null || formerLockOwner == formerConflictOwner
                : String.format("key: %s, formerLockOwner: %s, conflict: %s",
                                key, formerLockOwner, conflict);
            if (LOG.isLoggable(Level.INFO)) {
                LOG.log(Level.INFO,
                        "{0}: releaseLocks: {1}",
                        new Object[]{this, conflict});
            }
            if (conflict != null) {
                Transaction newLockOwner = conflict.giveLockToFirstWaiter();
                if (LOG.isLoggable(Level.INFO)) {
                    LOG.log(Level.INFO,
                            "{0}: Removed {1} as owner of {2}, new owner is {3}",
                            new Object[]{this, formerConflictOwner, conflict.key(), newLockOwner});
                }
                if (newLockOwner != null) {
                    if (conflict.hasWaiters()) {
                        // Removal was too optimistic
                        conflicts.put(key, conflict);
                    }
                    locks.put(key, newLockOwner);
                }
            }
        }
        // Waiters may have aborted and transactions may have been marked irrelevant.
        // Wake up all blocked threads and give them a chance to make progress.
        notifyAll();
    }

    public synchronized void wakeUp()
    {
        notifyAll();
    }

    public synchronized void findDependencies(Map<Transaction, WaitsFor> dependencies)
    {
        for (LockConflict conflict : conflicts.values()) {
            conflict.findDependencies(dependencies);
        }
    }

    public LockManagerBucket(int bucketNumber)
    {
        this.bucketNumber = bucketNumber;
    }

    private void markAllWaitersAsConflictVictims(Transaction transaction, LockConflict conflict)
    {
        // Transaction committed. All waiters have to abort. Just mark them
        // as being in conflict, but don't actually carry out the abort here.
        // Abort will lock buckets, so this would lead to java deadlock (not transaction 
        // deadlock).
        Collection<Transaction> waiters = conflict.waiters();
        if (LOG.isLoggable(Level.INFO)) {
            LOG.log(Level.INFO,
                    "{0}: Marking transactions waiting on {1} as conflict victims: {2}",
                    new Object[]{this, transaction, waiters});
        }
        for (Transaction waiter : waiters) {
            waiter.rollback();
        }
    }

    public synchronized void describeConflicts(List<String> conflictDescriptions)
    {
        for (LockConflict conflict : conflicts.values()) {
            conflictDescriptions.add(conflict.toString());
        }
    }

    // For use by this class

    private String conflictsDescription()
    {
        StringBuilder buffer = new StringBuilder();
        for (LockConflict conflict : conflicts.values()) {
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            buffer.append(conflict.toString());
        }
        return buffer.toString();
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(LockManagerBucket.class.getName());

    // Object state

    private final int bucketNumber;
    // locks contains an entry for each key whose lock is owned by a transaction.
    // conflicts contains an entry only when there are actual conflicts on the key.
    // This penalizes lock and unlock operations slightly, but limits deadlock detection
    // to the conflicts map, which should normally be much smaller.
    private final Map<AbstractKey, Transaction> locks = new HashMap<>();
    private final Map<AbstractKey, LockConflict> conflicts = new HashMap<>();
}
