/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.immutableitemcache;

public abstract class CacheEntry<ID, ITEM>
{
    public ID id()
    {
        throw new UnsupportedOperationException();
    }

    public abstract ITEM item();

    public boolean okToEvict()
    {
        throw new UnsupportedOperationException();
    }

    public int referenceCount()
    {
        throw new UnsupportedOperationException();
    }

    public boolean placeholder()
    {
        return false;
    }

    public Thread owner()
    {
        throw new UnsupportedOperationException();
    }

    public final ITEM next()
    {
        return next;
    }

    public final void next(ITEM item)
    {
        next = item;
    }

    public final boolean recentAccess()
    {
        return recentAccess;
    }

    public final void recentAccess(boolean recentAccess)
    {
        this.recentAccess = recentAccess;
    }

    private volatile ITEM next;
    private volatile boolean recentAccess;
}
