/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/**
 * END USER LICENSE AGREEMENT (“EULA”)
 *
 * READ THIS AGREEMENT CAREFULLY (date: 9/13/2011):
 * http://www.akiban.com/licensing/20110913
 *
 * BY INSTALLING OR USING ALL OR ANY PORTION OF THE SOFTWARE, YOU ARE ACCEPTING
 * ALL OF THE TERMS AND CONDITIONS OF THIS AGREEMENT. YOU AGREE THAT THIS
 * AGREEMENT IS ENFORCEABLE LIKE ANY WRITTEN AGREEMENT SIGNED BY YOU.
 *
 * IF YOU HAVE PAID A LICENSE FEE FOR USE OF THE SOFTWARE AND DO NOT AGREE TO
 * THESE TERMS, YOU MAY RETURN THE SOFTWARE FOR A FULL REFUND PROVIDED YOU (A) DO
 * NOT USE THE SOFTWARE AND (B) RETURN THE SOFTWARE WITHIN THIRTY (30) DAYS OF
 * YOUR INITIAL PURCHASE.
 *
 * IF YOU WISH TO USE THE SOFTWARE AS AN EMPLOYEE, CONTRACTOR, OR AGENT OF A
 * CORPORATION, PARTNERSHIP OR SIMILAR ENTITY, THEN YOU MUST BE AUTHORIZED TO SIGN
 * FOR AND BIND THE ENTITY IN ORDER TO ACCEPT THE TERMS OF THIS AGREEMENT. THE
 * LICENSES GRANTED UNDER THIS AGREEMENT ARE EXPRESSLY CONDITIONED UPON ACCEPTANCE
 * BY SUCH AUTHORIZED PERSONNEL.
 *
 * IF YOU HAVE ENTERED INTO A SEPARATE WRITTEN LICENSE AGREEMENT WITH AKIBAN FOR
 * USE OF THE SOFTWARE, THE TERMS AND CONDITIONS OF SUCH OTHER AGREEMENT SHALL
 * PREVAIL OVER ANY CONFLICTING TERMS OR CONDITIONS IN THIS AGREEMENT.
 */

package com.geophile.erdo.util;

import java.util.concurrent.atomic.AtomicLong;

public class Timer
{
    public String toString()
    {
        double totalUsec = elapsedNsec.get() / 1000.0;
        double averageUsec = totalUsec / count.get();
        return String.format("%s:    total: %s usec    count: %s    average: %s usec",
                             label, totalUsec, count.get(), averageUsec);
    }

    public void start()
    {
        start.get().set(System.nanoTime());
    }

    public void stop()
    {

        elapsedNsec.addAndGet(System.nanoTime() - start.get().get());
        count.incrementAndGet();
    }

    public long nsec()
    {
        return elapsedNsec.get();
    }

    public long count()
    {
        return count.get();
    }

    public Timer(String label)
    {
        this.label = label;
    }

    private final ThreadLocal<AtomicLong> start = new ThreadLocal<AtomicLong>()
    {
        @Override
        protected AtomicLong initialValue()
        {
            return new AtomicLong(0);
        }
    };
    private final String label;
    private final AtomicLong elapsedNsec = new AtomicLong(0);
    private final AtomicLong count = new AtomicLong(0);
}
