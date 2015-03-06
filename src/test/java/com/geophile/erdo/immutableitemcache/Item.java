/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.immutableitemcache;

class Item extends CacheEntry<Id, Item> implements Comparable
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("item(%s)%s", id.value(), okToEvict ? "*" : "");
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return compareTo(obj) == 0;
    }

    // Comparable interface

    public int compareTo(Object o)
    {
        Item that = (Item) o;
        return this.id().value() - that.id().value();
    }

    // CacheEntry interface

    @Override
    public Item item()
    {
        return this;
    }

    @Override
    public Id id()
    {
        return id;
    }

    @Override
    public boolean okToEvict()
    {
        return okToEvict;
    }

    public void okToEvict(boolean okToEvict)
    {
        this.okToEvict = okToEvict;
    }

    @Override
    public int referenceCount()
    {
        return 1;
    }

    // Item interface

    public Item(Id id)
    {
        super();
        this.id = id;
    }

    private final Id id;
    private volatile boolean okToEvict = false;
}
