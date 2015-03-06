/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class DeadlockDetector
{
    public Set<Transaction> victims()
    {
        int detectionId = detectionCounter++;
        LOG.log(Level.INFO, "Starting deadlock detection {0}", detectionId);
        Set<Transaction> victims = new HashSet<Transaction>();
        // WaitsFor.waiter -> WaitsFor
        Map<Transaction, WaitsFor> dependencies = lockManager.dependencies();
        List<Path> paths = new ArrayList<Path>();
        for (WaitsFor waitsFor : dependencies.values()) {
            paths.add(new Path(waitsFor));
        }
        int extensions = 0;
        List<Path> cycles = new ArrayList<Path>();
        logPaths(detectionId, 0, paths, cycles);
        do {
            for (Iterator<Path> pathIterator = paths.iterator(); pathIterator.hasNext();) {
                Path path = pathIterator.next();
                Transaction pathLast = path.last();
                WaitsFor extension = dependencies.get(pathLast);
                if (extension == null) {
                    pathIterator.remove();
                } else if (!path.extend(extension.copy())) {
                    // path contains cycle
                    cycles.add(path);
                    pathIterator.remove();
                }
            }
            logPaths(detectionId, ++extensions, paths, cycles);
        } while (!paths.isEmpty());
        for (Path path : cycles) {
            victims.add(path.victim());
        }
        return victims;
    }
    
    public DeadlockDetector(LockManager lockManager)
    {
        this.lockManager = lockManager;
    }
    
    // For use by this class

    private void logPaths(int detectionId, int extensions, List<Path> paths, List<Path> cycles)
    {
        if (paths.isEmpty()) {
            LOG.log(Level.INFO, "{0}.{1} NO PATHS", new Object[]{detectionId, extensions});
        } else {
            LOG.log(Level.INFO, "{0}.{1} paths", new Object[]{detectionId, extensions});
            for (Path path : paths) {
                LOG.log(Level.INFO, "{0}.{1}     {2}", new Object[]{detectionId, extensions, path});
            }
        }
        if (cycles.isEmpty()) {
            LOG.log(Level.INFO, "{0}.{1} NO CYCLES", new Object[]{detectionId, extensions});
        } else {
            LOG.log(Level.INFO, "{0}.{1} cycles", new Object[]{detectionId, extensions});
            for (Path path : cycles) {
                LOG.log(Level.INFO, "{0}.{1}     {2}", new Object[]{detectionId, extensions, path});
            }
        }
    }
    
    // Class state
    
    private static final Logger LOG = Logger.getLogger(DeadlockDetector.class.getName());
    
    // Object state
    
    private final LockManager lockManager;
    private int detectionCounter = 0;
    
    // Inner classes
    
    private static class Path
    {
        public String toString()
        {
            StringBuilder buffer = new StringBuilder();
            buffer.append(head.waiter());
            WaitsFor w = head;
            do {
                buffer.append(" -> ");
                buffer.append(w.owner());
                w = w.ownerWaitsFor();
            } while (w != null);
            return buffer.toString();
        }
        
        public Path(WaitsFor waitsFor)
        {
            this.head = waitsFor;
            this.tail = waitsFor;
            transactions.put(head.waiter(), head.waiter());
            transactions.put(head.owner(), head.owner());
        }

        // Returns true iff cycle is not formed
        public boolean extend(WaitsFor extension)
        {
            assert tail.owner() == extension.waiter();
            tail.ownerWaitsFor(extension);
            tail = extension;
            Transaction replaced = transactions.put(extension.owner(), extension.owner());
            boolean cycle = replaced != null;
            if (cycle) {
                firstWaiterInCycle = extension.owner();
            }
            return !cycle;
        }
        
        public Transaction first()
        {
            return head.waiter();
        }
        
        public Transaction last()
        {
            return tail.owner();
        }
        
        public Transaction victim()
        {
            Transaction victim = null;
            // Get to start of cycle
            WaitsFor w = head;
            while (w.waiter() != firstWaiterInCycle) {
                w = w.ownerWaitsFor();
            }
            // Pick the transaction in the cycle with the max start time. This should be the
            // newest one in the cycle, wasting the least amount of work. Arguments could be made
            // for other criteria.
            long maxStartTime = Long.MIN_VALUE;
            do {
                Transaction waiter = w.waiter();
                if (waiter.startTime() > maxStartTime) {
                    maxStartTime = waiter.startTime();
                    victim = waiter;
                }
                w = w.ownerWaitsFor();
            } while (w != null);
            assert victim != null;
            return victim;
        }
        
        private final WaitsFor head;
        private WaitsFor tail;
        private IdentityHashMap<Transaction, Transaction> transactions =
            new IdentityHashMap<Transaction, Transaction>();
        private Transaction firstWaiterInCycle;
    }
}
