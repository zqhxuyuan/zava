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

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

/**
 * <p>Stores messages on the file system in multiple files in a directory. Thread safe. The files are named
 * [id of first message in hex, 16 digits]-[timestamp of first message in hex, 16 digits].qdb so that they sort
 * in message id order.</p>
 */
public class PersistentMessageBuffer implements MessageBuffer {

    private final File dir;

    private long maxSize = 100 * 1000 * 1000000L; // 100 GB
    private int segmentCount = 1000;
    private int segmentLength;                // auto
    private int maxPayloadSize = 128 * 1024;  // auto

    private long[] files;           // first message ID stored in each file (from filename)
    private long[] timestamps;      // timestamp of first message stored in each file (from filename)
    private int[] counts;           // number of messages stored in each file (from filename)
    private int fileOffset;         // actual indexing into files, timestamps and counts must always add this offset
    private int firstFile;          // index of first entry in files in use (relative to fileOffset)
    private int lastFile;           // index of last entry in files in use + 1 (relative to fileOffset)

    private MessageFile current;    // file we are currently appending to
    private int lastFileLength;     // only used if current is null

    private long mostRecentTimestamp;

    private Cursor[] waitingCursors = new Cursor[1];

    private Executor executor;
    private Runnable cleanupJob;

    private int autoSyncIntervalMs = 1000;
    private Timer timer;
    private SyncTimerTask syncTask;
    private boolean open;

    private final long creationTime = System.currentTimeMillis();

