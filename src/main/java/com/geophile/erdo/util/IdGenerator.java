/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator
{
    public long nextId()
    {
        return idGenerator.getAndIncrement();
    }

    public void restore(long maxKnownId)
    {
        idGenerator.set(maxKnownId + 1);
    }

    public IdGenerator()
    {
        this(0);
    }

    public IdGenerator(int initialValue)
    {
        idGenerator = new AtomicLong(initialValue);
    }

    private final AtomicLong idGenerator;
}
