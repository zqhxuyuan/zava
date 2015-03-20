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


import com.github.Allogy.simplemq.config.MessageQueueConfig;
import com.github.Allogy.simplemq.config.PersistentMessageQueueConfig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The entry point for getting/creating and deleting message queues
 *
 * @author Niels Peter Strandberg
 */
public final class MessageQueueService {

    private static final Map<String, MessageQueue> queues = new HashMap<String, MessageQueue>();

    private MessageQueueService() {
        //
    }

    /**
     * If a queue by the name of 'queueName' allready excists - a reference to that queue is returned.
     * If no queue is found - a new "in-memory" queue is created with the name 'queueName' and a default {@link com.github.Allogy.simplemq.config.MessageQueueConfig}.
     *
     * @param queueName - the name of the queue
     * @return a "in-memory" message queue
     */
    public static MessageQueue getMessageQueue(String queueName) {
        return getMessageQueue(queueName, false);
    }

    /**
     * If a queue by the name of 'queueName' allready excists - a reference to that queue is returned.
     * If no queue is found and 'Persistent' is set to 'true' - a new "Persistent" queue is created with the name 'queueName' and a
     * default {@link com.github.Allogy.simplemq.config.PersistentMessageQueueConfig} or else a "in-memory" queue is created with the name 'queueName' and a default {@link com.github.Allogy.simplemq.config.MessageQueueConfig}.
     *
     * @param queueName  - the name of the queue
     * @param persistent - 'true' creates a "persistens" queue, 'false' creates a "in-memory" queue
     * @return a message queue
     */
    public static MessageQueue getMessageQueue(String queueName, boolean persistent) {
        if (persistent) {
            return getMessageQueue(queueName, new PersistentMessageQueueConfig());
        } else {
            return getMessageQueue(queueName, new MessageQueueConfig());
        }
    }

    /**
     * If a queue by the name of 'queueName' allready excists - a reference to that queue is returned.
     * If no queue is found and 'config' is an instance of {@link com.github.Allogy.simplemq.config.PersistentMessageQueueConfig}
     * - a new "Persistent" queue is created with the name 'queueName'. Or if 'config' is an instance of
     * {@link com.github.Allogy.simplemq.config.MessageQueueConfig} - a new "in-memory" queue is created
     * with the name 'queueName'.
     * <p/>
     * The config object is copied/cloned by the message queue. To get a reference to the copied/cloned
     * config object use {@link MessageQueue#getMessageQueueConfig()}.
     *
     * @param queueName - the name of the queue
     * @param config    - an instance of {@link com.github.Allogy.simplemq.config.PersistentMessageQueueConfig}
     *                  or {@link com.github.Allogy.simplemq.config.MessageQueueConfig}
     * @return a message queue
     */
    public static MessageQueue getMessageQueue(String queueName, MessageQueueConfig config) {
        if (queueName == null) {
            throw new NullPointerException("The name of the queue cannot be 'null'");
        }

        synchronized (queues) {
            if (queues.containsKey(queueName)) {
                return queues.get(queueName);
            } else {
                MessageQueue newQueue = new MessageQueueImp(queueName, config);

                queues.put(queueName, newQueue);

                return newQueue;
            }
        }
    }

    /**
     * Deletes a message queue and all remaning messages in it.
     * If it is a "Persistent" queue all files is also deleted.
     *
     * @param queueName
     * @return true - if the deletion was successfull
     */
    public static boolean deleteMessageQueue(String queueName) {
        if (queueName == null) {
            throw new NullPointerException("The name of the queue cannot be 'null'");
        }

        synchronized (queues) {
            if (queues.containsKey(queueName)) {
                MessageQueue queue = queues.get(queueName);
                MessageQueueImp mqi = (MessageQueueImp) queue;

                mqi.deleteQueue();
                queues.remove(queueName);

                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Return the names of all exsisting message queues
     *
     * @return all queue names
     */
    public static Collection<String> getMessageQueueNames() {
        return queues.keySet();
    }

    public static
    boolean forgetMessageQueue(MessageQueue queue)
    {
        synchronized (queues)
        {
            return (queues.remove(queue.getQueueName())!=null);
        }
    }

    public static
    boolean forgetMessageQueue(String queueName)
    {
        synchronized (queues)
        {
            return (queues.remove(queueName)!=null);
        }
    }

}
