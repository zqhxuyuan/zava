/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import java.io.IOException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

abstract class Consolidator
{
    // Object interface

    @Override
    public final String toString()
    {
        return getClass().getSimpleName();
    }

    // Consolidator interface

    public ConsolidationPlanner planner()
    {
        return planner;
    }

    public void noteNewElement()
    {
    }

    public void stopThreads() throws IOException, InterruptedException
    {
    }

    public void noteConsolidationStart()
    {
    }

    public void noteConsolidationEnd(Throwable termination)
    {
        if (termination != null) {
            consolidationSet.container().reportCrash(termination);
        }
    }

    public final void consolidate(Consolidation.Element newElement)
        throws IOException, InterruptedException
    {
        new ConsolidationTask(this, newElement).run();
    }

    // For use by subclasses

    protected static ThreadFactory newThreadFactory(final String description)
    {
        return new ThreadFactory()
        {
            public Thread newThread(Runnable consolidationTask)
            {
                Thread thread = new Thread(consolidationTask);
                thread.setDaemon(true);
                thread.setName(String.format("CONSOLIDATION_%s(%s)",
                                             description, THREAD_FACTORY_COUNTER.getAndIncrement()));
                return thread;
            }
        };
    }

    // For use by this class

    protected Consolidator(ConsolidationSet consolidationSet, ConsolidationPlanner planner)
    {
        this.consolidationSet = consolidationSet;
        this.planner = planner;
    }

    // Class state

    protected static final Logger LOG = Logger.getLogger(Consolidator.class.getName());
    protected static final long MAXIMUM_SHUTDOWN_WAIT_SEC = 5L;
    private static final AtomicInteger THREAD_FACTORY_COUNTER = new AtomicInteger(0);

    // Object state

    protected final ConsolidationSet consolidationSet;
    protected final ConsolidationPlanner planner;

    // Inner classes

    public static class ShutdownError extends java.lang.Error
    {
        ShutdownError()
        {
        }
    }
}
