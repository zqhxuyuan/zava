/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.map.MapCursor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class TestMultiRecord extends AbstractMultiRecord
{
    // Transferrable interface

    @Override
    public void writeTo(ByteBuffer buffer)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void readFrom(ByteBuffer buffer)
    {
        throw new UnsupportedOperationException();
    }

    // AbstractRecord interface

    @Override
    public long estimatedSizeBytes()
    {
        return 0;
    }

    // AbstractMultiRecord interface

    public void append(AbstractRecord record)
    {
        records.add(record);
    }

    public MapCursor cursor()
    {
        return new IteratorCursor(records.iterator());
    }

    // TestMultiRecord interface

    List<AbstractRecord> records()
    {
        return records;
    }

    TestMultiRecord(MultiRecordKey multiRecordKey)
    {
        super(multiRecordKey);
    }

    // Object state

    private final List<AbstractRecord> records = new ArrayList<AbstractRecord>();
}
