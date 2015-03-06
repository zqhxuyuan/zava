/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.geophile.erdo.consolidate.Consolidation.Container;
import static com.geophile.erdo.consolidate.Consolidation.Element;

class ConsolidationTask implements Runnable
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("ConsolidationTask#%s(%s)", taskId, planner);
    }

    // Runnable interface

    public void run()
    {
        consolidator.noteConsolidationStart();
        try {
            consolidateIfNecessary();
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "{0} terminated by interruption", this);
        } catch (IOException e) {
            termination = e;
            LOG.log(Level.SEVERE, "{0} terminated by IOException", this);
            LOG.log(Level.SEVERE, "stack", e);
        } catch (Throwable e) {
            termination = e;
            LOG.log(Level.SEVERE, "Something has gone very wrong", e);
        } finally {
            consolidator.noteConsolidationEnd(termination);
        }
    }

    public boolean inputDurable()
    {
        return planner.inputDurable();
    }

    public boolean outputDurable()
    {
        return planner.outputDurable();
    }

    // ConsolidationTask interface

    public ConsolidationTask(Consolidator consolidator, Element newElement)
    {
        this.consolidator = consolidator;
        this.planner = consolidator.planner();
        this.container = planner.consolidationSet().container();
        this.newElement = newElement;
    }

    // For use by this class

    // package private so that tests can get to it
    /* private */ void consolidateIfNecessary() throws InterruptedException, IOException
    {
        ConsolidationSet consolidationSet = planner.consolidationSet();
        boolean needToConsolidate;
        synchronized (container) {
            needToConsolidate = planner.planConsolidation(newElement);
            if (needToConsolidate) {
                consolidationSet.markForConsolidation(this, planner.elementsToConsolidate());
            }
        }
        if (needToConsolidate) {
            consolidate();
        }
    }

    private void consolidate() throws InterruptedException
    {
        ConsolidationSet consolidationSet = planner.consolidationSet();
        consolidationSet.consolidationStarting();
        Element replacement = null;
        List<Element> obsolete = planner.elementsToConsolidate();
        try {
            List<Element> destroy;
            long start = System.currentTimeMillis();
            try {
                replacement = container.consolidate(obsolete,
                                                    planner.inputDurable(),
                                                    planner.outputDurable());
            } catch (ClosedByInterruptException | InterruptedException e) {
                // cleanup is below
            }
            if (replacement == null) {
                // Interrupted, either by InterruptedException, or ClosedByInterruptException.
                // Clean up the ConsolidationSet
                consolidationSet.consolidationFailed(obsolete, planner.inputDurable());
                LOG.log(Level.WARNING, "{0} failed consolidation complete", this);
            } else {
                long stop = System.currentTimeMillis();
                logConsolidation(obsolete, replacement, stop - start);
                synchronized (container) {
                    destroy = consolidationSet.replaceObsolete(this, obsolete, replacement);
                    // TODO: Move this call, and synchronization, inside ConsolidationSet
                    container.replaceObsolete(obsolete, replacement);
                }
                // Transactions in obsolete are now durable and public.
                // Destroy elements that were unused when they became obsolete.
                consolidationSet.deleteElements(destroy);
                LOG.log(Level.FINE, "{0} consolidation complete", this);
            }
        } catch (IOException e) {
            // But this is not normal, even on shutdown
            LOG.log(Level.WARNING,
                    "{0} failed. replacement: {1}, obsolete: {2}",
                    new Object[]{this, replacement, obsolete});
            LOG.log(Level.SEVERE, e.toString(), e);
        } catch (Throwable e) {
            LOG.log(Level.SEVERE, "Well this is unexpected", e);
            throw new Error(e);
        } finally {
            consolidationSet.consolidationEnding();
        }
    }

    private void logConsolidation(List<Element> obsolete, Element replacement, long timeMsec)
    {
        if (LOG.isLoggable(Level.INFO)) {
            SortedMap<String, Integer> sizeDistribution = new TreeMap<>();
            for (Element element : obsolete) {
                String classname = element.getClass().getSimpleName();
                long size = element.count();
                String key = String.format("%s:%s", classname, size);
                Integer count = sizeDistribution.get(key);
                if (count == null) {
                    count = 0;
                }
                sizeDistribution.put(key, count + 1);
            }
            StringBuilder obsoleteSizes = new StringBuilder();
            for (Map.Entry<String, Integer> entry : sizeDistribution.entrySet()) {
                if (obsoleteSizes.length() > 0) {
                    obsoleteSizes.append(", ");
                }
                obsoleteSizes.append(String.format("%s x %s", entry.getValue(), entry.getKey()));
            }
            LOG.log(Level.INFO,
                    "{0} {1} replaced by {2}, consolidation took {3} msec",
                    new Object[]{this, obsoleteSizes, replacement, timeMsec});
        }
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(ConsolidationTask.class.getName());
    private static final AtomicLong taskIdCounter = new AtomicLong(0);

    // Object state

    private final Consolidator consolidator;
    private final ConsolidationPlanner planner;
    private final long taskId = taskIdCounter.getAndIncrement();
    private final Container container;
    private final Element newElement;
    private Throwable termination;
}
