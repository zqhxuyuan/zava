/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import com.geophile.erdo.UsageError;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.IdentityHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Tracker<T>
{
    public static <T> Tracker<T> trackCount(String label)
    {
        return new CountTracker<T>(label);
    }

    public static <T> Tracker<T> trackDetail(String label)
    {
        return new DetailTracker<T>(label);
    }

    public abstract void register(T t);

    public abstract void unregister(T t);

    public abstract void checkCount(int expectedCount);

    protected Tracker(String label)
    {
        this.label = label;
    }

    protected String label;

    public static class CountTracker<T> extends Tracker<T>
    {
        @Override
        public void register(T t)
        {
            count.incrementAndGet();
        }

        @Override
        public void unregister(T t)
        {
            count.decrementAndGet();
        }

        @Override
        public void checkCount(int expectedCount)
        {
            int n = count.get();
            if (n != expectedCount) {
                throw new RuntimeException(String.format("%s: count is %s, expected %s", label, n, expectedCount));
            }
        }

        public CountTracker(String label)
        {
            super(label);
        }

        private final AtomicInteger count = new AtomicInteger(0);
    }

    public static class DetailTracker<T> extends Tracker<T>
    {
        @Override
        public void register(T t)
        {
            registrations.put(t, new Throwable());
        }

        @Override
        public void unregister(T t)
        {
            registrations.remove(t);
        }

        @Override
        public void checkCount(int expectedCount)
        {
            int n = registrations.size();
            if (n != expectedCount) {
                StringBuilder buffer = new StringBuilder();
                for (Throwable throwable : registrations.values()) {
                    StringWriter stringWriter = new StringWriter();
                    PrintWriter printWriter = new PrintWriter(stringWriter);
                    throwable.printStackTrace(printWriter);
                    buffer.append('\n');
                    buffer.append(stringWriter.toString());
                }
                throw new RuntimeException(String.format("%s: count is %s, expected %s. Stacks: %s",
                                                         label, n, expectedCount, buffer.toString()));
            }
        }

        public DetailTracker(String label)
        {
            super(label);
        }

        private final IdentityHashMap<T, Throwable> registrations = new IdentityHashMap<T, Throwable>();
    }

    public static class RuntimeException extends UsageError
    {
        public RuntimeException(String message)
        {
            super(message);
        }
    }
}
