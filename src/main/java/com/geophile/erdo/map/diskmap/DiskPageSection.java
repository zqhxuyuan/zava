/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.util.Transferrable;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/*
 * For all DiskPageSections:
 *     SIZE: 2 byte section size (including header).
 *     COUNT: 2 byte count of DATA records, (counts the calls to append).
 *     METADATA_SIZE: 2 byte size of METADATA.
 *     METADATA: fixed-size area containing metadata.
 *     RECORD_SIZE: 2-byte size of each record (used only by DiskPageSectionFixedLengthRecords)
 *
 * For all DiskPageSections
 *     DATA: COUNT records with no separators.
 *
 * For DiskPageSectionVariableLengthRecords only:
 *     END: Array of 2-byte deltas into DATA area, (relative to start of DATA).
 *
 *  Due to the use of 2-byte integers, a section size is limited to 64k.
 */

abstract class DiskPageSection
{
    // DiskPageSection interface

    public final int metadataUByte(int position)
    {
        assert state == State.CLOSED : this;
        return buffer.get(metadataPosition + position) & 0xff;
    }

    public final int metadataUShort(int position)
    {
        assert state == State.CLOSED : this;
        return buffer.getChar(metadataPosition + position) & 0xffff;
    }

    public final long metadataUInt(int position)
    {
        assert state == State.CLOSED : this;
        return ((long) buffer.getInt(metadataPosition + position)) & MAX_UINT;
    }

    public final long metadataULong(int position)
    {
        assert state == State.CLOSED : this;
        return buffer.getLong(metadataPosition + position);
    }

    public final void metadataAppendUByte(int x)
    {
        assert state == State.METADATA : this;
        assert x >= 0;
        assert x <= MAX_UBYTE : this;
        assert buffer.position() >= metadataPosition : this;
        buffer.put((byte) x);
        assert buffer.position() <= dataPosition : this;
    }

    public final void metadataAppendUShort(int x)
    {
        assert state == State.METADATA : this;
        assert x >= 0;
        assert x <= MAX_USHORT : this;
        assert buffer.position() >= metadataPosition : this;
        buffer.putChar((char) x);
        assert buffer.position() <= dataPosition : this;
    }

    public final void metadataAppendUInt(long x)
    {
        assert state == State.METADATA : this;
        assert x >= 0;
        assert x <= MAX_UINT : this;
        assert buffer.position() >= metadataPosition : this;
        buffer.putInt((int) x);
        assert buffer.position() <= dataPosition : this;
    }

    public final void metadataAppendULong(long x)
    {
        assert state == State.METADATA : this;
        assert x >= 0;
        assert buffer.position() >= metadataPosition : this;
        buffer.putLong(x);
        assert buffer.position() <= dataPosition : this;
    }

    public final void metadataClose()
    {
        state = State.DATA;
        buffer.position(dataPosition);
    }

    public final void append(Transferrable data)
        throws BufferOverflowException
    {
        assert state == State.DATA : this;
        assert buffer.position() >= dataPosition : this;
        assert data != null;
        buffer.mark();
        try {
            int recordStart = buffer.position();
            data.writeTo(buffer);
            int recordEnd = buffer.position();
            recordEndsAt(recordEnd);
            size += recordEnd - recordStart;
            count += data.recordCount();
        } catch (BufferOverflowException e) {
            buffer.reset();
            throw e;
        }
    }

    public final void append(ByteBuffer data)
        throws BufferOverflowException
    {
        assert state == State.DATA : this;
        assert buffer.position() >= dataPosition : this;
        assert data != null;
        int recordStart = buffer.position();
        buffer.put(data); // If insufficient space, leaves buffer alone and throws BufferOverflowException
        int recordEnd = buffer.position();
        recordEndsAt(recordEnd);
        size += recordEnd - recordStart;
        count++; // Because append(ByteBuffer) is only used for single records
    }

    public final void mark()
    {
        assert state == State.DATA || state == State.METADATA : this;
        markCount = count;
        markPosition = buffer.position();
    }

