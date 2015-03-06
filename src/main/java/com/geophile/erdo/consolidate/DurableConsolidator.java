/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

class DurableConsolidator extends Consolidator
{
    // Consolidator interface

    @Override
    public void noteNewElement()
    {
        try {
            if (LOG.isLoggable(Level.FINER)) {
                consolidationSet.describe(
                    LOG,
                    Level.FINER,
                    "Consolidation set prior to enqueueing fractal consolidation");
            }
            if (threadPool != null) {
                threadPool.execute(new ConsolidationTask(this, null));
            }
        } catch (RejectedExecutionException e) {
            LOG.log(Level.WARNING, "Shutting down, skipping durable consolidation");
        }
    }

    @Override
    public void stopThreads() throws IOException, InterruptedException
    {
        if (threadPool != null) {
            boolean timeout;
            try {
                threadPool.shutdownNow();
                timeout = !threadPool.awaitTermination(MAXIMUM_SHUTDOWN_WAIT_SEC, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new ShutdownError();
            }
            if (timeout) {
                throw new ShutdownError();
            }
        }
    }

    @Override
    public void noteConsolidationStart()
    {
        inProgress.incrementAndGet();
    }

    @Override
    public void noteConsolidationEnd(Throwable termination)
    {
        super.noteConsolidationEnd(termination);
        inProgress.decrementAndGet();
    }

    // DurableConsolidator interface

    public static DurableConsolidator usingThreadPool(ConsolidationSet consolidationSet,
                                                      ConsolidationPlanner planner)
    {
        return new DurableConsolidator(consolidationSet,
                                       planner,
                                       ThreadPoolType.MULTIPLE_THREADS);
    }

    public static DurableConsolidator runPeriodically(ConsolidationSet consolidationSet,
                                                      ConsolidationPlanner planner)
    {
        return new DurableConsolidator(consolidationSet,
                                       planner,
                                       ThreadPoolType.ONE_PERIODIC_THREAD);
    }

    // For use by this class

    private DurableConsolidator(ConsolidationSet consolidationSet,
                                ConsolidationPlanner planner,
                                ThreadPoolType threadPoolType)
    {
        super(consolidationSet, planner);
        int threads = consolidationSet.configuration().consolidationThreads();
        // threads = 0 means we want NO background conslidation
        if (threads > 0) {
            switch (threadPoolType) {
                case MULTIPLE_THREADS:
                    threadPool = Executors.newFixedThreadPool(threads, newThreadFactory(threadPoolType.toString()));
                    break;
                case ONE_PERIODIC_THREAD:
                    ScheduledExecutorService executorService =
                        Executors.newSingleThreadScheduledExecutor(newThreadFactory(threadPoolType.toString()));
                    executorService.scheduleWithFixedDelay(
                        new Runnable()
                        {
                            public void run()
                            {
                                try {
                                    new ConsolidationTask(DurableConsolidator.this, null).run();
                                } catch (Exception e) {
                                    LOG.log
                                        (Level.WARNING,
                                         "Periodic DurableConsolidator terminated by exception",
                                         e);
                                } catch (Throwable e) {
                                    LOG.log
                                        (Level.SEVERE,
                                         "Periodic DurableConsolidator terminated by error",
                                         e);
                                }
                            }
                        },
                        // TODO: Make interval configurable
                        100, 100, TimeUnit.MILLISECONDS);
                    threadPool = executorService;
                    break;
                default:
                    assert false;
                    threadPool = null;
                    break;
            }
        } else {
            threadPool = null;
        }
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(DurableConsolidator.class.getName());

    // Object state

    private final ExecutorService threadPool;
    private AtomicInteger inProgress = new AtomicInteger(0);

    // Inner classes

    private enum ThreadPoolType { MULTIPLE_THREADS, ONE_PERIODIC_THREAD }
}
