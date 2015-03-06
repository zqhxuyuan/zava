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

public class DiskPageSectionFixedLengthRecordTest
{
    @Test
    public void testEmpty()
    {
        DiskPageSection s = new DiskPageSectionFixedLengthRecords(0, newDiskPageSectionBuffer());
        s.recordSize(4);
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
        DiskPageSection s = new DiskPageSectionFixedLengthRecords(6, newDiskPageSectionBuffer());
        s.recordSize(4);
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
        DiskPageSection s = new DiskPageSectionFixedLengthRecords(6, newDiskPageSectionBuffer());
        s.recordSize(4);
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
        DiskPageSection s = new DiskPageSectionFixedLengthRecords(6, newDiskPageSectionBuffer());
        s.recordSize(4);
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
        // Section size is 8 + 4 * #records. Buffer size is 4k, so #records <= 1022.
        final int N = 1022;
        DiskPageSection s = new DiskPageSectionFixedLengthRecords(0, newDiskPageSectionBuffer());
        s.recordSize(4);
        assertEquals(0, s.count());
        assertEquals(8, s.size());
        for (int i = 0; i < N; i++) {
            s.append(new TestValue(i));
            assertEquals(i + 1, s.count());
            assertEquals(8 + (i + 1) * 4, s.size());
        }
        try {
            s.append(new TestValue(N + 1));
            fail();
        } catch (BufferOverflowException e) {
        }
        assertEquals(N, s.count());
        assertEquals(8 + N * 4, s.size());
        s.close();
        assertEquals(N, s.count());
        assertEquals(8 + N * 4, s.size());
        ByteBuffer buffer = s.accessBuffer();
        for (int i = 0; i < N; i++) {
            s.setBoundariesInBuffer(i, buffer);
            new TestValue(i).readFrom(buffer);
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
        // Write 3 0-length records
        DiskPageSection s = new DiskPageSectionFixedLengthRecords(0, newDiskPageSectionBuffer());
        s.recordSize(4);
        TestValue v123 = new TestValue(123);
        TestValue v456 = new TestValue(456);
        TestValue v789 = new TestValue(789);
        s.append(v123);
        s.append(v456);
        s.append(v789);
        s.close();
        assertEquals(3, s.count());
        assertEquals(8 + 4 * 3, s.size());
        ByteBuffer buffer = s.accessBuffer();
        s.setBoundariesInBuffer(0, buffer);
        v123.readFrom(buffer);
        s.setBoundariesInBuffer(1, buffer);
        v456.readFrom(buffer);
        s.setBoundariesInBuffer(2, buffer);
        v789.readFrom(buffer);
    }

    @Test
    public void testMarkReset()
    {
        DiskPageSection s = new DiskPageSectionFixedLengthRecords(0, newDiskPageSectionBuffer());
        s.recordSize(4);
        s.append(new TestValue(1));
        s.append(new TestValue(2));
        assertEquals(2, s.count());
        assertEquals(8 + 2 * 4, s.size());
        s.mark();
        s.append(new TestValue(3));
        s.append(new TestValue(4));
        assertEquals(4, s.count());
        assertEquals(8 + 4 * 4, s.size());
        s.resetToMark();
        assertEquals(2, s.count());
        assertEquals(8 + 2 * 4, s.size());
        s.append(new TestValue(5));
        s.append(new TestValue(6));
        assertEquals(4, s.count());
        assertEquals(8 + 4 * 4, s.size());
        s.close();
        assertEquals(4, s.count());
        assertEquals(8 + 4 * 4, s.size());
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
            buffer.putInt(value);
        }

        public void readFrom(ByteBuffer buffer)
        {
            assertEquals(value, buffer.getInt());
        }

        public int recordCount()
        {
            return 1;
        }

        public TestValue(int value)
        {
            this.value = value;
        }

        private final int value;
    }
}
