/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;

import java.nio.ByteBuffer;

public class KeyOnlyRecord extends AbstractRecord
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

    // KeyOnlyRecord interface

    @Override
    public AbstractRecord copy()
    {
        throw new UnsupportedOperationException();
    }

    public KeyOnlyRecord(AbstractKey key)
    {
        super(key);
    }
}
