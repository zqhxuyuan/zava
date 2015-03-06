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

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.Executor;

/**
 * Queue that supports sequential retrieval of old messages by id and timestamp.
 */
public interface MessageBuffer extends Closeable {

    /**
     * Is this buffer open?
     */
    boolean isOpen();

    /**
     * What should the id of the first message appended be? Throws IllegalStateException if the buffer is not
     * empty.
     */
    void setFirstId(long firstMessageId) throws IOException;

    /**
     * Append a message and return its id.
     * Throws IllegalArgumentException if the payload exceeds {@link #getMaxPayloadSize()}.
     */
    long append(long timestamp, String routingKey, byte[] payload) throws IOException;

    /**
     * Append a message and return its id.
     * Throws IllegalArgumentException if the payload exceeds {@link #getMaxPayloadSize()}.
     */
    long append(long timestamp, String routingKey, ReadableByteChannel payload, int payloadSize) throws IOException;

    /**
     * How much space will the message consume in the buffer including header overhead?
     */
    int getMessageSize(String routingKey, int payloadSize);

    /**
     * What ID will the next message appended have?
     */
    long getNextId() throws IOException;

    /**
     * Create a cursor reading the next message with id greater than or equal to messageId onwards (i.e. messageId
     * can be 'between' messages). To read the oldest message use 0 as the message ID. To read the newest use
     * {@link #getNextId()}. If the messageId is before the oldest message the the cursor reads from the
     * oldest message onwards. The cursor should only be used from one thread at a time i.e. it is not thread safe.
     */
    MessageCursor cursor(long messageId) throws IOException;

    /**
     * Create a cursor reading data from timestamp onwards. If timestamp is before the first message then the cursor
     * reads starting at the first message. If timestamp is past the last message then the cursor will return false
     * until more messages appear in the buffer. The cursor should only be used from one thread at a time i.e. it is
     * not thread safe.
     */
    MessageCursor cursorByTimestamp(long timestamp) throws IOException;

    /**
     * Set the maximum size of this buffer in bytes. When it is full the oldest messages are deleted to make space.
     */
    void setMaxSize(long bytes) throws IOException;

    /**
     * What is the maximum size of this buffer in bytes?
     */
    long getMaxSize();

    /**
     * What is the maximum size of a message payload in bytes? Set to 0 for the default of approximately
     * {@link #getMaxSize} / 1000.
     */
    void setMaxPayloadSize(int maxPayloadSize);

    /**
     * What is the maximum size of a message payload in bytes?
     */
    int getMaxPayloadSize();

    /**
     * Is this buffer empty?
     */
    boolean isEmpty() throws IOException;

    /**
     * How much space is this buffer currently consuming in bytes?
     */
    long getSize() throws IOException;

    /**
     * How many messages are in the buffer?
     */
    long getMessageCount() throws IOException;

    /**
     * What is the timestamp of the oldest message in the buffer? Returns null if the buffer is empty.
     */
    Date getOldestTimestamp() throws IOException;

    /**
     * What is the id of the oldest message in the buffer? Returns {@link #setFirstId(long)} if the buffer is
     * empty or 0 if no firstMessageId has been set.
     */
    long getOldestId() throws IOException;

    /**
     * What is the timestamp of the newest message in the buffer? Returns null if the buffer is empty.
     */
    Date getMostRecentTimestamp() throws IOException;

    /**
     * When was this buffer instance created? Note that this is when this instance was created in this
     * virtual machine and has no relationship to when the actual disk files were created.
     */
    long getCreationTime();

    /**
     * Sync all changes to persistent storage. A system crash immediately following this call will not result in
     * any loss of messages.
     */
    void sync() throws IOException;

    /**
     * A {@link #sync()} is done every this many ms if at least one message has been appended since the last
     * sync. Default is 1000 ms. Set to 0 to disable auto-sync.
     */
    void setAutoSyncInterval(int ms);

    /**
     * How often are auto syncs done in ms?
     * @see #setAutoSyncInterval(int)
     */
    int getAutoSyncInterval();

    /**
     * Set the timer used for auto-sync (see {@link #setAutoSyncInterval(int)}). If none is set then one will be
     * created when it is first needed i.e. if auto-sync is enabled and a sync is scheduled.
     */
    void setTimer(Timer timer);

    /**
     * Get a snapshot of the high level timeline for this buffer. Note that if the buffer is empty null is returned.
     */
    Timeline getTimeline() throws IOException;

    /**
     * Get the detailed timeline from approximately messageId onwards in the buffer or null if this is not available
     * (e.g. the message has been deleted).
     */
    Timeline getTimeline(long messageId) throws IOException;

    /**
     * Provide an executor (e.g. thread pool) to do cleanup's asynchronously when the buffer starts a new message
     * file. If no executor is set then cleanups are done synchronously i.e. on the thread appending the message.
     */
    void setExecutor(Executor executor);
}
