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
package com.github.Allogy.simplemq.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A Job can be serialized and put on the queue. It implements Runnable,
 * so the reciever can run then Job in a Thread.
 *
 * @author Niels Peter Strandberg
 */
public abstract class Job implements Serializable, Runnable {

    private Map<String, Serializable> metadata = new HashMap<String, Serializable>();

    public Serializable getMetaData(String key) {
        return metadata.get(key);
    }

    public void putMetaData(String key, Serializable value) {
        metadata.put(key, value);
    }

    public boolean containsKey(String key) {
        return metadata.containsKey(key);
    }

    public void setMetaData(Map<String, Serializable> metadata) {
        this.metadata = metadata;
    }

}
