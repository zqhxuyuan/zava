/*
 * Copyright 2012 David Tinker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.qdb.buffer;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ChannelInputTest {

    private static File dir = new File("build/test-data");

    @BeforeClass
    public static void beforeClass() throws IOException {
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IOException("Unable to create [" + dir + "]");
        }
    }

    @Test
    public void testRead() throws IOException {
        File file = new File(dir, "read.dat");
        file.delete();

        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
        out.writeByte(0x23);
        out.writeShort(0x1234);
        out.writeInt(0x12345678);
        out.writeLong(0x1234567898765432L);
        // total 1 + 2 + 4 + 8 = 15 bytes

        // put a short across a buffer boundary
        for (int i = 0; i < 8192 - 15 - 1; i++) out.write(0);
        out.writeShort(0x4321);

        // put an int across the next buffer boundary
        for (int i = 0; i < 8192 - 1 - 3; i++) out.write(0);
        out.writeInt(0x1a2b3c4d);

        // put a long across the next buffer boundary
        for (int i = 0; i < 8192 - 1 - 7; i++) out.write(0);
        out.writeLong(0x1122334455667788L);

        // now write several buffers worth of data
        for (int i = 0; i < 8192 * 3; i++) out.writeByte(i);

        out.close();

        FileInputStream ins = new FileInputStream(file);
        ChannelInput in = new ChannelInput(ins.getChannel(), 0, 8192);

        assertEquals((byte)0x23, in.readByte());
        assertEquals((short)0x1234, in.readShort());
        assertEquals(0x12345678, in.readInt());
        assertEquals(0x1234567898765432L, in.readLong());

        in.skip(8192 - 15 - 1);
        assertEquals((short)0x4321, in.readShort());

        in.skip(8192 - 1 - 3);
        assertEquals(0x1a2b3c4d, in.readInt());

        in.skip(8192 - 1 - 7);
        assertEquals(0x1122334455667788L, in.readLong());

        byte[] data = new byte[8192 * 3];
        in.read(data, 0, data.length);
        for (int i = 0; i < data.length; i++) {
            assertEquals((byte)i, data[i]);
        }

        ins.close();
   }

    @Test
    public void testSeek() throws IOException {
        File file = new File(dir, "seek.dat");
        file.delete();

        // fill file with 3 buffers worth of random data
        Random rnd = new Random(123);
        byte[] data = new byte[8192 * 3];
        rnd.nextBytes(data);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();

        FileInputStream ins = new FileInputStream(file);
        ChannelInput in = new ChannelInput(ins.getChannel(), 0, 8192);

        in.position(0);
        assertEquals(data[0], in.readByte());

        in.position(8190);  // 2 bytes before end of buffer so next position check must move buffer
        assertEquals(data[8190], in.readByte());

        in.position(8192);  // at next buffer position
        assertEquals(data[8192], in.readByte());

        in.position(16383); // just before end of buffer
        assertEquals(data[16383], in.readByte());

        in.position(8192);  // start of buffer
        assertEquals(data[8192], in.readByte());

        in.position(8191);  // just before start of buffer
        assertEquals(data[8191], in.readByte());

        ins.close();
    }

}