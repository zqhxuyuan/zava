/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.immutableitemcache;

public class ItemPlaceholder<ID, ITEM> extends CacheEntry<ID, ITEM>
{
    @Override
    public ITEM item()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean placeholder()
    {
        return true;
    }

    @Override
    public Thread owner()
    {
        return owner;
    }

    public static <ID, ITEM> ItemPlaceholder forCurrentThread()
    {
        return new ItemPlaceholder<ID, ITEM>();
    }

    private ItemPlaceholder()
    {
        this.owner = Thread.currentThread();
    }

    private final Thread owner;
}
