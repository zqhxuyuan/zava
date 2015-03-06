/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.map.Factory;

import java.nio.ByteBuffer;

class TimestampSection extends DiskPageSectionFixedLengthRecords
{
    // DiskPageSection interface

    @Override
    public void close()
    {
        timestamps.close();
        // Timestamp section metadata
        timestampBase = timestamps.min();
        metadataAppendULong(timestampBase);
        metadataAppendUByte(timestamps.deltaBytes());
        metadataClose();
        recordSize(timestamps.deltaBytes());
        // Write the deltas (relative to the min timestamp)
        append(timestamps);
        super.close();
    }

    // TimestampSection interface

    public void append(long timestamp)
    {
        timestamps.append(timestamp);
    }

    public void removeLast()
    {
        timestamps.removeLast();
    }

    public long timestamp(int position)
    {
        return timestamps.at(position);
    }

    // Size within a disk page. If not closed (which is expected usage), then figure it out from the header
    // size (available from super.size(), known now), and timestamps.
    public int size()
    {
        int size = super.size();
        if (state != State.CLOSED) {
            size += timestamps.size() * timestamps.deltaBytes();
        }
        return size;
    }

    public static TimestampSection forRead(ByteBuffer pageBuffer)
    {
        return new TimestampSection(pageBuffer);
    }

    public static TimestampSection forWrite(ByteBuffer buffer, Factory factory)
    {
        return new TimestampSection(buffer, factory);
    }

    // For use by this class

    private TimestampSection(ByteBuffer pageBuffer)
    {
        super(pageBuffer);
        timestampBase = metadataULong(0);
        int deltaSize = metadataUByte(TIMESTAMP_BASE_SIZE);
        timestamps = new CompressibleLongArray(timestampBase, count, deltaSize);
        timestamps.readFrom(pageBuffer);
        pageBuffer.position(dataPosition + count * timestamps.deltaBytes());
    }

    private TimestampSection(ByteBuffer buffer, Factory factory)
    {
        super(TIMESTAMP_SECTION_METADATA_SIZE, buffer);
        int pageSize = factory.configuration().diskPageSizeBytes();
        // pageSize / 8 is big enough to accomodate 8-byte records. If that's not enough,
        // CompressibleLongArray grows.
        timestamps = new CompressibleLongArray(pageSize / 8);
    }

    // Class state

    // 8 bytes for timestampBase. 1 byte for delta size.
    private static final int TIMESTAMP_BASE_SIZE = 8;
    private static final int TIMESTAMP_DELTA_BYTES_SIZE = 1;
    private static final int TIMESTAMP_SECTION_METADATA_SIZE = TIMESTAMP_BASE_SIZE + TIMESTAMP_DELTA_BYTES_SIZE;

    // Object state

    private long timestampBase;
    private CompressibleLongArray timestamps;
}
