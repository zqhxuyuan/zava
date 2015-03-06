/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.apiimpl;

import com.geophile.erdo.transaction.LockManager;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

class DeadlockFixer
{
    public DeadlockFixer(LockManager lockManager)
    {
        scheduledExecutorService =
            Executors.newScheduledThreadPool(
                1,
                new ThreadFactory()
                {
                    public Thread newThread(Runnable runnable)
                    {
                        Thread thread = new Thread(runnable);
                        thread.setDaemon(true);
                        thread.setName("DEADLOCK_DETECTOR");
                        return thread;
                    }
                });
        scheduledFuture =
            scheduledExecutorService.scheduleWithFixedDelay(
                new DeadlockFixerTask(lockManager),
                DEADLOCK_DETECTION_INTERVAL_MSEC,
                DEADLOCK_DETECTION_INTERVAL_MSEC,
                TimeUnit.MILLISECONDS);
    }

    public void stop()
    {
        scheduledExecutorService.shutdownNow();
        scheduledFuture.cancel(true);
    }

    private static final Logger LOG = Logger.getLogger(DeadlockFixer.class.getName());
    private static final int DEADLOCK_DETECTION_INTERVAL_MSEC = 100;

    private final ScheduledExecutorService scheduledExecutorService;
    private final ScheduledFuture scheduledFuture;

    private class DeadlockFixerTask implements Runnable
    {
        public void run()
        {
            try {
                lockManager.killDeadlockVictims();
            } catch (Throwable e) {
                LOG.log(Level.SEVERE, "Caught exception while killing deadlock victims", e);
            }
        }

        public DeadlockFixerTask(LockManager lockManager)
        {
            this.lockManager = lockManager;
        }

        private final LockManager lockManager;
    }
}
