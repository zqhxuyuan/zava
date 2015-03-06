/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.apiimpl;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;

import java.nio.ByteBuffer;

public class DeletedRecord extends AbstractRecord
{
    // Transferrable interface

    @Override
    public final void readFrom(ByteBuffer buffer)
    {
        super.readFrom(buffer);
    }

    @Override
    public final void writeTo(ByteBuffer buffer)
    {
        super.writeTo(buffer);
    }

    // AbstractRecord interface

    @Override
    public boolean deleted()
    {
        return true;
    }

    // DeletedRecord interface

    @Override
    public final AbstractRecord copy()
    {
        AbstractKey key = key();
        assert key != null;
        return new DeletedRecord(key.copy());
    }

    public DeletedRecord(AbstractKey key)
    {
        super(markDeleted(key.copy()));
    }

    private static <KEY extends AbstractKey> KEY markDeleted(KEY key)
    {
        key.deleted(true);
        return key;
    }
}
