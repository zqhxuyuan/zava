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

/**
 * A collection of points on the message timeline for a buffer.
 */
public interface Timeline {

    /**
     * How many points are on this timeline?
     */
    int size();

    /**
     * Get the id of the message at i on the timeline.
     */
    long getMessageId(int i);

    /**
     * Get the timestamp of the message at i on the timeline. Note that this may be rounded down to the nearest
     * second.
     */
    long getTimestamp(int i);

    /**
     * Return the number of bytes of messages between i and i + 1 on the timeline.
     */
    int getBytes(int i);

    /**
     * Return the time between i and i + 1 on the timeline.
     */
    long getMillis(int i);

    /**
     * Return the number of messages between i and i + 1 on the timeline. Note that this will return -1 if this
     * unknown.
     */
    int getCount(int i);

}
