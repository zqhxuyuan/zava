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
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * <p>A bunch of messages all in the same file. Supports fast seek to a message by timestamp and detection and
 * recovery from corruption due to server crash. New messages are always appended to the end of the file for
 * performance. Thread safe.</p>
 *
 * <p>The file header is 4096 bytes long. The fixed part is 16 bytes and has the following format
 * (all BIG_ENDIAN):</p>
 * <pre>
 * magic: 2 bytes (currently 0xBE01)
 * reserved: 2 bytes (currently 0x0000)
 * max file size: 4 bytes
 * length of file at last checkpoint: 4 bytes
 * reserved: 4 bytes (currently 0x00000000)
 * </pre>
 *
 * <p>Recovery from a crash is simply a matter of truncating the file to its last checkpoint length. That might
 * discard some good messages but has the advantage of being very fast (compared to calculating and checking
 * message CRC values for example). The assumption is that if the messages are very important they will be
 * written to multiple machines.</p>
 *
 * <p>The rest of the file header consists of up to 255 histogram buckets for fast message lookup by timestamp
 * and id:</p>
 * <pre>
 * first message id (relative to this file): 4 bytes
 * timestamp: 8 bytes
 * message count: 4 bytes
 * </pre>
 *
 * <p>The histogram is updated at each checkpoint. Checkpoints are done manually or automatically every max file
 * size / 255 bytes.</p>
 *
 * <p>The remainder of the file consists of records in the following format (all BIG_ENDIAN):</p>
 *
 * <pre>
 * record type: 1 byte (value always 0xA1 currently)
 * timestamp: 8 bytes
 * routing key size in bytes (m): 2 bytes
 * payload size (n): 4 bytes
 * routing key UTF8 encoded: m bytes
 * payload: n bytes
 * </pre>
 */
class MessageFile implements Closeable {

    private final File file;
    private final long firstMessageId;
    private final int maxFileSize;
    private final RandomAccessFile raf;
    private final FileChannel channel;
    private final ByteBuffer fileHeader;
    private final ByteBuffer header;
    private int usageCounter = 1;

    private int length;
    private int lastCheckpointLength;
    private long mostRecentTimestamp;

    private final int bytesPerBucket;
    private int bucketIndex;
    private long bucketTimestamp;
    private int bucketMessageId;
    private int bucketCount;

    public static final int FILE_HEADER_SIZE = 4096;
    private static final int FILE_HEADER_FIXED_SIZE = 16;
    private static final int BUCKET_RECORD_SIZE = 16;
    private static final int MAX_BUCKETS = (FILE_HEADER_SIZE - FILE_HEADER_FIXED_SIZE) / BUCKET_RECORD_SIZE;

    private static final short FILE_MAGIC = (short)0xBE01;

    private static final byte TYPE_MESSAGE = (byte)0xA1;

    private static final int MESSAGE_HEADER_SIZE = 1 + 8 + 2 + 4;

    private static final Charset UTF8 = Charset.forName("UTF8");