    public void resetToMark()
    {
        assert state == State.DATA || state == State.METADATA : this;
        assert markCount >= 0 : this;
        assert markPosition >= 0 : this;
        count = markCount;
        buffer.position(markPosition);
        markCount = -1;
        markPosition = -1;
        // Subclass resets size and its own state
    }

    public int size()
    {
        return size;
    }

    public void close()
    {
        if (state != State.CLOSED) {
            // Write size and count
            assert size == buffer.position() - start;
            buffer.putChar(start, (char) size);
            buffer.putChar(countPosition, (char) count);
            state = State.CLOSED;
        }
    }

    public final int count()
    {
        return count;
    }

    public final ByteBuffer buffer()
    {
        return buffer;
    }

    public final ByteBuffer accessBuffer()
    {
        return buffer.duplicate();
    }

    public void recordSize(int recordSize)
    {
        throw new UnsupportedOperationException();
    }

    // For use by subclasses

    // Used when creating a new page
    protected DiskPageSection(int metadataSize, ByteBuffer buffer)
    {
        this.buffer = buffer;
        this.metadataSize = metadataSize;
        this.start = 0;
        this.countPosition = this.start + OFFSET_SIZE;
        this.metadataSizePosition = this.countPosition + OFFSET_SIZE;
        this.metadataPosition = this.metadataSizePosition + OFFSET_SIZE;
        this.recordSizePosition = this.metadataPosition + metadataSize;
        this.dataPosition = this.recordSizePosition + OFFSET_SIZE;
        // Write metadataSize
        writeUShort(this.metadataSizePosition, metadataSize);
        // Prepare to write metadata, or if there is none, then data.
        if (metadataSize == 0) {
            this.state = State.DATA;
            this.buffer.position(this.dataPosition);
        } else {
            this.state = State.METADATA;
            this.buffer.position(this.metadataPosition);
        }
        this.size = this.dataPosition - this.start;
        this.count = 0;
    }

    // Used when reading a page
    protected DiskPageSection(ByteBuffer pageBuffer)
    {
        this.buffer = pageBuffer.duplicate();
        this.start = this.buffer.position();
        this.countPosition = this.start + OFFSET_SIZE;
        this.metadataSizePosition = this.countPosition + OFFSET_SIZE;
        this.metadataPosition = this.metadataSizePosition + OFFSET_SIZE;
        this.metadataSize = readUShort(this.metadataSizePosition);
        this.recordSizePosition = this.metadataPosition + this.metadataSize;
        this.dataPosition = this.recordSizePosition + OFFSET_SIZE;
        this.size = readUShort(this.start);
        this.count = readUShort(this.countPosition);
        this.state = State.CLOSED;
        this.buffer.limit(this.buffer.position() + this.size);
        // Subclass sets pageBuffer.position to end of what's been read
    }

    // For use by this package

    abstract void recordEndsAt(int recordEnd);

    abstract void setBoundariesInBuffer(int position, ByteBuffer dataBuffer);

    final int readUShort(int position)
    {
        return buffer.getChar(position);
    }

    final void writeUShort(int position, int value)
    {
        buffer.putChar(position, (char) value);
    }

    // Class state

    private static final long MAX_UBYTE = 0xffL;
    private static final long MAX_USHORT = 0xffffL;
    private static final long MAX_UINT = 0xffffffffL;
    protected static final int OFFSET_SIZE = 2;

    // Object state

    protected final ByteBuffer buffer;
    protected final int metadataSize;
    protected final int start; // position of size
    protected final int countPosition;
    protected final int metadataSizePosition;
    protected final int metadataPosition;
    protected final int recordSizePosition;
    protected final int dataPosition;
    protected State state;
    protected int count;
    protected int size;
    private int markCount = -1;
    private int markPosition = -1;

    // Inner classes

    protected enum State
    {
        METADATA, DATA, CLOSED
    }
}
