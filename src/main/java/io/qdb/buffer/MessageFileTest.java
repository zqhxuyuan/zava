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
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MessageFileTest {

    private static File dir = new File("build/test-data");

    @BeforeClass
    public static void beforeClass() throws IOException {
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IOException("Unable to create [" + dir + "]");
        }
    }

    @Test
    public void testAppend() throws IOException {
        File file = new File(dir, "append-from-channel.qdb");
        file.delete();

        MessageFile mf = new MessageFile(file, 1000L, 1000000);

        long ts0 = System.currentTimeMillis();
        String key0 = "foo";
        byte[] payload0 = "piggy".getBytes("UTF8");
        int length0 = 1/*type*/ + 8/*timestamp*/ + 2/*key size*/ + 4/* payload size*/ + key0.length() + payload0.length;

        long ts1 = ts0 + 1;
        String key1 = "foobar";
        byte[] payload1 = "oink".getBytes("UTF8");
        int length1 = 1/*type*/ + 8/*timestamp*/ + 2/*key size*/ + 4/* payload size*/ + key1.length() + payload1.length;

        assertEquals(1000L, mf.append(ts0, key0, toChannel(payload0), payload0.length));
        assertEquals(1000L + length0, mf.append(ts1, key1, toChannel(payload1), payload1.length));

        int expectedLength = 4096/*file header*/ + length0 + length1;
        assertEquals(expectedLength, mf.length());
        mf.close();
        assertEquals(expectedLength, file.length());

        DataInputStream ins = new DataInputStream(new FileInputStream(file));

        assertEquals((short)0xBE01, ins.readShort());   // magic
        assertEquals((short)0, ins.readShort());        // reserved
        assertEquals(1000000, ins.readInt());           // max file size
        assertEquals(expectedLength, ins.readInt());    // checkpoint
        assertEquals(0, ins.readInt());                 // reserved

        assertEquals(0, ins.readInt());                 // bucket first message id (relative to file)
        assertEquals(ts0, ins.readLong());              // bucket timestamp
        assertEquals(2, ins.readInt());                 // bucket count

        for (int i = 16 + 16; i < 4096; i += 16) {
            assertEquals(-1, ins.readInt());
            assertEquals(0L, ins.readLong());
            assertEquals(0, ins.readInt());
        }

        assertEquals((byte)0xA1, ins.readByte());   // type
        assertEquals(ts0, ins.readLong());
        assertEquals(key0.length(), (int)ins.readShort());
        assertEquals(payload0.length, ins.readInt());
        assertEquals(key0, readUTF8(ins, key0.length()));
        assertEquals(new String(payload0, "UTF8"), readUTF8(ins, payload0.length));

        assertEquals((byte)0xA1, ins.readByte());   // type
        assertEquals(ts1, ins.readLong());
        assertEquals(key1.length(), (int)ins.readShort());
        assertEquals(payload1.length, ins.readInt());
        assertEquals(key1, readUTF8(ins, key1.length()));
        assertEquals(new String(payload1, "UTF8"), readUTF8(ins, payload1.length));

        ins.close();
    }

    private String readUTF8(InputStream ins, int length) throws IOException {
        return new String(readBytes(ins, length), "UTF8");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private byte[] readBytes(InputStream ins, int length) throws IOException {
        byte[] buf = new byte[length];
        assertEquals(length, ins.read(buf));
        return buf;
    }

    private ReadableByteChannel toChannel(byte[] data) {
        return Channels.newChannel(new ByteArrayInputStream(data));
    }

    private long append(MessageFile mf, long timestamp, String routingKey, byte[] data) throws IOException {
        return mf.append(timestamp, routingKey, toChannel(data), data.length);
    }

    @Test
    public void testCheckpoint() throws IOException {
        File file = new File(dir, "checkpoint.qdb");
        file.delete();
        MessageFile mf = new MessageFile(file, 0, 1000000);
        append(mf, System.currentTimeMillis(), "", "oink".getBytes("UTF8"));
        mf.checkpoint(false);
        mf.close();

        DataInputStream ins = new DataInputStream(new FileInputStream(file));
        int expectedLength = (int) file.length();
        ins.skip(2 + 2 + 4);
        assertEquals(expectedLength, ins.readInt());
        ins.close();

        FileOutputStream out = new FileOutputStream(file, true);
        out.write("junk".getBytes("UTF8"));
        out.close();

        assertEquals(expectedLength + 4, file.length());
        new MessageFile(file, 0, 1000000).close();
        assertEquals(expectedLength, file.length());
    }

    @Test
    public void testUnclosedFileNotCorrupt() throws IOException {
        File file = new File(dir, "unclosed.qdb");
        file.delete();
        MessageFile mf = new MessageFile(file, 0, 1000000);
        append(mf, System.currentTimeMillis(), "", "oink".getBytes("UTF8"));

        // this will fail with an IOException if the file is corrupt
        MessageFile mf2 = new MessageFile(file, 0, 1000000);

        mf2.close();
        mf.close();
    }

    @Test
    public void testRead() throws IOException {
        File file = new File(dir, "read.qdb");
        file.delete();
        MessageFile mf = new MessageFile(file, 1000, 1000000);

        try {
            mf.cursor(999);     // before start
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ignore) {
        }

        assertFalse(mf.cursor(1000).next());    // first message id

        try {
            mf.cursor(1001);    // after end
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ignore) {
        }

        long ts0 = System.currentTimeMillis();
        String key0 = "foo";
        byte[] payload0 = "piggy".getBytes("UTF8");
        long id0 = append(mf, ts0, key0, payload0);

        long ts1 = ts0 + 1;
        String key1 = "foobar";
        byte[] payload1 = "oink".getBytes("UTF8");
        long id1 = append(mf, ts1, key1, payload1);

        MessageCursor i = mf.cursor(1000);

        assertTrue(i.next());
        assertEquals(id0, i.getId());
        assertEquals(ts0, i.getTimestamp());
        assertEquals(key0, i.getRoutingKey());
        assertArrayEquals(payload0, i.getPayload());
        assertEquals(id1, i.getNextId());

        assertTrue(i.next());
        assertEquals(id1, i.getId());
        assertEquals(ts1, i.getTimestamp());
        assertEquals(key1, i.getRoutingKey());
        assertArrayEquals(payload1, i.getPayload());

        assertFalse(i.next());
    }

    @Test
    public void testMaxLength() throws IOException {
        File file = new File(dir, "max-length.qdb");
        file.delete();

        Random rnd = new Random(456);
        byte[] msg = new byte[101];
        rnd.nextBytes(msg);


        MessageFile mf = new MessageFile(file, 0, 4096 + 15/*header*/ + 100);
        long id = append(mf, System.currentTimeMillis(), "", Arrays.copyOf(msg, 100));
        assertEquals(id, 0);
        mf.close();

        file.delete();
        mf = new MessageFile(file, 0, 4096 + 15/*header*/ + 100);
        id = append(mf, System.currentTimeMillis(), "", Arrays.copyOf(msg, 101));
        assertEquals(id, -1);
        mf.close();
    }

    @Test
    public void testHistogramWrite() throws IOException {
        File file = new File(dir, "histogram-write.qdb");
        file.delete();

        int maxBuckets = 255;

        // file is big enough for 2 * 255 messages each 100 bytes so there will be 2 per bucket and all buckets
        // will be used
        MessageFile mf = new MessageFile(file, 0, 4096 + 2 * maxBuckets * (100 + 15));

        Random rnd = new Random(123);
        byte[] msg = new byte[100];
        rnd.nextBytes(msg);

        long ts = System.currentTimeMillis();
        int c;
        for (c = 0; c < 100000; c++) {
            long id = append(mf, ts + c * 1000, "", msg);
            if (id < 0) break;
        }
        mf.close();

        assertEquals(maxBuckets * 2, c);

        DataInputStream ins = new DataInputStream(new FileInputStream(file));
        ins.skip(16);

        for (int i = 0; i < maxBuckets; i++) {
            assertEquals(i * (100 + 15) * 2, ins.readInt());
            assertEquals(ts + i * 2000, ins.readLong());
            assertEquals(2, ins.readInt());
        }
        ins.close();
    }

    @Test
    public void testHistogramRead() throws IOException {
        File file = new File(dir, "histogram-read.qdb");
        file.delete();

        int maxBuckets = 255;

        // file is big enough for 2 * 255 messages each 100 bytes so there will be 2 per bucket and all buckets
        // will be used
        MessageFile mf = new MessageFile(file, 1000, 4096 + 2 * maxBuckets * (100 + 15));

        byte[] msg = new byte[100];
        long ts = (System.currentTimeMillis() / 1000L) * 1000L;
        for (int i = 0; i < maxBuckets * 2; i++) {
            append(mf, ts + i * 1000, "", msg);
        }

        for (int i = 0; i < maxBuckets; i++) {
            MessageFile.Bucket b = mf.getBucket(i);
            String m = "bucket[" + i + "]";
            assertEquals(m, 1000 + i * (100 + 15) * 2, b.getFirstMessageId());
            assertEquals(m, ts + i * 2000, b.getTimestamp());
            assertEquals(m, 2, b.getCount());
            assertEquals(m, (100 + 15) * 2, b.getSize());
        }

        try {
            mf.getBucket(-1);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ignore) {
        }

        try {
            mf.getBucket(maxBuckets);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException ignore) {
        }

        assertTrue(mf.getBucket(0).toString().contains("Bucket"));

        assertEquals(-1, mf.findBucket(999));  // before first message
        assertEquals(0, mf.findBucket(1000));  // first message
        assertEquals(0, mf.findBucket(1001));
        assertEquals(0, mf.findBucket(1229));  // last message in first bucket
        assertEquals(1, mf.findBucket(1230));  // first message in 2nd bucket
        assertEquals(maxBuckets - 2, mf.findBucket(1000 + (maxBuckets - 1) * 230 - 1));    // 2nd last bucket
        assertEquals(maxBuckets - 1, mf.findBucket(1000 + (maxBuckets - 1) * 230));        // last bucket
        assertEquals(maxBuckets - 1, mf.findBucket(1000 + (maxBuckets - 1) * 230 + 229));

        assertEquals(-1, mf.findBucketByTimestamp(ts - 1));  // before first message
        assertEquals(0, mf.findBucketByTimestamp(ts));  // first message
        assertEquals(0, mf.findBucketByTimestamp(ts + 1));
        assertEquals(0, mf.findBucketByTimestamp(ts + 1999));  // last message in first bucket
        assertEquals(1, mf.findBucketByTimestamp(ts + 2000));  // first message in 2nd bucket
        assertEquals(maxBuckets - 2, mf.findBucketByTimestamp(ts + (maxBuckets - 1) * 2000 - 1));    // 2nd last bucket
        assertEquals(maxBuckets - 1, mf.findBucketByTimestamp(ts + (maxBuckets - 1) * 2000));        // last bucket
        assertEquals(maxBuckets - 1, mf.findBucketByTimestamp(ts + (maxBuckets - 1) * 2000 + 1999));

        mf.close();
    }

    @Test
    public void testTimeline() throws IOException {
        File file = new File(dir, "timeline.qdb");
        file.delete();

        int maxBuckets = 255;

        // file is big enough for 2 * 255 messages each 100 bytes so there will be 2 per bucket and all buckets
        // will be used
        MessageFile mf = new MessageFile(file, 1000, 4096 + 2 * maxBuckets * (100 + 15));

        byte[] msg = new byte[100];
        long ts = (System.currentTimeMillis() / 1000L) * 1000L;
        for (int i = 0; i < maxBuckets * 2; i++) {
            append(mf, ts + i * 1000, "", msg);
        }

        Timeline t = mf.getTimeline();
        assertEquals(255, t.size());
        for (int i = 0; i < t.size(); i++) {
            String m = "timeline[" + i + "]";
            System.out.println(m + " " + t.getMillis(i) + " millis");
            assertEquals(m, 1000 + i * (100 + 15) * 2, t.getMessageId(i));
            assertEquals(m, ts + i * 2000, t.getTimestamp(i));
            assertEquals(m, i == maxBuckets - 1 ? 1000 : 2000, t.getMillis(i));
            assertEquals(m, 2, t.getCount(i));
            assertEquals(m, (100 + 15) * 2, t.getBytes(i));
        }
        mf.close();

        // repeat check on a newly opened file
        mf = new MessageFile(file, 1000, 4096 + 2 * maxBuckets * (100 + 15));
        t = mf.getTimeline();
        assertEquals(255, t.size());
        for (int i = 0; i < t.size(); i++) {
            String m = "timeline[" + i + "]";
            System.out.println(m + " " + t.getMillis(i) + " millis");
            assertEquals(m, 1000 + i * (100 + 15) * 2, t.getMessageId(i));
            assertEquals(m, ts + i * 2000, t.getTimestamp(i));
            assertEquals(m, i == maxBuckets - 1 ? 1000 : 2000, t.getMillis(i));
            assertEquals(m, 2, t.getCount(i));
            assertEquals(m, (100 + 15) * 2, t.getBytes(i));
        }
        mf.close();
    }

    @Test
    public void testReadBetweenMessages() throws IOException {
        File file = new File(dir, "read-between-messages.qdb");
        file.delete();

        // 1000000 / 340 = approx 2900 bytes per bucket
        MessageFile mf = new MessageFile(file, 1000, 1000000);

        // write random messages until the file is full
        Random rnd = new Random(123);
        long ts = 1351279645901L;
        List<Msg> list = new ArrayList<Msg>();
        while (true) {
            Msg msg = new Msg(ts += rnd.nextInt(1000) + 1, rnd, 500);
            // avg approx 6 messages per bucket so some skipping will be required
            msg.id = append(mf, msg.timestamp, msg.routingKey, msg.payload);
            if (msg.id < 0) break;
            list.add(msg);
        }

        // check we can read back each message starting at its id
        for (Msg msg : list) {
            MessageCursor c = mf.cursor(msg.id);
            assertTrue(c.next());
            assertEquals(msg.id, c.getId());
            assertEquals(msg.timestamp, c.getTimestamp());
            assertEquals(msg.routingKey, c.getRoutingKey());
            assertArrayEquals(msg.payload, c.getPayload());
            c.close();
        }

        // check we can read back each message starting at its id less a random number of bytes
        for (int i = 0; i < list.size(); i++) {
            Msg msg = list.get(i);
            // more than 15 bytes might put us on the prev msg
            MessageCursor c = mf.cursor(msg.id - (i > 0 ? rnd.nextInt(15) : 0));
            assertTrue(c.next());
            assertEquals(msg.id, c.getId());
            c.close();
        }

        // check reading from most recent returns false for next
        MessageCursor cc = mf.cursor(mf.getNextMessageId());
        assertFalse(cc.next());
        cc.close();

        // check we can read back each message starting at its timestamp
        for (Msg msg : list) {
            MessageCursor c = mf.cursorByTimestamp(msg.timestamp);
            assertTrue(c.next());
            assertEquals(msg.id, c.getId());
            assertEquals(msg.timestamp, c.getTimestamp());
            assertEquals(msg.routingKey, c.getRoutingKey());
            assertArrayEquals(msg.payload, c.getPayload());
            c.close();
        }

        mf.close();
    }

    @Test
    public void testFindBucketIndexEmptyFile() throws IOException {
        File file = new File(dir, "find-bucket-index-empty.qdb");
        file.delete();

        MessageFile mf = new MessageFile(file, 1000, 100000);
        assertEquals(0, mf.getBucketCount());
        assertEquals(-1, mf.findBucket(1000));
        mf.close();
    }

    @Test
    public void testCursorSeesNewMessage() throws IOException {
        File file = new File(dir, "cursor-sees-new-msg.qdb");
        file.delete();

        MessageFile mf = new MessageFile(file, 1000, 100000);
        MessageCursor c = mf.cursor(1000);
        assertFalse(c.next());

        long ts0 = System.currentTimeMillis();
        String key0 = "foo";
        byte[] payload0 = "piggy".getBytes("UTF8");
        long id0 = append(mf, ts0, key0, payload0);
        assertTrue(c.next());
        assertEquals(id0, c.getId());
        assertEquals(ts0, c.getTimestamp());
        assertEquals(payload0.length, c.getPayloadSize());
        assertArrayEquals(payload0, c.getPayload());
        assertFalse(c.next());

        long ts1 = ts0 + 1;
        String key1 = "foobar";
        byte[] payload1 = "oink".getBytes("UTF8");
        long id1 = append(mf, ts1, key1, payload1);
        assertTrue(c.next());
        assertEquals(id1, c.getId());
        assertEquals(ts1, c.getTimestamp());
        assertArrayEquals(payload1, c.getPayload());

        mf.close();
    }

    @Test
    public void testUsageCounter() throws IOException {
        File file = new File(dir, "usage-counter");
        file.delete();

        MessageFile mf = new MessageFile(file, 1000, 100000);
        assertTrue(mf.isOpen());
        mf.closeIfUnused();
        assertFalse(mf.isOpen());

        mf = new MessageFile(file, 1000, 100000);
        mf.use();
        mf.use();
        mf.closeIfUnused();
        assertTrue(mf.isOpen());
        mf.closeIfUnused();
        assertTrue(mf.isOpen());
        mf.closeIfUnused();
        assertFalse(mf.isOpen());
    }

    @Test
    public void testMostRecentTimestamp() throws IOException {
        File file = new File(dir, "most-recent-timestamp");
        file.delete();

        MessageFile mf = new MessageFile(file, 1000, 1000000);
        assertEquals(0L, mf.getMostRecentTimestamp());

        append(mf, 123L, "", new byte[0]);
        assertEquals(123L, mf.getMostRecentTimestamp());

        mf.close();

        mf = new MessageFile(file, 1000);
        assertEquals(123L, mf.getMostRecentTimestamp());
        mf.close();
    }

    @Test
    public void testMessageCount() throws IOException {
        File file = new File(dir, "message-count");
        file.delete();

        int maxBuckets = 255;

        // file is big enough for 2 * 255 messages each 100 bytes so there will be 2 per bucket
        MessageFile mf = new MessageFile(file, 1000, 4096 + 2 * maxBuckets * (100 + 15));
        assertEquals(0, mf.getMessageCount());

        byte[] msg = new byte[100];
        append(mf, 10L, "", msg);
        assertEquals(1, mf.getMessageCount());
        append(mf, 20L, "", msg);
        assertEquals(2, mf.getMessageCount());
        append(mf, 30L, "", msg);
        assertEquals(3, mf.getMessageCount());  // new bucket
        mf.close();

        // check count works on existing file
        mf = new MessageFile(file, 1000, 4096 + 2 * maxBuckets * (100 + 15));
        assertEquals(3, mf.getMessageCount());
        mf.close();
    }

    @Test
    public void testPerformance() throws IOException {
        if (System.getProperty("perf") == null) {
            System.out.println("Skipping testPerformance, run with -Dperf=true to enable");
            return;
        }

        File file = new File(dir, "performance.qdb");
        file.delete();
        MessageFile mf = new MessageFile(file, 0, 2100000000);

        Random rnd = new Random(123);
        byte[] msg = new byte[4096];
        rnd.nextBytes(msg);

        int numMessages = 1000000;
        long start = System.currentTimeMillis();
        for (int i = 0; i < numMessages; i++) {
            int sz = rnd.nextInt(msg.length);
            append(mf, System.currentTimeMillis(), "msg" + i, Arrays.copyOf(msg, sz));
        }
        mf.checkpoint(false);
        mf.close();

        int ms = (int)(System.currentTimeMillis() - start);
        double perSec = numMessages / (ms / 1000.0);
        System.out.println("Write " + numMessages + " in " + ms + " ms, " + perSec + " messages per second");

        mf = new MessageFile(file, 0);
        start = System.currentTimeMillis();
        int c = 0;
        for (MessageCursor i = mf.cursor(0); i.next(); c++) {
            i.getId();
            i.getTimestamp();
            i.getRoutingKey();
            i.getPayload();
        }

        ms = (int)(System.currentTimeMillis() - start);
        perSec = c / (ms / 1000.0);
        System.out.println("Read " + c + " in " + ms + " ms, " + perSec + " messages per second");
    }

}