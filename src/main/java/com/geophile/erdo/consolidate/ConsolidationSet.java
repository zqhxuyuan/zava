/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.diskmap.DiskMap;
import com.geophile.erdo.util.IdentitySet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.geophile.erdo.consolidate.Consolidation.Element;

/*
 * A ConsolidationSet manages a set of Elements that are periodically consolidated.
 * Consolidation selects a number of Elements, merges them to create a replacement, and then
 * removes the obsolete, consolidated elements from the ConsolidationSet. From the outside,
 * (to a user of the ConsolidationSet), the replacement is atomic. An Element
 * manages resources, and since the switch from obsolete elements to the replacement is
 * instantaneous, the set of resources being managed doesn't change.
 *
 * A ConsolidationSet is occasionally copied, to create a ConsolidationSetSnapshot. The copy
 * contains a set of Elements at a point in time, and does not reflect later changes
 * to the ConsolidationSet.
 *
 * An Element can only be destroyed when it is obsolete, (i.e., has been consolidated and replaced),
 * and it is unused (i.e., not present in any ConsolidationSetSnapshot). If an Element is unused when
 * it becomes obsolete, it is immediately destroyed. If it is in use (present in a ConsolidationSetSnapshot)
 * when it becomes obsolete, then it is kept around and destroyed when the usage count drops to zero.
 *
 * Elements are managed in two ConsolidationElementTrackers:
 * - nonDurable: Elements whose state exists only in memory.
 * - durable: Elements whose state exists on disk.
 * ConsolidationElementTracker notes which elements are currently being consolidated.
 *
 * ConsolidationSet state is operated on under synchronization. The code synchronizes on container, not this
 * because the state managed by the container resides both in the container's ConsolidationSet (this) and
 * possibly in the container. Synchronizing on this misses the container's state. Synchronization protects access
 * to a ConsolidationSet and its container. Actual consolidation, and destruction of elements are expensive, and
 * done outside of synchronized blocks.
 */

public class ConsolidationSet
{
    // ConsolidationSet interface

    public Consolidation.Container container()
    {
        return container;
    }

