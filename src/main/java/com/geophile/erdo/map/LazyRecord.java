/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.util.AbstractPool;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class LazyRecord<KEY extends AbstractKey>
{
    // LazyRecord interface

    public abstract KEY key() throws IOException, InterruptedException;

    public abstract ByteBuffer keyBuffer() throws IOException, InterruptedException;

    public abstract AbstractRecord<KEY> materializeRecord() throws IOException, InterruptedException;

    public abstract ByteBuffer recordBuffer() throws IOException, InterruptedException;

    public abstract long estimatedSizeBytes() throws IOException, InterruptedException;

    public abstract boolean prefersSerialized();

    public void destroyRecordReference()
    {
        pool.returnResource(this);
    }

    // For use by subclasses

    protected LazyRecord()
    {
        this.pool = null;
    }

    protected LazyRecord(Pool pool)
    {
        this.pool = pool;
    }

    // Object state

    protected final Pool pool;

    // Inner classes

    public static abstract class Pool extends AbstractPool<LazyRecord>
    {}
}
