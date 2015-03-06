/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

class LazyConsolidator extends Consolidator
{
    // Consolidator interface

    @Override
    public void noteNewElement()
    {
        try {
            if (!consolidationWaiting) {
                synchronized (this) {
                    if (!consolidationWaiting) {
                        threadPool.execute(new ConsolidationTask(this, null));
                        consolidationWaiting = true;
                    }
                }
            }
        } catch (RejectedExecutionException e) {
            LOG.log(Level.WARNING, "Shutting down, skipping async consolidation");
        }
    }

    @Override
    public void stopThreads() throws IOException, InterruptedException
    {
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

    @Override
    public synchronized void noteConsolidationStart()
    {
        consolidationWaiting = false;
    }

    // LazyConsolidator interface

    public LazyConsolidator(ConsolidationSet consolidationSet, ConsolidationPlanner planner)
    {
        super(consolidationSet, planner);
        this.threadPool = newThreadPool();
    }

    // For use by this class

    private static ExecutorService newThreadPool()
    {
        return Executors.newSingleThreadExecutor(newThreadFactory("lazy"));
    }

    //Object state

    private final ExecutorService threadPool;
    private volatile boolean consolidationWaiting = false;
}