    private static final FilenameFilter QDB_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".qdb");
        }
    };

    /**
     * Dir will be created if it does not exist. It must be writeable.
     */
    public PersistentMessageBuffer(File dir) throws IOException {
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Directory [" + dir + "] does not exist and could not be created");
            }
        }
        if (!dir.isDirectory()) {
            throw new IOException("Not a directory [" + dir + "]");
        }
        if (!dir.canWrite()) {
            throw new IOException("Not writeable [" + dir + "]");
        }
        this.dir = dir;

        // build our master index from the names of the files in dir
        String[] list = dir.list(QDB_FILTER);
        if (list == null) {
            throw new IOException("Unable to list files in [" + dir + "]");
        }
        Arrays.sort(list);
        int n = list.length;
        int len = ((n / 512) + 1) * 512;
        files = new long[len];
        timestamps = new long[len];
        counts = new int[len];
        if (n > 0) {
            for (int i = 0; i < n; i++) {
                String name = list[i];
                if (name.length() < 39) {
                    throw new IOException("File [" + dir + "/" + list[i] + "] has invalid name");
                }
                try {
                    files[i] = Long.parseLong(name.substring(0, 16), 16);
                    timestamps[i] = Long.parseLong(name.substring(17, 33), 16);
                    counts[i] = Integer.parseInt(name.substring(34, name.lastIndexOf('.')));
                } catch (NumberFormatException e) {
                    throw new IOException("File [" + dir + "/" + list[i] + "] has invalid name");
                }
            }
            lastFile = n;
            lastFileLength = (int)getFile(lastFile - 1).length();
        }

        open = true;
    }

    @Override
    public synchronized void setFirstId(long firstMessageId) throws IOException {
        checkOpen();
        int c = lastFile - firstFile;
        if (c != 0) throw new IllegalStateException("Buffer is not empty");
        files[0] = firstMessageId;
    }

    @Override
    public void setMaxSize(long bytes) throws IOException {
        if (this.maxSize == bytes) return;
        if (bytes <= 0) throw new IllegalArgumentException("Invalid maxSize " + bytes);
        this.maxSize = bytes;
        if (executor != null) {
            executor.execute(cleanupJob);
        } else {
            cleanup();
        }
    }

    @Override
    public long getMaxSize() {
        return maxSize;
    }

    /**
     * How many message segments should the buffer contain when it is full? Note that this value may be ignored
     * depending on {@link #setMaxPayloadSize(int)} and {@link #setSegmentLength(int)}.
     */
    public void setSegmentCount(int segmentCount) {
        if (segmentCount <= 0) throw new IllegalArgumentException("Invalid segmentCount " + segmentCount);
        this.segmentCount = segmentCount;
    }

    public int getSegmentCount() {
        return segmentCount;
    }

    @Override
    public void setMaxPayloadSize(int maxPayloadSize) {
        if (maxPayloadSize < 0 || maxPayloadSize >= 1000 * 1000000 /*1G*/) {
            throw new IllegalArgumentException("maxPayloadLength out of range: " + maxPayloadSize);
        }
        this.maxPayloadSize = maxPayloadSize;
    }

    @Override
    public int getMaxPayloadSize() {
        if (maxPayloadSize > 0) return maxPayloadSize;
        return getSegmentLength() - 2048;
    }

    /**
     * How big are the individual message segments? Smaller segments provide more granular timeline data but limit
     * the maximum message size and may impact performance. Use a segment size of 0 for automatic sizing based
     * on {@link #getMaxSize()}, {@link #getSegmentCount()} and {@link #getMaxPayloadSize()}.
     */
    public void setSegmentLength(int segmentLength) {
        this.segmentLength = segmentLength;
    }

    public int getSegmentLength() {
        if (segmentLength > 0) return segmentLength;
        int ans = (int)Math.min(maxSize / segmentCount, 1000 * 1000000L /*1G*/);
        ans = Math.max(ans, maxPayloadSize + 8192);
        return ans;
    }

    @Override
    public void setExecutor(Executor executor) {
        this.executor = executor;
        if (cleanupJob == null) {
            cleanupJob = new Runnable() {
                @Override
                public void run() {
                    try {
                        cleanup();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
    }

    @Override
    public synchronized boolean isEmpty() throws IOException {
        checkOpen();
        return lastFile - firstFile == 0;
    }

    @Override
    public synchronized long getSize() throws IOException {
        checkOpen();
        int c = lastFile - firstFile;
        if (c == 0) return 0L;
        return  (c - 1) * MessageFile.FILE_HEADER_SIZE
                + files[lastFile - 1 - fileOffset] - files[firstFile - fileOffset]
                + (current == null ? lastFileLength : current.length());
    }

    /**
     * How many message files does this buffer have?
     */
    public synchronized int getFileCount() {
        return lastFile - firstFile;
    }

    @Override
    public long append(long timestamp, String routingKey, byte[] payload) throws IOException {
        return append(timestamp, routingKey, Channels.newChannel(new ByteArrayInputStream(payload)), payload.length);
    }

    @SuppressWarnings({"ConstantConditions", "SynchronizationOnLocalVariableOrMethodParameter"})
    @Override
    public long append(long timestamp, String routingKey, ReadableByteChannel payload, int payloadSize)
            throws IOException {
        long id;
        Cursor[] copyOfWaitingCursors;

        if (routingKey == null) routingKey = "";

        synchronized (this) {
            checkOpen();

            int maxLen = getMaxPayloadSize();
            if (payloadSize > maxLen) {
                throw new IllegalArgumentException("Payload size of " + payloadSize + " exceeds max payload size of " +
                        maxLen);
            }

            if (current == null) {
                if (lastFile == 0) {    // new buffer
                    current = new MessageFile(toFile(files[0], timestamps[0] = timestamp, 0), files[0], getSegmentLength());
                    ++lastFile;
                } else {
                    ensureCurrent();
                }
            }
            id = current.append(timestamp, routingKey, payload, payloadSize);
            if (id < 0) {
                ensureSpaceInFiles();

                // rename current to match number of messages it contains
                int count = current.getMessageCount();
                File newName = toFile(files[lastFile - 1 - fileOffset], timestamps[lastFile - 1 - fileOffset], count);
                if (!current.getFile().renameTo(newName)) {
                    throw new IOException("Unable to rename [" + current.getFile().getAbsolutePath() + "] to [" +
                            newName.getAbsolutePath() + "]");
                }
                counts[lastFile - 1 - fileOffset] = count;

                long firstMessageId = current.getNextMessageId();
                current.closeIfUnused();

                current = new MessageFile(toFile(firstMessageId, timestamp, 0), firstMessageId, getSegmentLength());
                timestamps[lastFile - fileOffset] = timestamp;
                files[lastFile++ - fileOffset] = firstMessageId;
                id = current.append(timestamp, routingKey, payload, payloadSize);
                if (id < 0) {   // this shouldn't happen
                    throw new IllegalArgumentException("Message is too long?");
                }
                mostRecentTimestamp = timestamp;

                if (executor != null) {
                    executor.execute(cleanupJob);
                } else {
                    cleanup();
                }
            } else {
                mostRecentTimestamp = timestamp;
            }

            copyOfWaitingCursors = waitingCursors;
        }

        // Don't notify waiting cursors while we hold our own lock or we can get deadlock.
        // It doesnt matter if entries in the array are changed while we are notifying so just copying the ref is ok.
        for (Cursor c : copyOfWaitingCursors) {
            if (c != null) {
                synchronized (c) {
                    c.notifyAll();
                }
            }
        }

        if (autoSyncIntervalMs > 0) {
            synchronized (this) {
                if (timer == null) timer = new Timer("qdb-timer:" + dir, true);
                if (syncTask == null || syncTask.isDone()) {
                    timer.schedule(syncTask = new SyncTimerTask(), autoSyncIntervalMs);
                }
            }
        }

        return id;
    }

    private void ensureCurrent() throws IOException {
        if (current == null) {
            long firstMessageId = files[lastFile - 1 - fileOffset];
            current = new MessageFile(toFile(firstMessageId, timestamps[lastFile - 1 - fileOffset],
                    counts[lastFile - 1 - fileOffset]), firstMessageId);
        }
    }

    /**
     * Make sure there is space for one more entry in the files array.
     */
    private void ensureSpaceInFiles() {
        if (lastFile - fileOffset < files.length) return;
        int n = lastFile - firstFile;
        int n2 = n + 512;

        long[] a = new long[n2];
        System.arraycopy(files, firstFile - fileOffset, a, 0, n);
        files = a;

        a = new long[n2];
        System.arraycopy(timestamps, firstFile - fileOffset, a, 0, n);
        timestamps = a;

        int[] b = new int[n2];
        System.arraycopy(counts, firstFile - fileOffset, b, 0, n);
        counts = b;

        fileOffset = firstFile;
    }

    @Override
    public int getMessageSize(String routingKey, int payloadSize) {
        return MessageFile.getMessageSize(routingKey, payloadSize);
    }

    @Override
    public void setAutoSyncInterval(int ms) {
        this.autoSyncIntervalMs = ms;
    }

    @Override
    public int getAutoSyncInterval() {
        return autoSyncIntervalMs;
    }

    @Override
    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    /**
     * If this buffer is exceeding its maximum capacity then delete some of the the oldest files until it is under
     * the limit.
     */
    public void cleanup() throws IOException {
        for (;;) {
            File doomed;
            synchronized (this) {
                if (maxSize == 0 || getSize() <= maxSize || firstFile >= lastFile - 1) return;
                doomed = getFile(firstFile);
                ++firstFile;
                // todo what about cursors that might have doomed open?
            }
            if (!doomed.delete()) {
                throw new IOException("Unable to delete [" + doomed + "]");
            }
        }
    }

    @Override
    public synchronized void sync() throws IOException {
        if (current != null) {
            current.checkpoint(true);
        }
    }

    private static final char[] ZERO_CHARS = "0000000000000000".toCharArray();

    private File toFile(long firstMessageId, long timestamp, int count) {
        StringBuilder b = new StringBuilder();
        String name = Long.toHexString(firstMessageId);
        b.append(ZERO_CHARS, 0, ZERO_CHARS.length - name.length()).append(name).append('-');
        name = Long.toHexString(timestamp);
        b.append(ZERO_CHARS, 0, ZERO_CHARS.length - name.length()).append(name).append("-");
        b.append(count).append(".qdb");
        return new File(dir, b.toString());
    }

    private File getFile(int i) {
        if (i < firstFile || i >= lastFile) {
            throw new IllegalArgumentException("Index " + i + " out of range (" + firstFile + " to " + (lastFile - 1) + ")");
        }
        return toFile(files[i - fileOffset], timestamps[i - fileOffset], counts[i - fileOffset]);
    }

    private void checkOpen() throws IOException {
        if (!isOpen()) throw new IOException(this + " has been closed");
    }

    @Override
    public synchronized boolean isOpen() {
        return open;
    }

    @Override
    public void close() throws IOException {
        Cursor[] copyOfWaitingCursors;
        synchronized (this) {
            if (!isOpen()) return;
            if (syncTask != null) {
                syncTask.cancel();
                syncTask = null;
            }
            if (current != null) {
                current.close();
                current = null;
            }
            open = false;
            copyOfWaitingCursors = waitingCursors;
        }

        // interrupt threads waiting for messages
        for (Cursor c : copyOfWaitingCursors) {
            if (c != null) c.interrupt();
        }
    }

    @Override
    public synchronized long getNextId() throws IOException {
        checkOpen();
        if (lastFile == 0) return files[0];  // empty buffer
        ensureCurrent();
        return current.getNextMessageId();
    }

    @Override
    public synchronized long getMessageCount() throws IOException {
        checkOpen();
        int n = lastFile - firstFile;
        if (n == 0) return 0L;    // buffer is empty
        ensureCurrent();
        long ans = current.getMessageCount();
        for (int i = firstFile; i < lastFile - 1; i++) ans += counts[i - fileOffset];
        return ans;
    }

    @Override
    public synchronized Date getOldestTimestamp() throws IOException {
        checkOpen();
        return lastFile == firstFile ? null : new Date(timestamps[firstFile - fileOffset]);
    }

    @Override
    public synchronized long getOldestId() throws IOException {
        checkOpen();
        return lastFile == firstFile ? files[fileOffset] : files[firstFile - fileOffset];
    }

    public synchronized Date getMostRecentTimestamp() throws IOException {
        checkOpen();
        int n = lastFile - firstFile;
        if (n == 0) return null;    // buffer is empty
        ensureCurrent();
        if (mostRecentTimestamp == 0) mostRecentTimestamp = current.getMostRecentTimestamp();
        return new Date(mostRecentTimestamp);
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public synchronized Timeline getTimeline() throws IOException {
        checkOpen();
        int n = lastFile - firstFile;
        if (n == 0) return null;    // buffer is empty
        TopTimeline ans = new TopTimeline(n + 1);
        System.arraycopy(this.files, firstFile - fileOffset, ans.files, 0, n);
        System.arraycopy(this.timestamps, firstFile - fileOffset, ans.timestamps, 0, n);
        System.arraycopy(this.counts, firstFile - fileOffset, ans.counts, 0, n - 1);
        ensureCurrent();
        ans.files[n] = current.getNextMessageId();
        long mrt = current.getMostRecentTimestamp();
        ans.timestamps[n] = mrt == 0 ? ans.timestamps[n - 1] : mrt;
        ans.counts[n - 1] = current.getMessageCount();
        return ans;
    }

    static class TopTimeline implements Timeline {

        private long[] files, timestamps;
        private int[] counts;

        TopTimeline(int n) {
            files = new long[n];
            timestamps = new long[n];
            counts = new int[n];
        }

        public int size() {
            return files.length;
        }

        public long getMessageId(int i) {
            return files[i];
        }

        public long getTimestamp(int i) {
            return timestamps[i];
        }

        public int getBytes(int i) {
            return i == files.length - 1 ? 0 : (int)(files[i + 1] - files[i]);
        }

        public long getMillis(int i) {
            return i == files.length - 1 ? 0L : timestamps[i + 1] - timestamps[i];
        }

        public int getCount(int i) {
            return counts[i];
        }
    }

    @Override
    public synchronized Timeline getTimeline(long messageId) throws IOException {
        int i = findFileIndex(messageId);
        if (i < 0) return null;
        MessageFile mf = getMessageFileForCursor(i);
        try {
            return mf.getTimeline();
        } finally {
            mf.closeIfUnused();
        }
    }

    @Override
    public String toString() {
        return "PersistentMessageBuffer[" + dir.getAbsolutePath() + "]";
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    @Override
    public synchronized MessageCursor cursor(long messageId) throws IOException {
        int i = findFileIndex(messageId);
        if (i < 0) return new EmptyCursor();
        MessageFile mf = getMessageFileForCursor(i);
        long first = mf.getFirstMessageId();
        if (messageId < first) messageId = first;
        return new Cursor(i, mf, mf.cursor(messageId));
    }

    private int findFileIndex(long messageId) throws IOException {
        if (messageId < 0) {
            throw new IllegalArgumentException("Invalid messageId " + messageId + ", " + this);
        }
        long next = getNextId();
        if (messageId > next) {
            throw new IllegalArgumentException("messageId " + messageId + " past end of buffer " + next + ", " + this);
        }

        int i;
        synchronized (this) {
            checkOpen();
            if (lastFile == firstFile) {
                return -1;
            }
            long firstMessageId = files[firstFile - fileOffset];
            if (messageId < firstMessageId) {
                messageId = firstMessageId;
            }

            i = Arrays.binarySearch(files, firstFile - fileOffset, lastFile - fileOffset, messageId);
            if (i < 0) {
                i = -(i + 2); // return position before the insertion index if we didn't get a match
            }
            i += fileOffset;
        }
        return i;
    }

    @Override
    public synchronized MessageCursor cursorByTimestamp(long timestamp) throws IOException {
        checkOpen();
        if (lastFile == firstFile) {
            return new EmptyCursor();
        }
        long firstTimestamp = timestamps[firstFile - fileOffset];
        if (timestamp < firstTimestamp) {
            timestamp = firstTimestamp;
        }

        int i = Arrays.binarySearch(timestamps, firstFile - fileOffset, lastFile - fileOffset, timestamp);
        if (i < 0) {
            i = -(i + 2); // return position before the insertion index if we didn't get a match
        }
        i += fileOffset;

        MessageFile mf = getMessageFileForCursor(i);
        return new Cursor(i, mf, mf.cursorByTimestamp(timestamp));
    }

    private synchronized MessageFile getMessageFileForCursor(int i) throws IOException {
        checkOpen();
        if (i == lastFile - 1 && current != null) {
            current.use();
            return current;
        } else if (i >= lastFile) {
            return null;
        }
        return new MessageFile(getFile(i), files[i - fileOffset]);
    }

    private synchronized void addWaitingCursor(Cursor c) {
        for (int i = waitingCursors.length - 1; i >= 0; i--) {
            if (waitingCursors[i] == null) {
                waitingCursors[i] = c;
                return;
            }
        }
        int n = waitingCursors.length;
        Cursor[] a = new Cursor[n * 2];
        System.arraycopy(waitingCursors, 0, a, 0, n);
        a[n] = c;
        waitingCursors = a;
    }

    private synchronized void removeWaitingCursor(Cursor c) {
        for (int i = waitingCursors.length - 1; i >= 0; i--) {
            if (waitingCursors[i] == c) {
                waitingCursors[i] = null;
                return;
            }
        }
    }

    private synchronized boolean isCurrentFile(int fileIndex) {
        return fileIndex == lastFile - 1;
    }

    private class Cursor implements MessageCursor {

        protected int fileIndex;
        protected MessageFile mf;
        protected MessageCursor c;
        protected Thread waitingThread;

        public Cursor(int fileIndex, MessageFile mf, MessageCursor c) {
            this.fileIndex = fileIndex;
            this.mf = mf;
            this.c = c;
        }

        @Override
        public synchronized boolean next() throws IOException {
            if (c == null) throw new IOException("Cursor has been closed");
            if (c.next()) return true;
            synchronized (PersistentMessageBuffer.this) {
                if (isCurrentFile(fileIndex)) return false;
                close();
                mf = getMessageFileForCursor(++fileIndex);
            }
            c = mf.cursor(mf.getFirstMessageId());
            return c.next();
        }

        public synchronized boolean next(int timeoutMs) throws IOException, InterruptedException {
            boolean haveNext = false;
            addWaitingCursor(this);
            try {
                waitingThread = Thread.currentThread();
                if (timeoutMs <= 0) {
                    while (!(haveNext = next())) {
                        wait(timeoutMs);
                    }
                } else {
                    while (!(haveNext = next()) && timeoutMs > 0) {
                        long start = System.currentTimeMillis();
                        wait(timeoutMs);
                        timeoutMs -= (int)(System.currentTimeMillis() - start);
                    }
                }
            } finally {
                removeWaitingCursor(this);
                waitingThread = null;
            }
            return haveNext;
        }

        public long getId() throws IOException {
            return c.getId();
        }

        public long getTimestamp() throws IOException {
            return c.getTimestamp();
        }

        public String getRoutingKey() throws IOException {
            return c.getRoutingKey();
        }

        public int getPayloadSize() throws IOException {
            return c.getPayloadSize();
        }

        public byte[] getPayload() throws IOException {
            return c.getPayload();
        }

        public long getNextId() throws IOException {
            return c.getNextId();
        }

        public synchronized void close() throws IOException {
            if (c != null) {
                c.close();
                c = null;
            }
            if (mf != null) {
                mf.closeIfUnused();
                mf = null;
            }
            notifyAll();    // causes threads blocked on next(int) to get a "cursor has been closed" IOException
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            close();
        }

        void interrupt() {
            Thread t = waitingThread;
            if (t != null) t.interrupt();
        }
    }

    /**
     * This implementation is used when the buffer is empty. It checks to see if the buffer is still empty on
     * each call to next and initializes the cursor when the buffer becomes not empty.
     */
    private class EmptyCursor extends Cursor {

        private boolean closed;

        private EmptyCursor() {
            super(-1, null, null);
        }

        @Override
        public boolean next() throws IOException {
            if (fileIndex < 0) {
                if (closed) throw new IOException("Cursor has been closed");
                mf = getMessageFileForCursor(0);
                if (mf == null) return false;
                fileIndex = 0;
                c = mf.cursor(mf.getFirstMessageId());
            }
            return super.next();
        }

        @Override
        public boolean next(int timeoutMs) throws IOException, InterruptedException {
            return super.next(timeoutMs);
        }

        @Override
        public synchronized void close() throws IOException {
            super.close();
            closed = true;
        }
    }

    private class SyncTimerTask extends TimerTask {

        private boolean done;

        public boolean isDone() {
            return done;
        }

        @Override
        public void run() {
            try {
                PersistentMessageBuffer.this.sync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                done = true;
            }
        }
    }
}