    /**
     * Open an existing file.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public MessageFile(File file, long firstMessageId) throws IOException {
        this(file, firstMessageId, -1);
    }

    /**
     * Open a new or existing file. The maxFileSize parameter is only used when creating a new file.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public MessageFile(File file, long firstMessageId, int maxFileSize) throws IOException {
        this.file = file;
        this.firstMessageId = firstMessageId;

        if (maxFileSize < 0 && !file.isFile()) {
            throw new IllegalArgumentException("File does not exist, is not readable or is not a file [" + file + "]");
        }

        raf = new RandomAccessFile(file, "rw");
        channel = raf.getChannel();
        fileHeader = ByteBuffer.allocateDirect(FILE_HEADER_SIZE);
        header = ByteBuffer.allocateDirect(1024);

        int size = (int)channel.size();
        if (size == 0) {
            if (maxFileSize < FILE_HEADER_SIZE) {
                throw new IllegalArgumentException("Invalid max file size " + maxFileSize);
            }
            fileHeader.putShort(FILE_MAGIC);
            fileHeader.putShort((short)0);
            fileHeader.putInt(this.maxFileSize = maxFileSize);
            fileHeader.putInt(length = FILE_HEADER_SIZE);
            for (int i = bucketPosition(0); i < FILE_HEADER_SIZE; i += 16) fileHeader.putInt(i, -1);
            fileHeader.position(0);
            channel.write(fileHeader);
            channel.force(false);       // make sure file always has a valid header
            bucketIndex = -1;
        } else {
            int sz = channel.read(fileHeader);
            if (sz < FILE_HEADER_SIZE) throw new IOException("File header too short [" + file + "]");
            fileHeader.flip();
            short magic = fileHeader.getShort();
            if (magic != FILE_MAGIC) {
                throw new IOException("Invalid file magic 0x" + Integer.toHexString(magic & 0xFFFF) + " [" + file + "]");
            }
            fileHeader.position(fileHeader.position() + 2);
            this.maxFileSize = fileHeader.getInt();
            if (this.maxFileSize < FILE_HEADER_SIZE) {
                throw new IOException("Invalid max file size " + this.maxFileSize + " [" + file + "]");
            }
            length = fileHeader.getInt();
            if (length > size) {
                throw new IOException("Checkpoint " + length + " exceeds file size " + size + " [" + file + "]");
            } else if (length < size) {
                channel.truncate(length);   // discard possibly corrupt portion
            }
            lastCheckpointLength = length;

            for (bucketIndex = 0; bucketIndex < MAX_BUCKETS && fileHeader.getInt(bucketPosition(bucketIndex)) != -1; bucketIndex++);

            fileHeader.position(bucketPosition(--bucketIndex));
            bucketMessageId = fileHeader.getInt();
            bucketTimestamp = fileHeader.getLong();
            bucketCount = fileHeader.getInt();
        }

        bytesPerBucket = (this.maxFileSize - FILE_HEADER_SIZE) / MAX_BUCKETS;
    }

    private int bucketPosition(int i) {
        return FILE_HEADER_FIXED_SIZE + i * BUCKET_RECORD_SIZE;
    }

    public File getFile() {
        return file;
    }

    public long getFirstMessageId() {
        return firstMessageId;
    }

    /**
     * What ID will the next message appended have, assuming there is space for it?
     */
    public long getNextMessageId() {
        synchronized (channel) {
            return firstMessageId + length - FILE_HEADER_SIZE;
        }
    }

    /**
     * Append a message and return its id (position in the file plus the firstMessageId of the file). Returns
     * -1 if this file is too full for the message.
     */
    public long append(long timestamp, String routingKey, ReadableByteChannel payload, int payloadSize) throws IOException {
        int n = routingKey.length();
        if (n > 255) throw new IllegalArgumentException("Routing key length " + n + " > 255 characters");

        byte[] routingKeyBytes = routingKey.getBytes(UTF8);

        synchronized (channel) {
            if (length + MESSAGE_HEADER_SIZE + routingKeyBytes.length + payloadSize > maxFileSize) return -1;

            header.clear();
            channel.position(length);
            header.put(TYPE_MESSAGE);
            header.putLong(timestamp);
            header.putShort((short)routingKeyBytes.length);
            header.putInt(payloadSize);
            header.put(routingKeyBytes);
            header.flip();

            int id = length - FILE_HEADER_SIZE;
            channel.write(header);
            long sz = channel.transferFrom(payload, channel.position(), payloadSize);
            if (sz != payloadSize) {
                throw new IOException("Only read " + sz + " bytes from payload channel instead of " + payloadSize);
            }
            length = (int)channel.position() + payloadSize; // update after write so a partial write won't corrupt file

            // see if we need to start a new histogram bucket
            if (bucketIndex < 0 || ((id - bucketMessageId >= bytesPerBucket) && bucketIndex < MAX_BUCKETS - 1)) {
                if (bucketIndex >= 0) {
                    putBucketDataInFileHeader();
                    ++bucketIndex;
                } else {
                    bucketIndex = 0;
                }
                bucketMessageId = id;
                bucketTimestamp = timestamp;
                bucketCount = 1;
            } else {
                ++bucketCount;
            }

            mostRecentTimestamp = timestamp;
            return firstMessageId + id;
        }
    }

    private void putBucketDataInFileHeader() {
        fileHeader.position(bucketPosition(bucketIndex));
        fileHeader.putInt(bucketMessageId);
        fileHeader.putLong(bucketTimestamp);
        fileHeader.putInt(bucketCount);
        // data will be written at the next checkpoint
    }

