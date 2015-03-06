/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import java.nio.ByteBuffer;

class DiskPageSectionFixedLengthRecords extends DiskPageSection
{
    // DiskPageSection interface

    @Override
    public void resetToMark()
    {
        assert recordSize >= 0;
        super.resetToMark();
        size = dataPosition + count * recordSize;
    }

    @Override
    public void recordSize(int recordSize)
    {
        this.recordSize = recordSize;
        writeUShort(recordSizePosition, recordSize);
    }

    // DiskPageSectionFixedLengthRecords interface

    // Used when creating a new page
    public DiskPageSectionFixedLengthRecords(int metadataSize, ByteBuffer buffer)
    {
        super(metadataSize, buffer);
    }

    // Used when reading a page
    public DiskPageSectionFixedLengthRecords(ByteBuffer pageBuffer)
    {
        super(pageBuffer);
        // Parent class requires this subclass to set pageBuffer position to end of what's been read
        pageBuffer.position(dataPosition);
    }

    // For use by this package

    // For testing only
    @Override
    void setBoundariesInBuffer(int position, ByteBuffer dataBuffer)
    {
        assert dataBuffer.array() == buffer.array() : this;
        assert position >= 0 : this;
        assert recordSize >= 0;
        if (position < count) {
            dataBuffer.limit(dataPosition + (position + 1) * recordSize);
            dataBuffer.position(dataPosition + position * recordSize);
        }
    }

    @Override
    void recordEndsAt(int recordEnd)
    {
    }

    // Object state

    private int recordSize = -1;
}
