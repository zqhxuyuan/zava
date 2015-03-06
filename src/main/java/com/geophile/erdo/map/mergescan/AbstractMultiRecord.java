/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.map.MapCursor;

public abstract class AbstractMultiRecord extends AbstractRecord
{
    // Object interface

    @Override
    public String toString()
    {
        return key().toString();
    }

    // AbstractRecord interface

    // Should be final, but needs to be overridden by TestMultiRecord
    @Override
    public long estimatedSizeBytes()
    {
        assert false;
        return -1L;
    }

    @Override
    public final AbstractRecord copy()
    {
        throw new UnsupportedOperationException();
    }

    // AbstractMultiRecord interface

    public abstract void append(AbstractRecord record);

    public abstract MapCursor cursor();

    // For use by subclasses

    protected AbstractMultiRecord(MultiRecordKey multiRecordKey)
    {
        super(multiRecordKey);
    }
}
