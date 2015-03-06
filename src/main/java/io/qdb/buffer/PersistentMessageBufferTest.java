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
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

public class PersistentMessageBufferTest {

    private static File dir = new File("build/test-data");

    @BeforeClass
    public static void beforeClass() throws IOException {
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IOException("Unable to create [" + dir + "]");
        }
    }

    @Test
    public void testAppend() throws IOException {
        PersistentMessageBuffer b = new PersistentMessageBuffer(mkdir("append"));
        assertTrue(b.toString().contains("append"));
        assertTrue(b.getCreationTime() >= System.currentTimeMillis());
        b.setSegmentLength(10000 + MessageFile.FILE_HEADER_SIZE);
        assertEquals(0, b.getFileCount());
        assertEquals(0L, b.getSize());
        assertEquals(10000 + MessageFile.FILE_HEADER_SIZE, b.getSegmentLength());

        long ts = System.currentTimeMillis();
        assertEquals(0L, append(b, ts, "", 5000));
        assertEquals(5000L, append(b, ts, "", 5000));
        assertEquals(1, b.getFileCount());
        assertEquals(10000L + MessageFile.FILE_HEADER_SIZE, b.getSize());

        assertEquals(10000L, append(b, ts, "", 5000));
        assertEquals(2, b.getFileCount());
        assertEquals(15000L + MessageFile.FILE_HEADER_SIZE * 2, b.getSize());

        assertEquals(15000L, append(b, ts, "", 5000));
        assertEquals(2, b.getFileCount());
        assertEquals(20000L + MessageFile.FILE_HEADER_SIZE * 2, b.getSize());

        b.close();
    }

    @Test
    public void testFirstMessageId() throws IOException {
        File bd = mkdir("firstmsg");

        PersistentMessageBuffer b = new PersistentMessageBuffer(bd);
        b.setFirstId(0x1234);
        long ts = 0x5678;
        assertEquals(0x1234L, append(b, ts, "", 256));
        b.close();

        expect(bd.list(), "0000000000001234-0000000000005678-0.qdb");

        b = new PersistentMessageBuffer(bd);
        assertEquals(0x1334L, append(b, ts, "", 256));
        b.close();
    }

    @Test
    public void testOpenExisting() throws IOException {
        File bd = mkdir("open-existing");

        PersistentMessageBuffer b = new PersistentMessageBuffer(bd);
        b.setSegmentLength(8192 + MessageFile.FILE_HEADER_SIZE);
        long ts = 0x5678;
        append(b, ts, "", 4096);
        append(b, ts, "", 4096);
        b.close();

        expect(bd.list(), "0000000000000000-0000000000005678-0.qdb");

        b = new PersistentMessageBuffer(bd);
        b.setSegmentLength(8192 + MessageFile.FILE_HEADER_SIZE);
        ts = 0x9abc;
        append(b, ts, "", 4096);
        b.close();

        expect(bd.list(), "0000000000000000-0000000000005678-2.qdb", "0000000000002000-0000000000009abc-0.qdb");
    }

    @Test
    public void testNextMessageId() throws IOException {
        File bd = mkdir("nextmsg");

        PersistentMessageBuffer b = new PersistentMessageBuffer(bd);
        b.setFirstId(0x1234);
        assertEquals(0x1234L, b.getNextId());

        long ts = System.currentTimeMillis();
        append(b, ts, "", 256);
        assertEquals(0x1334L, b.getNextId());
        b.close();

        b = new PersistentMessageBuffer(bd);
        assertEquals(0x1334L, b.getNextId());
        b.close();
    }

    @Test
    public void testMoreThan512Files() throws IOException {
        File bd = mkdir("files512");

        PersistentMessageBuffer b = new PersistentMessageBuffer(bd);
        b.setSegmentLength(8192 + MessageFile.FILE_HEADER_SIZE);
        int ts = 0;
        int n = 513;
        String[] expect = new String[n];
        for (int i = 0; i < n; i++) {
            append(b, ++ts, "", 8192);
            expect[i] = "00000000" + String.format("%08x", i * 8192) + "-00000000" + String.format("%08x", ts) +
                    "-" + (i < n - 1 ? "1" : "0") + ".qdb";
        }
        b.close();

        expect(bd.list(), expect);
    }

    @Test
    public void testCursor() throws IOException {
        File bd = mkdir("cursor");
        Random rnd = new Random(123);

        PersistentMessageBuffer b = new PersistentMessageBuffer(bd);
        b.setFirstId(1000);
        b.setSegmentLength(8192 + MessageFile.FILE_HEADER_SIZE);

        MessageCursor c = b.cursor(0);
        assertFalse(c.next());

        Msg m0 = appendFixedSizeMsg(b, 100, 4096, rnd);
        assertNextMsg(m0, c);
        assertFalse(c.next());
        c.close();

        // cursor starting on an empty buffer is a special case so repeat the test with a 'normal' cursor
        c = b.cursor(0);
        assertNextMsg(m0, c);
        assertFalse(c.next());

        // this fills up the first file
        Msg m1 = appendFixedSizeMsg(b, 200, 4096, rnd);
        assertNextMsg(m1, c);
        assertFalse(c.next());

        // fill the 2nd file and start the 3rd
        Msg m2 = appendFixedSizeMsg(b, 300, 4096, rnd);
        Msg m3 = appendFixedSizeMsg(b, 400, 4096, rnd);
        Msg m4 = appendFixedSizeMsg(b, 500, 4096, rnd);

        // these messages are fetched from 2nd file (not current file)
        assertNextMsg(m2, c);
        assertNextMsg(m3, c);

        // this one comes from current
        assertNextMsg(m4, c);
        assertFalse(c.next());
        c.close();

        // now run 2 cursors together
        c = b.cursor(0);
        MessageCursor c2 = b.cursor(0);
        assertNextMsg(m0, c);
        assertNextMsg(m0, c2);
        c.close();
        c2.close();

        // check seeking by id works
        seekByIdCheck(b, m0);
        seekByIdCheck(b, m1);
        seekByIdCheck(b, m2);
        seekByIdCheck(b, m3);
        seekByIdCheck(b, m4);

        // check seeking by timestamp works
        seekByTimestampCheck(b, m0);
        seekByTimestampCheck(b, m1);
        seekByTimestampCheck(b, m2);
        seekByTimestampCheck(b, m3);
        seekByTimestampCheck(b, m4);

        b.close();

        // check seeking by timestamp works on newly opened buffer
        b = new PersistentMessageBuffer(bd);
        seekByTimestampCheck(b, m0);
        b.close();
    }

    @Test
    public void testCursorOnSingleFileBufferNPE() throws IOException {
        File bd = mkdir("cursor2");
        Random rnd = new Random(123);

        PersistentMessageBuffer b = new PersistentMessageBuffer(bd);
        b.setFirstId(1000);
        b.setSegmentLength(8192 + MessageFile.FILE_HEADER_SIZE);

        Msg m0 = appendFixedSizeMsg(b, 100, 4096, rnd);
        b.close();

        b = new PersistentMessageBuffer(bd);
        seekByTimestampCheck(b, m0);
        b.close();
    }

    @Test
    public void testCursorToEndOnSingleFileBufferNPE() throws IOException {
        File bd = mkdir("cursor3");
        Random rnd = new Random(123);

        PersistentMessageBuffer b = new PersistentMessageBuffer(bd);
        b.setFirstId(1000);
        b.setSegmentLength(8192 + MessageFile.FILE_HEADER_SIZE);
        appendFixedSizeMsg(b, 100, 4096, rnd);
        b.close();

        b = new PersistentMessageBuffer(bd);
        MessageCursor c = b.cursorByTimestamp(0);
        assertTrue(c.next());
        assertFalse(c.next());
        c.close();
        b.close();
    }

    private void seekByIdCheck(PersistentMessageBuffer b, Msg m) throws IOException {
        MessageCursor c = b.cursor(m.id);
        assertNextMsg(m, c);
        c.close();
        c = b.cursor(m.id - 1);
        assertNextMsg(m, c);
        c.close();
    }

    private void seekByTimestampCheck(PersistentMessageBuffer b, Msg m) throws IOException {
        MessageCursor c = b.cursorByTimestamp(m.timestamp);
        assertNextMsg(m, c);
        c.close();
        c = b.cursorByTimestamp(m.timestamp - 99);
        assertNextMsg(m, c);
        c.close();
    }

    private void assertNextMsg(Msg msg, MessageCursor c) throws IOException {
        assertTrue(c.next());
        assertEquals(msg.id, c.getId());
        assertEquals(msg.timestamp, c.getTimestamp());
        assertEquals(msg.routingKey, c.getRoutingKey());
        assertEquals(msg.payload.length, c.getPayloadSize());
        assertArrayEquals(msg.payload, c.getPayload());
    }

    private Msg appendFixedSizeMsg(PersistentMessageBuffer b, long ts, int totalSize, Random rnd) throws IOException {
        String key = "key" + ts;
        byte[] payload = new byte[totalSize - 15 - key.length()];
        rnd.nextBytes(payload);
        Msg msg = new Msg(ts, key, payload);
        msg.id = b.append(msg.timestamp, msg.routingKey, msg.payload);
        return msg;
    }

    private void expect(String[] actual, String... expected) {
        Arrays.sort(actual);
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals("[" + i + "]", expected[i], actual[i]);
        }
    }

    private long append(PersistentMessageBuffer b, long timestamp, String key, int len) throws IOException {
        byte[] payload = new byte[len - 15 - key.length()];
        return b.append(timestamp, key, payload);
    }

    private static class CursorThread extends Thread {

        MessageCursor c;
        int timeoutMs;
        CountDownLatch startSignal = new CountDownLatch(1);
        long startTime;
        boolean gotMessage;
        Throwable exception;
        int waitingMs;
        CountDownLatch doneSignal = new CountDownLatch(1);

        CursorThread(MessageBuffer b, int timeoutMs) throws IOException {
            this.c = b.cursor(0);
            this.timeoutMs = timeoutMs;
            start();
        }

        @Override
        public void run() {
            try {
                startSignal.await();
                startTime = System.currentTimeMillis();
                gotMessage = c.next(timeoutMs);
                c.close();
            } catch (Throwable e) {
                exception = e;
            } finally {
                waitingMs = (int)(System.currentTimeMillis() - startTime);
                doneSignal.countDown();
            }
        }
    }

    @Test
    public void testCursorBlocking() throws Exception {
        File bd = mkdir("cursor-blocking");

        PersistentMessageBuffer b = new PersistentMessageBuffer(bd);
        b.setFirstId(1000);

        // thread waits for 100 ms and finishes without getting a message
        CursorThread t = new CursorThread(b, 100);
        t.startSignal.countDown();
        boolean done = t.doneSignal.await(120, TimeUnit.MILLISECONDS);
        assertTrue(done);
        assertFalse(t.gotMessage);
        assertNull(t.exception);
        assertEquals(100, t.waitingMs, 20.0);

        // thread waits for 50 ms and gets interrupted
        t = new CursorThread(b, 100);
        t.startSignal.countDown();
        Thread.sleep(50);
        t.interrupt();
        done = t.doneSignal.await(10, TimeUnit.MILLISECONDS);
        assertTrue(done);
        assertFalse(t.gotMessage);
        assertTrue(t.exception instanceof InterruptedException);

        // thread waits forever and gets interrupted
        t = new CursorThread(b, 0);
        t.startSignal.countDown();
        Thread.sleep(50);
        t.interrupt();
        done = t.doneSignal.await(10, TimeUnit.MILLISECONDS);
        assertTrue(done);
        assertFalse(t.gotMessage);
        assertTrue(t.exception instanceof InterruptedException);
        assertEquals(50, t.waitingMs, 20.0);

        // thread waits for 50 ms and its cursor is closed so it gets interrupted
        t = new CursorThread(b, 100);
        t.startSignal.countDown();
        Thread.sleep(50);
        t.c.close();
        done = t.doneSignal.await(10, TimeUnit.MILLISECONDS);
        assertTrue(done);
        assertFalse(t.gotMessage);
        assertTrue(t.exception instanceof IOException);

        // 2 threads gets message after about 50 ms
        t = new CursorThread(b, 100);
        CursorThread t2 = new CursorThread(b, 100);
        t.startSignal.countDown();
        t2.startSignal.countDown();
        Thread.sleep(50);
        b.append(123L, "", new byte[0]);    // this should immediately wake up both cursor thread2
        done = t.doneSignal.await(100, TimeUnit.MILLISECONDS);
        assertTrue(done);
        done = t2.doneSignal.await(100, TimeUnit.MILLISECONDS);
        assertTrue(done);
        assertTrue(t.gotMessage);
        assertTrue(t2.gotMessage);
        assertNull(t.exception);
        assertNull(t2.exception);
        assertEquals(50, t.waitingMs, 50.0);
        assertEquals(50, t2.waitingMs, 50.0);

        // now that there is something in the buffer the thread gets message immediately without having to wait
        t = new CursorThread(b, 100);
        t.startSignal.countDown();
        done = t.doneSignal.await(10, TimeUnit.MILLISECONDS);
        assertTrue(done);
        assertTrue(t.gotMessage);
        assertNull(t.exception);
        assertTrue(t.waitingMs < 10);

        b.close();
    }

    @SuppressWarnings("ConstantConditions")
    private File mkdir(String name) throws IOException {
        File f = new File(dir, name);
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                if (!file.delete()) throw new IOException("Unable to delete [" + file + "]");
            }
        }
        return f;
    }

    @Test
    public void testCleanup() throws IOException {
        File bd = mkdir("cleanup");

        PersistentMessageBuffer b = new PersistentMessageBuffer(bd);
        b.setSegmentLength(8192 + MessageFile.FILE_HEADER_SIZE);
        append(b, 0, "", 8192);
        append(b, 0, "", 8192);
        append(b, 0, "", 8192);
        append(b, 0, "", 8192);
        expect(bd.list(),
                "0000000000000000-0000000000000000-1.qdb", "0000000000002000-0000000000000000-1.qdb",
                "0000000000004000-0000000000000000-1.qdb", "0000000000006000-0000000000000000-0.qdb");

        b.setMaxSize((8192 + MessageFile.FILE_HEADER_SIZE) * 2);
        b.cleanup();
        assertEquals(0x4000, b.getOldestId());
        expect(bd.list(), "0000000000004000-0000000000000000-1.qdb", "0000000000006000-0000000000000000-0.qdb");

        b.setMaxSize(1);  // can't get rid of last file
        b.cleanup();
        expect(bd.list(), "0000000000006000-0000000000000000-0.qdb");

        b.close();
    }

    @Test
    public void testAutoCleanup() throws IOException {
        File bd = mkdir("auto-cleanup");

        PersistentMessageBuffer b = new PersistentMessageBuffer(bd);
        b.setSegmentLength(8192 + MessageFile.FILE_HEADER_SIZE);
        int maxBufferSize = (8192 + MessageFile.FILE_HEADER_SIZE) * 3;
        b.setMaxSize(maxBufferSize);
        assertEquals(maxBufferSize, b.getMaxSize());
        append(b, 0, "", 8192);
        append(b, 0, "", 8192);
        append(b, 0, "", 8192);
        append(b, 0, "", 8192);
        expect(bd.list(),
                "0000000000002000-0000000000000000-1.qdb",
                "0000000000004000-0000000000000000-1.qdb", "0000000000006000-0000000000000000-0.qdb");

        CountingExecutor exec = new CountingExecutor();
        b.setExecutor(exec);
        append(b, 0, "", 8192);
        assertEquals(1, exec.count);

        b.close();
    }

    private class CountingExecutor implements Executor {
        int count;

        @Override
        public void execute(Runnable command) {
            ++count;
            command.run();
        }
    }

    @Test
    public void testSync() throws IOException {
        File bd = mkdir("sync");
        File first = new File(bd, "0000000000000000-0000000000000000-0.qdb");

        PersistentMessageBuffer b = new PersistentMessageBuffer(bd);
        append(b, 0, "", 8192);
        assertEquals(4096, getStoredLength(first));     // just the file header
        b.sync();
        assertEquals(4096 + 8192, getStoredLength(first));
        b.close();
    }

    @Test
    public void testTimeline() throws IOException {
        File bd = mkdir("timeline");

        PersistentMessageBuffer b = new PersistentMessageBuffer(bd);
        b.setFirstId(1000);
        b.setSegmentLength(8192 + MessageFile.FILE_HEADER_SIZE);
        assertTrue(b.isEmpty());
        assertNull(b.getTimeline());
        assertNull(b.getMostRecentTimestamp());
        assertEquals(0L, b.getMessageCount());
        assertNull(b.getOldestTimestamp());
        assertEquals(1000L, b.getOldestId());

        long ts = 200000;
        append(b, ts + 0, "", 8192);
        assertFalse(b.isEmpty());
        assertEquals(1L, b.getMessageCount());
        assertEquals(ts, b.getOldestTimestamp().getTime());
        assertEquals(ts, b.getMostRecentTimestamp().getTime());
        assertEquals(1000L, b.getOldestId());

        Timeline t = b.getTimeline();
        assertEquals(2, t.size());
        checkTimeline(t, 0, 1000, ts, 8192, 1, 0);
        checkTimeline(t, 1, 9192, ts, 0, 0, 0);

        // repeat test on newly opened buffer
        b.close();
        b = new PersistentMessageBuffer(bd);
        b.setSegmentLength(8192 + MessageFile.FILE_HEADER_SIZE);
        assertEquals(1L, b.getMessageCount());
        assertEquals(ts, b.getOldestTimestamp().getTime());
        assertEquals(ts, b.getMostRecentTimestamp().getTime());
        assertEquals(1000L, b.getOldestId());
        t = b.getTimeline();
        assertEquals(2, t.size());
        checkTimeline(t, 0, 1000, ts, 8192, 1, 0);
        checkTimeline(t, 1, 9192, ts, 0, 0, 0);

        append(b, ts + 2000, "", 2048);
        append(b, ts + 3000, "", 2048);
        append(b, ts + 4000, "", 2048);
        append(b, ts + 5000, "", 2048);
        assertEquals(5L, b.getMessageCount());
        assertEquals(ts, b.getOldestTimestamp().getTime());
        assertEquals(ts + 5000, b.getMostRecentTimestamp().getTime());
        t = b.getTimeline();
        assertEquals(3, t.size());
        checkTimeline(t, 0, 1000,  ts,          8192, 1, 2000);
        checkTimeline(t, 1, 9192,  ts + 2000,   8192, 4, 3000);
        checkTimeline(t, 2, 17384, ts + 5000,   0,    0, 0);

        t = b.getTimeline(9192);
        assertEquals(4, t.size());
        checkTimeline(t, 0, 9192,          ts + 2000,   2048, 1, 1000);
        checkTimeline(t, 1, 9192 + 2048,   ts + 3000,   2048, 1, 1000);
        checkTimeline(t, 2, 9192 + 2048*2, ts + 4000,   2048, 1, 1000);
        checkTimeline(t, 3, 9192 + 2048*3, ts + 5000,   2048, 1, 0);

        b.close();
    }

    private void checkTimeline(Timeline t, int i, long messageId, long ts, int bytes, int count, long millis) {
        assertEquals(messageId, t.getMessageId(i));
        assertEquals(ts, t.getTimestamp(i));
        assertEquals(bytes, t.getBytes(i));
        assertEquals(count, t.getCount(i));
        assertEquals(millis, t.getMillis(i));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private int getStoredLength(File file) throws IOException {
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        try {
            in.skip(8); // length is at position 8 in the file
            return in.readInt();
        } finally {
            in.close();
        }
    }

    @Test
    public void testShutdownHook() throws IOException {
        File bd = mkdir("shutdown-hook");
        new PersistentMessageBuffer(bd);
        // don't close the buffer to exercise the close code in ShutdownHook - have to look in the coverage
        // report to see that it ran
    }

    @Test
    public void testClose() throws IOException {
        File bd = mkdir("close");
        PersistentMessageBuffer mb = new PersistentMessageBuffer(bd);
        assertTrue(mb.isOpen());
        mb.close();
        mb.close();  // already closed is NOP
        assertFalse(mb.isOpen());
        try {
            mb.append(0L, "", new byte[0]);
            fail();
        } catch (IOException e) {
            // good
        }
    }

}