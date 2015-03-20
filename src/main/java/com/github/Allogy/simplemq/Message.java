/*
 * Copyright 2008 Niels Peter Strandberg.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.Allogy.simplemq;


import java.io.Serializable;

/**
 * A Message is an immutable object returned by calling the receive methods on a {@link MessageQueue}.
 * 当调用MessageQueue.receive会返回一个不可变的对象: 就是消息对象!
 *
 * @author Niels Peter Strandberg
 * @see MessageQueue#receive()
 */
public interface Message {

    /**
     * Returns the {@link String} body 消息内容
     *
     * @return the body
     */
    String getBody();

    /**
     * Returns the {@link Serializable} object
     *
     * @return the {@link Serializable} object
     */
    Serializable getObject();

    /**
     * Returns the internal id
     * The internal id is set by the {@link MessageQueue}
     *
     * @return the internal id
     */
    long getId();

    /**
     * @return the time in milliseconds that this message became available (usually the enqueue time)
     */
    long getTime();

    /**
     * @return the string used to detect and automatically act upon similar/same messages in the queue, or null
     */
    String getDuplicateSuppressionKey();

    /**
     * @return the action that was to be performed if this message collided with another in the same message queue, or null only if the duplicate suppression key is also null
     */
    OnCollision getDuplicateSuppressionAction();

    /**
     * @return the amount of time (in milliseconds) that this message would be in a quiet period (immediately after being enqueued), before which it could not be dequeued.
     */
    long getStartDelay();
}