    public void add(Element element, boolean makeDurableImmediately)
        throws InterruptedException, IOException
    {
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE,
                    "Adding {0} to consolidation set, inProgress: {1}, nonDurable records: {2}, complexity: {3}",
                    new Object[]{element, inProgress, nonDurable.totalRecords(), nonDurable.complexity()});
        }
        synchronized (container) {
            long start = -1L;
            while (maxPendingBytes > 0 &&
                   nonDurable.totalBytes() >= maxPendingBytes) {
                start = System.currentTimeMillis();
                container.wait();
            }
            if (LOG.isLoggable(Level.INFO) && start != -1L) {
                long stop = System.currentTimeMillis();
                LOG.log(Level.INFO,
                        "Waited {0} msec to add {1}",
                        new Object[]{(stop - start), element});
            }
            nonDurable.add(element);
        }
        if (makeDurableImmediately) {
            makeDurableImmediatelyConsolidator.consolidate(element);
        } else {
            inMemoryConsolidator.consolidate(null);
        }
        if (element.count() > 0) {
            updateLastActivityTime();
        }
    }

    public ConsolidationSetSnapshot snapshot()
    {
        synchronized (container) {
            List<Element> snapshot = new ArrayList<>(nonDurable.elements());
            snapshot.addAll(durable.elements());
            for (Element element : snapshot) {
                usageCounts.register(element.id());
            }
            return new ConsolidationSetSnapshot(this, snapshot);
        }
    }

    public void consolidateAll() throws IOException, InterruptedException
    {
        allDurableConsolidator.consolidate(null);
    }

    public void consolidateOnRecovery()
    {
        List<Element> consolidateOnRecovery;
        synchronized (container) {
            long keysInMemoryMapLimit = container.configuration().keysInMemoryMapLimit();
            consolidateOnRecovery = new ArrayList<>();
            for (Element element : durable.availableForConsolidation()) {
                if (element.count() <= keysInMemoryMapLimit) {
                    consolidateOnRecovery.add(element);
                }
            }
        }
        if (consolidateOnRecovery.size() > 1) {
            Consolidator recoveryConsolidator =
                ImmediateConsolidator.newConsolidator
                    (this, RecoveryConsolidationPlanner.newPlanner(this, consolidateOnRecovery));
            try {
                recoveryConsolidator.consolidate(null);
            } catch (IOException | InterruptedException e) {
                assert false;
            }
            // Read the new map (highest-numbered) into memory
            DiskMap newMap = null;
            synchronized (container) {
                List<Element> availableForConsolidation = durable.availableForConsolidation();
                assert !availableForConsolidation.isEmpty();
                for (Element element : availableForConsolidation) {
                    DiskMap map = (DiskMap) element;
                    if (newMap == null || map.id() > newMap.id()) {
                        newMap = map;
                    }
                }
            }
            try {
                assert newMap != null;
                MapCursor mapScan = newMap.cursor(null, false);
                while (mapScan.next() == null);
            } catch (IOException | InterruptedException e) {
                assert false : e;
            }
        }
    }

    public void flush() throws IOException, InterruptedException
    {
        flushConsolidator.consolidate(null);
    }

    public void shutdown() throws InterruptedException, IOException
    {
        synchronized (shutdown) {
            if (!shutdown.get()) {
                makeDurableImmediatelyConsolidator.stopThreads();
                firstDurableConsolidator.stopThreads();
                laterDurableConsolidator.stopThreads();
                inMemoryConsolidator.stopThreads();
                LOG.log(Level.INFO, "Running final consolidation to make last commits durable.");
                flushConsolidator.consolidate(null);
                flushConsolidator.stopThreads();
                treeDeleter.delete(new ArrayList<>(destroyWhenUnused.keySet())); // No elements are in use at shutdown.
                treeDeleter.shutdown();
            }
            shutdown.set(true);
        }
    }

    public Configuration configuration()
    {
        return container.configuration();
    }

    public void describe(Logger log, Level level, String label)
    {
        synchronized (container) {
            durable.describe(log, level, label);
            nonDurable.describe(log, level, label);
        }
    }

    public static ConsolidationSet newConsolidationSet(Consolidation.Container owner, List<? extends Element> elements)
    {
        ConsolidationSet consolidationSet = new ConsolidationSet(owner, elements);
        consolidationSet.createSynchronousConsolidators();
        consolidationSet.consolidateOnRecovery();
        consolidationSet.createAsynchronousConsolidators();
        return consolidationSet;
    }

    // For use by this package

    ConsolidationElementTracker durable()
    {
        return durable;
    }

    ConsolidationElementTracker nonDurable()
    {
        return nonDurable;
    }

    void consolidationStarting()
    {
        synchronized (container) {
            inProgress++;
        }
    }

    void consolidationFailed(List<Element> elements, boolean inputDurable)
    {
        synchronized (container) {
            if (inputDurable) {
                durable.consolidationFailed(elements);
            } else {
                nonDurable.consolidationFailed(elements);
            }
        }
        updateLastActivityTime();
    }

    void consolidationEnding()
    {
        synchronized (container) {
            inProgress--;
            assert inProgress >= 0 : inProgress;
            container.notifyAll();
        }
        updateLastActivityTime();
    }

    void unregister(List<Element> elements)
    {
        List<Element> destroy = new ArrayList<>();
        synchronized (container) {
            for (Element element : elements) {
                long id = element.id();
                boolean unused = usageCounts.unregister(id);
                if (unused && destroyWhenUnused.contains(element)) {
                    destroy.add(element);
                    destroyWhenUnused.remove(element);
                }
            }
        }
        treeDeleter.delete(destroy);
    }

    List<Element> replaceObsolete(ConsolidationTask consolidationTask,
                                  List<Element> obsolete,
                                  Element replacement)
    {
        assert Thread.holdsLock(container);
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "{0} obsoletes {1}", new Object[]{replacement, obsolete});
        }
        List<Element> destroy = new ArrayList<>();
        ConsolidationElementTracker obsoleteElementTracker =
            consolidationTask.inputDurable() ? durable : nonDurable;
        for (Element element : obsolete) {
            obsoleteElementTracker.removeElementBeingConsolidated(element);
            if (usageCounts.unused(element.id())) {
                // Not used in any snapshot
                LOG.log(Level.FINE, "Destroy {0} now", element);
                destroy.add(element);
            } else {
                LOG.log(Level.FINE, "Destroy {0} when unused", element);
                destroyWhenUnused.add(element);
            }
        }
        if (replacement.durable()) {
            durable.add(replacement);
            laterDurableConsolidator.noteNewElement();
        } else {
            nonDurable.add(replacement);
            inMemoryConsolidator.noteNewElement();
        }
        return destroy;
    }

    void markForConsolidation(ConsolidationTask consolidationTask, List<Element> toConsolidate)
    {
        assert Thread.holdsLock(container);
        ConsolidationElementTracker elementTracker =
            consolidationTask.inputDurable() ? durable : nonDurable;
        elementTracker.beingConsolidated(toConsolidate);
    }

    boolean idle()
    {
        return
            inProgress == 0 &&
            System.currentTimeMillis() - lastActivityTime.get() >= idleTimeMsec;
    }

    void deleteElements(List<Element> elements)
    {
        treeDeleter.delete(elements);
    }

    // For use by this class

    private void updateLastActivityTime()
    {
        lastActivityTime.set(System.currentTimeMillis());
    }

    private ConsolidationSet(Consolidation.Container container, List<? extends Element> elements)
    {
        // A consolidation set is created on recovery, so all of the elements must be durable.
        nonDurable = ConsolidationElementTracker.nonDurable(container);
        durable = ConsolidationElementTracker.durable(container);
        this.container = container;
        // Synchronization is not really necessary. But durable.add asserts synchronization on the container.
        synchronized (this.container) {
            for (Element element : elements) {
                assert element.durable();
                durable.add(element);
            }
        }
        this.maxPendingBytes = configuration().consolidationMaxPendingCommittedSizeBytes();
        this.idleTimeMsec = configuration().consolidationIdleTimeSec() * 1000;
        this.treeDeleter = TreeDeleter.create();
    }

    private void createSynchronousConsolidators()
    {
        int minConsolidationMaps = configuration().consolidationMinMapsToConsolidate();
        this.makeDurableImmediatelyConsolidator =
            ImmediateConsolidator.newConsolidator
                (this, ImmediateConsolidationPlanner.newPlanner(this));
        this.inMemoryConsolidator =
            ImmediateConsolidator.newConsolidator
                (this, FractalConsolidationPlanner.nonDurable(this,
                                                              minConsolidationMaps,
                                                              0));
        this.flushConsolidator =
            ImmediateConsolidator.newConsolidator
                (this, AllNonDurableConsolidationPlanner.newPlanner(this));
        this.allDurableConsolidator =
            ImmediateConsolidator.newConsolidator
                (this, AllDurableConsolidationPlanner.newPlanner(this));
    }

    private void createAsynchronousConsolidators()
    {
        int minConsolidationSize = configuration().consolidationMinSizeBytes();
        int minConsolidationMaps = configuration().consolidationMinMapsToConsolidate();
        this.firstDurableConsolidator =
            DurableConsolidator.runPeriodically
                (this, MakeDurableConsolidationPlanner.newPlanner(this));
        this.laterDurableConsolidator =
            DurableConsolidator.usingThreadPool
                (this, FractalConsolidationPlanner.durable(this,
                                                           minConsolidationMaps,
                                                           minConsolidationSize));
        this.idleConsolidator =
            DurableConsolidator.runPeriodically
                (this, IdleConsolidationPlanner.newPlanner(this,
                                                           minConsolidationMaps,
                                                           minConsolidationSize));
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(ConsolidationSet.class.getName());

    // Object state

    private final Consolidation.Container container;
    private final ConsolidationElementTracker nonDurable;
    private final ConsolidationElementTracker durable;
    private final IdentitySet<Element> destroyWhenUnused = new IdentitySet<>();
    private final long maxPendingBytes;
    private final AtomicBoolean shutdown = new AtomicBoolean(false);
    private final TreeDeleter treeDeleter;
    private final int idleTimeMsec;
    private volatile int inProgress = 0;
    private volatile AtomicLong lastActivityTime = new AtomicLong(0);
    // Tracks usage of elements by snapshots
    private final UsageCounts usageCounts = new UsageCounts();
    private Consolidator makeDurableImmediatelyConsolidator;
    private Consolidator inMemoryConsolidator;
    // Consolidate everything
    private Consolidator allDurableConsolidator;
    // Consolidate in response to Database.flush()
    private Consolidator flushConsolidator;
    // Makes non-durable elements durable
    private Consolidator firstDurableConsolidator;
    // Consolidates durable elements, triggered by end of a consolidation
    private Consolidator laterDurableConsolidator;
    // Consolidates when there aren't any being triggered by updates
    private Consolidator idleConsolidator;
}
