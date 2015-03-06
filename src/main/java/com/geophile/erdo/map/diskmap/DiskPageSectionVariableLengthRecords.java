/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

class DiskPageSectionVariableLengthRecords extends DiskPageSection
{
    // DiskPageSection interface

    @Override
    public void resetToMark()
    {
        super.resetToMark();
        ends.truncate(count);
        size =
            dataPosition +
            (count == 0 ? 0 : (ends.at(count - 1) - dataPosition)) +
            count * OFFSET_SIZE;
    }

    @Override
    public void close()
    {
        if (state != State.CLOSED) {
            // Write index
            for (int i = 0; i < ends.size(); i++) {
                buffer.putChar(ends.at(i));
            }
            super.close(); // sets state to State.CLOSED
        }
    }

    // DiskPageSectionVariableLengthRecords interface

    public static DiskPageSectionVariableLengthRecords forWrite
        (int metadataSize, ByteBuffer buffer)
    {
        return new DiskPageSectionVariableLengthRecords(metadataSize, buffer);
    }

    public static DiskPageSectionVariableLengthRecords forRead
        (ByteBuffer pageBuffer)
    {
        return new DiskPageSectionVariableLengthRecords(pageBuffer);
    }

    // For use by this package

    @Override
    void setBoundariesInBuffer(int position, ByteBuffer dataBuffer)
    {
        assert dataBuffer.array() == buffer.array() : this;
        assert position >= 0 : this;
        if (position < count) {
            dataBuffer.limit(start + end(position));
            dataBuffer.position(position == 0 ? dataPosition : (start + end(position - 1)));
        }
    }

    void recordEndsAt(int recordEnd)
    {
        // Check that remaining space will accomodate index, including new entry. But check now, before
        // appending boundary.
        if (buffer.remaining() < (ends.size() + 1) * OFFSET_SIZE) {
            throw new BufferOverflowException();
        } else {
            appendBoundary(recordEnd);
            size += OFFSET_SIZE;
        }
    }

    // For use by this class

    private void appendBoundary(int boundary)
    {
        ends.append((char) (boundary - start));
    }

    private int end(int position)
    {
        return ends.at(position);
    }

    // Used when creating a new page
    private DiskPageSectionVariableLengthRecords(int metadataSize, ByteBuffer buffer)
    {
        super(metadataSize, buffer);
        this.ends = new UShortArray(buffer.capacity() / 8); // A guess, but UShortArray grows as necessary
    }

    // Used when reading a page
    private DiskPageSectionVariableLengthRecords(ByteBuffer pageBuffer)
    {
        super(pageBuffer);
        this.ends = new UShortArray(this.count);
        int p = this.start + this.size - this.count * OFFSET_SIZE;
        for (int i = 0; i < this.count; i++) {
            this.ends.append((char) readUShort(p));
            p += OFFSET_SIZE;
        }
        // Parent class requires this subclass to set pageBuffer position to end of what's been read
        pageBuffer.position(p);
    }

    // Object state

    private UShortArray ends;
}
