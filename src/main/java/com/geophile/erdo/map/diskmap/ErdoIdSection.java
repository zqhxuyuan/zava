/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.map.Factory;
import com.geophile.erdo.util.ErdoIdArray;

import java.nio.ByteBuffer;

class ErdoIdSection extends DiskPageSectionFixedLengthRecords
{
    // DiskPageSection interface

    @Override
    public void close()
    {
        recordSize(INT_SIZE);
        append(erdoIds);
        super.close();
    }

    // ErdoIdSection interface

    public void append(int erdoId)
    {
        erdoIds.append(erdoId);
    }

    public void removeLast()
    {
        erdoIds.removeLast();
    }

    public int erdoId(int position)
    {
        return erdoIds.at(position);
    }

    // Size within a disk page. If not closed (which is expected usage), then figure it out from the header
    // size (available from super.size(), known now), and erdoIds.
    public int size()
    {
        int size = super.size();
        if (state != State.CLOSED) {
            size += erdoIds.serializedSize();
        }
        return size;
    }

    public static ErdoIdSection forRead(ByteBuffer pageBuffer)
    {
        return new ErdoIdSection(pageBuffer);
    }

    public static ErdoIdSection forWrite(ByteBuffer buffer, Factory factory)
    {
        return new ErdoIdSection(buffer, factory);
    }

    // For use by this class

    private ErdoIdSection(ByteBuffer pageBuffer)
    {
        super(pageBuffer);
        erdoIds = new ErdoIdArray();
        erdoIds.readFrom(pageBuffer);
        pageBuffer.position(dataPosition + count * 4);
    }

    private ErdoIdSection(ByteBuffer buffer, Factory factory)
    {
        super(0, buffer);
        erdoIds = new ErdoIdArray();
    }

    // Class state

    protected static final int INT_SIZE = 4;

    // Object state

    private final ErdoIdArray erdoIds;
}
