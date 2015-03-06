/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import com.geophile.erdo.AbstractKey;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Map;

class LockConflict
{
    // Object interface
    
    @Override
    public String toString()
    {
        return String.format("LockConflict(%s: owner: %s, waiting: %s)", key, lockOwner, waiting);
    }
    
    // LockConflict interface
    
    public AbstractKey key()
    {
        return key;
    }
    
    public Transaction lockOwner()
    {
        return lockOwner;
    }
    
    public boolean hasWaiters()
    {
        return !waiting.isEmpty();
    }

    public boolean hasWaiter(Transaction transaction)
    {
        return waiting != null && waiting.contains(transaction);
    }
    
    public void addWaiter(Transaction transaction)
    {
        assert
            !waiting.contains(transaction)
            : String.format("%s, transaction %s", this, transaction);
        waiting.addLast(transaction);
    }

    public void removeWaiter(Transaction transaction)
    {
        waiting.remove(transaction);
    }
    
    public Transaction giveLockToFirstWaiter()
    {
        // waiting should not be empty. LockManager should discard a LockConflict object as soon
        // as the last waiter is removed.
        assert !waiting.isEmpty() : key;
        do {
            lockOwner = waiting.pollFirst();
        } while (lockOwner != null && ignorable(lockOwner));
        return lockOwner;
    }

    public void findDependencies(Map<Transaction, WaitsFor> dependencies)
    {
        for (Transaction lockWaiter : waiting) {
            if (!ignorable(lockWaiter)) {
                WaitsFor replaced = dependencies.put(lockWaiter,
                                                     new WaitsFor(lockWaiter, lockOwner));
                assert replaced == null : replaced;
            }
        }
    }

    public Collection<Transaction> waiters()
    {
        return waiting;
    }
    
    public LockConflict(AbstractKey key, Transaction lockOwner)
    {
        this.key = key;
        this.lockOwner = lockOwner;
    }
    
    // For use by this class
    
    private boolean ignorable(Transaction transaction)
    {
        return transaction.irrelevant() || transaction.hasAborted();
    }
    
    // Object state
    
    private final AbstractKey key;
    private Transaction lockOwner;
    private Deque<Transaction> waiting = new ArrayDeque<Transaction>();
}
