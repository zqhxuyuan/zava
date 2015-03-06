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

/**
 * Iterate over messages in a forward direction.
 */
public interface MessageCursor extends Closeable {

    /**
     * Advance to the next message or return false if there are no more messages. The cursor initially starts
     * "before" the first message (if any). Note that it is ok to call next repeatedly after it returns false.
     * If a new message is appended it will return true and the message can be read.
     */
    public boolean next() throws IOException;

    /**
     * Advance to the {@link #next()} message blocking for up to timeoutMs milliseconds if none are available.
     * Waits forever if timeoutMs is 0. Returns false the timeout expires before a message is available.
     *
     * @exception InterruptedException if this thread is interrupted while waiting for a message. The thread will
     *      be interrupted if the underlying MessageBuffer is closed
     */
    public boolean next(int timeoutMs) throws IOException, InterruptedException;

    /**
     * Get the ID of the current message.
     */
    public long getId() throws IOException;

    /**
     * Get the timestamp of the current message.
     */
    public long getTimestamp() throws IOException;

    /**
     * Get the routing key of the current message.
     */
    public String getRoutingKey() throws IOException;

    /**
     * Get the size in bytes of the payload of the current message.
     */
    public int getPayloadSize() throws IOException;

    /**
     * Get the payload of the current message.
     */
    public byte[] getPayload() throws IOException;

    /**
     * What will the id of the next message be? Note that it may not exist yet.
     */
    public long getNextId() throws IOException;
}
