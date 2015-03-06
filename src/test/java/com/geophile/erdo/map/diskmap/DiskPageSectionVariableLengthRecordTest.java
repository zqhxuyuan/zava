/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.util.Transferrable;
import org.junit.Test;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DiskPageSectionVariableLengthRecordTest
{
    @Test
    public void testEmpty()
    {
        DiskPageSection s = DiskPageSectionVariableLengthRecords.forWrite(0, newDiskPageSectionBuffer());
        s.metadataClose();
        try {
            s.metadataAppendUInt(0);
            fail();
        } catch (AssertionError e) {
        }
        assertEquals(0, s.count());
        assertEquals(8, s.size());
        s.close();
        assertEquals(0, s.count());
        assertEquals(8, s.size());
        try {
            s.append(new TestValue(0));
            fail();
        } catch (AssertionError e) {
        }
    }

    @Test
    public void testMetadataOnly()
    {
        DiskPageSection s = DiskPageSectionVariableLengthRecords.forWrite(6, newDiskPageSectionBuffer());
        s.metadataAppendUInt(123);
        s.metadataAppendUShort(456);
        assertEquals(0, s.count());
        assertEquals(14, s.size());
        try {
            s.metadataUInt(0);
            fail();
        } catch (AssertionError e) {
        }
        s.metadataClose();
        assertEquals(0, s.count());
        assertEquals(14, s.size());
        try {
            s.metadataUInt(0);
            fail();
        } catch (AssertionError e) {
        }
        s.close();
        assertEquals(123, s.metadataUInt(0));
        assertEquals(456, s.metadataUShort(4));
        assertEquals(0, s.count());
        assertEquals(14, s.size());
    }

    @Test
    public void testUnsignedMetadata()
    {
        DiskPageSection s = DiskPageSectionVariableLengthRecords.forWrite(6, newDiskPageSectionBuffer());
        s.metadataAppendUInt(0xffffffffL);
        s.metadataAppendUShort(0xffff);
        s.metadataClose();
        s.close();
        assertEquals(0xffffffffL, s.metadataUInt(0));
        assertEquals(0xffff, s.metadataUShort(4));
    }

    @Test
    public void testMetadataOverflow()
    {
        DiskPageSection s = DiskPageSectionVariableLengthRecords.forWrite(6, newDiskPageSectionBuffer());
        s.metadataAppendUInt(123);
        try {
            s.metadataAppendUInt(456);
            fail();
        } catch (AssertionError e) {
        }
    }

    @Test
    public void testData()
    {
        // Write records of size 1, 2, .... buffer size is 4k, metadata size is 0. There should be
        // room for 87 records with 88 bytes unused.
        final int N = 87;
        DiskPageSection s = DiskPageSectionVariableLengthRecords.forWrite(0, newDiskPageSectionBuffer());
        assertEquals(0, s.count());
        assertEquals(8, s.size());
        for (int size = 1; size <= N; size++) {
            s.append(new TestValue(size));
            assertEquals(size, s.count());
            assertEquals(8 + size * (size + 1) / 2 + 2 * size, s.size());
        }
        try {
            s.append(new TestValue(N + 1));
            fail();
        } catch (BufferOverflowException e) {
        }
        assertEquals(N, s.count());
        assertEquals(8 + N * (N + 1) / 2 + N * 2, s.size());
        s.close();
        assertEquals(N, s.count());
        assertEquals(8 + N * (N + 1) / 2 + N * 2, s.size());
        ByteBuffer buffer = s.accessBuffer();
        for (int position = 0; position < 87; position++) {
            s.setBoundariesInBuffer(position, buffer);
            int size = position + 1;
            new TestValue(size).readFrom(buffer);
        }
        try {
            s.setBoundariesInBuffer(-1, buffer);
            fail();
        } catch (AssertionError e) {
        }
    }

    @Test
    public void testEmptyRecords()
    {
        // Write 3 0-length records, a record of size 10 and then 3 more 0-length.
        DiskPageSection s = DiskPageSectionVariableLengthRecords.forWrite(0, newDiskPageSectionBuffer());
        TestValue v0 = new TestValue(0);
        TestValue v10 = new TestValue(10);
        s.append(v0);
        s.append(v0);
        s.append(v0);
        s.append(v10);
        s.append(v0);
        s.append(v0);
        s.append(v0);
        s.close();
        assertEquals(7, s.count());
        assertEquals(8 + 10 + 7 * 2, s.size());
        ByteBuffer buffer = s.accessBuffer();
        s.setBoundariesInBuffer(0, buffer);
        v0.readFrom(buffer);
        s.setBoundariesInBuffer(1, buffer);
        v0.readFrom(buffer);
        s.setBoundariesInBuffer(2, buffer);
        v0.readFrom(buffer);
        s.setBoundariesInBuffer(3, buffer);
        v10.readFrom(buffer);
        s.setBoundariesInBuffer(4, buffer);
        v0.readFrom(buffer);
        s.setBoundariesInBuffer(5, buffer);
        v0.readFrom(buffer);
        s.setBoundariesInBuffer(6, buffer);
        v0.readFrom(buffer);
    }

    @Test
    public void testMarkReset()
    {
        DiskPageSection s = DiskPageSectionVariableLengthRecords.forWrite(0, newDiskPageSectionBuffer());
        s.append(new TestValue(1));
        s.append(new TestValue(2));
        assertEquals(2, s.count());
        assertEquals(8 + 3 + 4, s.size());
        s.mark();
        s.append(new TestValue(3));
        s.append(new TestValue(4));
        assertEquals(4, s.count());
        assertEquals(8 + 10 + 8, s.size());
        s.resetToMark();
        assertEquals(2, s.count());
        assertEquals(8 + 3 + 4, s.size());
        s.append(new TestValue(5));
        s.append(new TestValue(6));
        assertEquals(4, s.count());
        assertEquals(8 + 14 + 8, s.size());
        s.close();
        assertEquals(4, s.count());
        assertEquals(8 + 14 + 8, s.size());
        assertEquals(4, s.count());
        ByteBuffer buffer = s.accessBuffer();
        s.setBoundariesInBuffer(0, buffer);
        new TestValue(1).readFrom(buffer);
        s.setBoundariesInBuffer(1, buffer);
        new TestValue(2).readFrom(buffer);
        s.setBoundariesInBuffer(2, buffer);
        new TestValue(5).readFrom(buffer);
        s.setBoundariesInBuffer(3, buffer);
        new TestValue(6).readFrom(buffer);
    }

    private ByteBuffer newDiskPageSectionBuffer()
    {
        return ByteBuffer.allocate(BUFFER_SIZE);
    }

    private static final int BUFFER_SIZE = 4096;

    private static class TestValue implements Transferrable
    {
        public void writeTo(ByteBuffer buffer) throws BufferOverflowException
        {
            for (int i = 0; i < size; i++) {
                buffer.put((byte)(i % 10));
            }
        }

        public void readFrom(ByteBuffer buffer)
        {
            for (int i = 0; i < size; i++) {
                assertEquals(i % 10, buffer.get());
            }
            assertEquals(0, buffer.remaining());
        }

        public int recordCount()
        {
            return 1;
        }

        public TestValue(int size)
        {
            this.size = size;
        }

        private final int size;
    }
}
