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
 * An instance of this class is used to add message data to a {@link MessageQueue}.
 *
 * @author Niels Peter Strandberg
 */
public class MessageInput implements Message, Serializable
{

	private static final long serialVersionUID = -1199735856685175142L;
	private String body;
    private Serializable object;
    private long startDelay;

    private String duplicateSuppressionKey;
    private OnCollision duplicateSuppressionAction;

    /**
     * Constructs a new MessageInput
     */
    public MessageInput() {}

    /**
     * Constructs a new MessageInput and add the body
     *
     * @param body
     */
    public
    MessageInput(String body)
    {
        this.body = body;
    }

    public
    MessageInput(String body, Serializable object)
    {
        this.body = body;
        this.object = object;
    }

    public
    String getBody()
    {
        return body;
    }

    public
    MessageInput setBody(String body)
    {
        this.body=body;
        return this;
    }

    public
    Serializable getObject()
    {
        return object;
    }

    @Override
    public
    long getId()
    {
        return -1;
    }

    private long creationTime=System.currentTimeMillis();

    public
    long getTime()
    {
        return creationTime;
    }

    public
    MessageInput setObject(Serializable object)
    {
        this.object = object;
        return this;
    }

    public
    long getStartDelay()
    {
        return startDelay;
    }

    public
    MessageInput setStartDelay(long startDelay)
    {
        this.startDelay = startDelay;
        return this;
    }

    public
    String getDuplicateSuppressionKey()
    {
        return duplicateSuppressionKey;
    }

    public
    MessageInput setDuplicateSuppressionKey(String duplicateSuppressionKey, OnCollision onCollision)
    {
        //Both arguments must be null or defined... but not "one of each".
        if ((duplicateSuppressionKey==null) != (onCollision==null)) throw new NullPointerException();

        this.duplicateSuppressionKey = duplicateSuppressionKey;
        this.duplicateSuppressionAction = onCollision;
        return this;
    }

    public
    OnCollision getDuplicateSuppressionAction()
    {
        return duplicateSuppressionAction;
    }
}
