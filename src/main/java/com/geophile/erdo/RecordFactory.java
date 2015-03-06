/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.map.SimpleRecordFactory;

import java.io.Serializable;

/**
 * A RecordFactory creates new uninitialized keys and records for an {@link com.geophile.erdo.OrderedMap}.
 */

public abstract class RecordFactory implements Serializable
{
    /**
     * Create a new uninitialized key.
     * @return a new uninitialized key.
     */
    public abstract AbstractKey newKey();

    /**
     * Create a new uninitialized record.
     * @return a new uninitialized record.
     */
    public abstract AbstractRecord newRecord();

    /**
     * For many applications, a "simple" RecordFactory suffices, in which new keys and records are created
     * by calling zero-argument constructors of the key and record classes. If this technique is not suitable,
     * then a RecordFactory subclass must be defined.
     * @param keyClass A key class.
     * @param recordClass A record class.
     * @return A RecordFactory which creates keys and records using zero-argument constructors of the
     * key and record classes.
     */
    public static RecordFactory simpleRecordFactory(Class<? extends AbstractKey> keyClass,
                                                    Class<? extends AbstractRecord> recordClass)
    {
        return new SimpleRecordFactory(keyClass, recordClass);
    }
}