    /**
     * How many bytes will the message take up in the file including headers?
     */
    public static int getMessageSize(String routingKey, int payloadSize) {
        try {
            return (routingKey == null ? 0 : routingKey.getBytes("UTF8").length) + payloadSize + MESSAGE_HEADER_SIZE;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);  // not possible really
        }
    }

    /**
     * How big is this file in bytes? Note that this is the total length of the file including the header.
     */
    public int length() {
        synchronized (channel) {
            return length;
        }
    }

    /**
     * Sync all changes to disk and write a checkpoint to the file. Note that the checkpoint is itself synced to
     * disk only if force is true.
     */
    public void checkpoint(boolean force) throws IOException {
        synchronized (channel) {
            // force all writes to disk before updating checkpoint length so we know all data up to length is good
            channel.force(true);
            if (length != lastCheckpointLength) {
                fileHeader.putInt(8, length);
                if (bucketIndex >= 0) putBucketDataInFileHeader();
                fileHeader.position(0);
                channel.position(0).write(fileHeader);
                lastCheckpointLength = length;
                if (force) channel.force(true);
            }
        }
    }

    /**
     * Increment the usage counter for this file. Each call to {@link #closeIfUnused()} decrements the counter and
     * the file is actually closed when the counter reaches zero.
     */
    public void use() {
        synchronized (channel) {
            ++usageCounter;
        }
    }

    /**
     * Close this file if no-one else is using it (see {@link #use()}). NOP if already closed.
     */
    public void closeIfUnused() throws IOException {
        synchronized (channel) {
            if (isOpen() && --usageCounter <= 0) {
                checkpoint(true);
                raf.close();
            }
        }
    }

    /**
     * Close this file even if it is in use. NOP if already closed.
     */
    @Override
    public void close() throws IOException {
        synchronized (channel) {
            if (isOpen()) {
                --usageCounter;
                checkpoint(true);
                raf.close();
            }
        }
    }

    /**
     * Is this file open?
     */
    public boolean isOpen() {
        synchronized (channel) {
            return channel.isOpen();
        }
    }

    @Override
    public String toString() {
        return "MessageFile[" + file + "] firstMessageId " + firstMessageId + " length " + length;
    }

    /**
     * Get the timestamp of the message most recently appended to this file or 0 if it is empty.
     */
    public long getMostRecentTimestamp() throws IOException {
        synchronized (channel) {
            if (mostRecentTimestamp == 0 && length > FILE_HEADER_SIZE) {
                MessageCursor c = cursor(getBucket(getBucketCount() - 1).getFirstMessageId());
                try {
                    while (c.next()) mostRecentTimestamp = c.getTimestamp();
                } finally {
                    c.close();
                }
            }
            return mostRecentTimestamp;
        }
    }

    /**
     * How many messages are in this file?
     */
    public int getMessageCount() throws IOException {
        synchronized (channel) {
            int count = 0;
            int start = bucketPosition(0);
            for (int i = 0; i < bucketIndex; i++) {
                count += fileHeader.getInt(start + i * 16 + 12);
            }
            count += bucketCount;
            return count;
        }
    }

    public Timeline getTimeline() throws IOException {
        synchronized (channel) {
            TimelineImpl ans = new TimelineImpl(firstMessageId, bucketIndex);
            fileHeader.position(bucketPosition(0));
            for (int i = 0; i < bucketIndex; i++) {
                ans.ids[i] = fileHeader.getInt();
                ans.timestamps[i] = fileHeader.getLong();
                ans.counts[i] = fileHeader.getInt();
            }
            ans.ids[bucketIndex] = bucketMessageId;
            ans.timestamps[bucketIndex] = bucketTimestamp;
            ans.counts[bucketIndex] = bucketCount;
            ans.ids[bucketIndex + 1] = (int)(getNextMessageId() - firstMessageId);
            ans.timestamps[bucketIndex + 1] = getMostRecentTimestamp();
            return ans;
        }
    }

    static class TimelineImpl implements Timeline {

        private long firstMessageId;
        private int[] ids, counts;
        private long[] timestamps;

        TimelineImpl(long firstMessageId, int bucketIndex) {
            this.firstMessageId = firstMessageId;
            ids = new int[bucketIndex + 2];
            this.timestamps = new long[bucketIndex + 2];
            counts = new int[bucketIndex + 1];
        }

        public int size() {
            return ids.length - 1;
        }

        public long getMessageId(int i) {
            return ids[i] + firstMessageId;
        }

        public long getTimestamp(int i) {
            return this.timestamps[i];
        }

        public int getBytes(int i) {
            return ids[i + 1] - ids[i];
        }

        public long getMillis(int i) {
            return this.timestamps[i + 1] - this.timestamps[i];
        }

        public int getCount(int i) {
            return counts[i];
        }
    }

    /**
     * How many histogram buckets are there?
     */
    public int getBucketCount() {
        synchronized (channel) {
            return bucketIndex + 1;
        }
    }

    /**
     * Get a copy of the data for the histogram bucket at index.
     */
    public Bucket getBucket(int i) {
        synchronized (channel) {
            if (i < 0 || i > bucketIndex) {
                throw new IllegalArgumentException("index " + i + " out of range (0 to " + bucketIndex + ")");
            }
            if (i == bucketIndex) {
                return new Bucket(firstMessageId + bucketMessageId, bucketTimestamp, bucketCount,
                        (length - FILE_HEADER_SIZE) - bucketMessageId);
            }
            fileHeader.position(bucketPosition(i));
            int id = fileHeader.getInt();
            return new Bucket(firstMessageId + id, fileHeader.getLong(), fileHeader.getInt(),
                    (i == bucketIndex - 1 ? bucketMessageId : fileHeader.getInt()) - id);
        }
    }

    /**
     * Get the index of the histogram bucket containing messageId or -1 if it is before the first message id or this
     * file is empty. If messageId is after the last message the last bucket index is returned.
     */
    public int findBucket(long messageId) throws IOException {
        synchronized (channel) {
            int key = (int)(messageId - firstMessageId);
            if (key >= bucketMessageId) return bucketIndex; // last bucket
            int low = 0;
            int high = bucketIndex - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                int midVal = fileHeader.getInt(bucketPosition(mid));
                if (midVal < key) low = mid + 1;
                else if (midVal > key) high = mid - 1;
                else return mid;
            }
            return low - 1;
        }
    }

    /**
     * Get the index of the histogram bucket containing timestamp or -1 if it is before the first message or this
     * file is empty. If timestamp is after the last message the last bucket index is returned.
     */
    public int findBucketByTimestamp(long timestamp) throws IOException {
        synchronized (channel) {
            if (timestamp >= bucketTimestamp) return bucketIndex; // last bucket
            int low = 0;
            int high = bucketIndex - 1;
            while (low <= high) {
                int mid = (low + high) >>> 1;
                long midVal = fileHeader.getLong(bucketPosition(mid) + 4);
                if (midVal < timestamp) low = mid + 1;
                else if (midVal > timestamp) high = mid - 1;
                else return mid;
            }
            return low - 1;
        }
    }

    public static class Bucket {

        private final long firstMessageId;
        private final long timestamp;
        private final int count;
        private final int size;

        public Bucket(long firstMessageId, long timestamp, int count, int size) {
            this.firstMessageId = firstMessageId;
            this.timestamp = timestamp;
            this.count = count;
            this.size = size;
        }

        /**
         * Get the timestamp of the first message in the bucket.
         */
        public long getTimestamp() {
            return timestamp;
        }

        /**
         * Get the ID of the first message in the bucket.
         */
        public long getFirstMessageId() {
            return firstMessageId;
        }

        /**
         * Get the number of messages in the bucket.
         */
        public int getCount() {
            return count;
        }

        /**
         * Get the number of bytes of messages in the bucket.
         */
        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return "Bucket{firstMessageId=" + firstMessageId + ", timestamp=" + timestamp + ", count=" + count +
                    ", size=" + size + '}';
        }
    }

    /**
     * Create a cursor reading data from messageId onwards. To read the oldest message appearing in the file
     * use {@link #getFirstMessageId()} as the message ID. To read the newest use {@link #getNextMessageId()}.
     * If messageId is 'between' messages it is advanced to the next message. The cursor is not thread safe.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public MessageCursor cursor(long messageId) throws IOException {
        long nextMessageId = getNextMessageId();
        if (messageId < firstMessageId || messageId > nextMessageId) {
            throw new IllegalArgumentException("messageId " + messageId + " not in " + this);
        }

        if (messageId == nextMessageId) return new Cursor(messageId); // at EOF

        long pos = getBucket(findBucket(messageId)).getFirstMessageId();
        Cursor c = new Cursor(pos);
        if (pos < messageId) {  // skip messages until we get to the one we want
            while (c.next() && c.getNextId() < messageId);
        }
        return c;
    }

    /**
     * Create a cursor reading data from timestamp onwards. If timestamp is before the first message then the cursor
     * reads starting at the first message. If timestamp is past the last message then the cursor will return false
     * until more messages appear in the file.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public MessageCursor cursorByTimestamp(long timestamp) throws IOException {
        int i = findBucketByTimestamp(timestamp);
        if (i < 0) return new Cursor(firstMessageId);

        // the first message with timestamp >= the time we are looking for may be in a previous bucket because
        // the bucket timestamp resolution is only ms so go back until we get a change in time .. that way we
        // are sure to find it
        Bucket b = getBucket(i);
        for (; b.getTimestamp() == timestamp && i > 0; b = getBucket(--i));

        Cursor c = new Cursor(getBucket(i).getFirstMessageId());
        for (; c.next(); ) {    // skip messages until we get one >= timestamp
            if (c.getTimestamp() >= timestamp) {
                c.unget();
                break;
            }
        }
        return c;
    }

    /**
     * Iterates over messages in the file. Not thread safe.
     */
    private class Cursor implements MessageCursor {

        private final ChannelInput input;
        private final byte[] routingKeyBuf = new byte[1024];

        private long id;
        private long timestamp;
        private int routingKeySize;
        private int payloadSize;

        private int nextPosition;

        public Cursor(long messageId) throws IOException {
            input = new ChannelInput(channel, messageIdToPosition(messageId), 8192);
        }

        private int messageIdToPosition(long messageId) {
            return (int)(messageId - firstMessageId) + FILE_HEADER_SIZE;
        }

        private void unget() {
            routingKeySize = payloadSize = -1;
            input.position(messageIdToPosition(id));
        }

        /**
         * Advance to the next message or return false if there are no more messages. The cursor initially starts
         * "before" the next message.
         */
        public boolean next() throws IOException {
            if (routingKeySize > 0) {
                input.skip(routingKeySize); // routing key was never read
                routingKeySize = -1;
            }
            if (payloadSize > 0) {
                input.skip(payloadSize);       // payload was never read
                payloadSize = -1;
            }

            int len = length();
            if (input.position() >= len) return false;

            id = firstMessageId + input.position() - FILE_HEADER_SIZE;

            byte type = input.readByte();
            if (type != TYPE_MESSAGE) {
                throw new IOException("Unexpected message type 0x" + Integer.toHexString(type & 0xFF) + " at " +
                        (input.position() - 1) + " in " + MessageFile.this);
            }

            timestamp = input.readLong();

            routingKeySize = input.readShort();
            if (routingKeySize < 0 || routingKeySize >= routingKeyBuf.length) {
                throw new IOException("Invalid routing key size " + routingKeySize + " at " +
                        (input.position() - 2) + " in " + MessageFile.this);
            }

            payloadSize = input.readInt();
            if (payloadSize < 0) {
                throw new IOException("Negative payload size " + payloadSize + " at " + (input.position() - 4)  +
                        " in " + MessageFile.this);
            }

            nextPosition = input.position() + routingKeySize + payloadSize;
            if (nextPosition > len) {
                throw new IOException("Payload size " + payloadSize + " at " + (input.position() - 4) +
                        " extends beyond EOF " + len + " in " + MessageFile.this);
            }

            return true;
        }

        @Override
        public boolean next(int timeoutMs) throws IOException {
            throw new UnsupportedOperationException();
        }

        public long getId() {
            return id;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getRoutingKey() throws IOException {
            if (routingKeySize < 0) throw new IllegalStateException("Routing key already read");
            input.read(routingKeyBuf, 0, routingKeySize);
            String ans = new String(routingKeyBuf, 0, routingKeySize, UTF8);
            routingKeySize = -1;
            return ans;
        }

        public int getPayloadSize() {
            return payloadSize;
        }

        public byte[] getPayload() throws IOException {
            if (payloadSize < 0) throw new IllegalStateException("Payload already read");
            if (routingKeySize > 0) {
                input.skip(routingKeySize); // routing key was never read
                routingKeySize = -1;
            }
            byte[] buf = new byte[payloadSize];
            input.read(buf, 0, payloadSize);
            payloadSize = -1;
            return buf;
        }

        public long getNextId() {
            return firstMessageId + nextPosition - FILE_HEADER_SIZE;
        }

        @Override
        public void close() throws IOException {
            // nothing to do
        }
    }
}
