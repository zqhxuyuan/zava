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
package com.github.Allogy.simplemq.config;


import java.io.Serializable;

/**
 * The MessageQueueConfig is used to configure a "in memory" {@link com.github.Allogy.simplemq.MessageQueue}.
 * For at "Persistent" MessageQueue, use {@link PersistentMessageQueueConfig}
 * 消息队列保存在内存时的配置项
 *
 * @author Niels Peter Strandberg
 * @see com.github.Allogy.simplemq.MessageQueueService#getMessageQueue(String, MessageQueueConfig)
 * @see PersistentMessageQueueConfig
 */
public class MessageQueueConfig implements Serializable {

    /**
     * The default value for {@link #messageReviveTime} is 3600 sec (1 hour).
     */
    public static final int DEFAULT_MSG_REVIVE_TIME = 60 * 60;

    /**
     * The default value for {@link #messageReviveTime} is 86.400 (24 hours).
     */
    public static final int DEFAULT_MSG_REMOVE_TIME = 60 * 60 * 24;

    /**
     * The default value for {@link #deleteOldMessagesThreadDelay} is 3600 seconds (1 hour).
     */
    public static final int DEFAULT_DELETE_THREAD_DELAY = 60 * 60;

    /**
     * The default value for {@link #reviveNonDeletedMessagsThreadDelay} is 1600 seconds (30 minutes).
     */
    public static final int DEFAULT_REVIVE_THREAD_DELAY = 60 * 30;

    /**
     * The delay in seconds between checks by the background Thread that looks for messages
     * that has not been read and is to old using the 'messageRemoveTime'.
     */
    private int deleteOldMessagesThreadDelay;

    /**
     * Indicate the delay in seconds between checks by the background Thread that looks for messages
     * that has been read but not deleted for 'messageReviveTime' ago.
     */
    private int reviveNonDeletedMessagsThreadDelay;

    /**
     * The no of seconds the message should be in the queue before it is deleted.
     */
    private int messageRemoveTime;

    /**
     * The no of seconds the message should be in the fetched queue before it is revieved.
     */
    private int messageReviveTime;

    /**
     * Set to true results in events related to persistence to be logged, including any failures. The events are logged in a file ending with .app.log
     */
    private boolean hsqldbapplog;


    /**
     * Constructs a new MessageQueueConfig with default values
     */
    public MessageQueueConfig() {
        messageReviveTime = DEFAULT_MSG_REVIVE_TIME;
        messageRemoveTime = DEFAULT_MSG_REMOVE_TIME;
        reviveNonDeletedMessagsThreadDelay = DEFAULT_REVIVE_THREAD_DELAY;
        deleteOldMessagesThreadDelay = DEFAULT_DELETE_THREAD_DELAY;
    }


    public int getMessageReviveTime() {
        return messageReviveTime;
    }


    public int getMessageRemoveTime() {
        return messageRemoveTime;
    }


    public int getReviveNonDeletedMessagsThreadDelay() {
        return reviveNonDeletedMessagsThreadDelay;
    }


    public int getDeleteOldMessagesThreadDelay() {
        return deleteOldMessagesThreadDelay;
    }

    public MessageQueueConfig setMessageReviveTime(int messageReviveTime) {
        this.messageReviveTime = messageReviveTime;
        return this;
    }


    public MessageQueueConfig setMessageRemoveTime(int messageRemoveTime) {
        this.messageRemoveTime = messageRemoveTime;
        return this;
    }

    public MessageQueueConfig setDeleteOldMessagesThreadDelay(int deleteOldMessagesThreadDelay) {
        this.deleteOldMessagesThreadDelay = deleteOldMessagesThreadDelay;
        return this;
    }


    public MessageQueueConfig setReviveNonDeletedMessagsThreadDelay(int reviveNonDeletedMessagsThreadDelay) {
        this.reviveNonDeletedMessagsThreadDelay = reviveNonDeletedMessagsThreadDelay;
        return this;
    }

    public boolean getHsqldbapplog() {
        return hsqldbapplog;
    }

    public MessageQueueConfig setHsqldbapplog(boolean hsqldbapplog) {
        this.hsqldbapplog = hsqldbapplog;
        return this;
    }
}

