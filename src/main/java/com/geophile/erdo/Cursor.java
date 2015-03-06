/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import java.io.IOException;

/**
 * A {@link Cursor} is used to visit the records of an {@link com.geophile.erdo.OrderedMap}.
 * Key order is defined by {@link com.geophile.erdo.AbstractKey#compareTo(AbstractKey)}.
 */

public abstract class Cursor
{
    /**
     * Move to and return the next record of the sequence of records represented by this Cursor, where "next"
     * is defined as follows:
     * <ul>
     * <li> null if the Cursor has been explicitly closed, by calling {@link Cursor#close()}.
     * <li> If the Cursor has just been created by calling {@link OrderedMap#first()}, then the next record
     *   is the record with the smallest key, or null if the map is empty.
     * <li> If the Cursor has just been created by calling {@link OrderedMap#last()}, then the next record is the
     *   record with the largest key, or null if the map is empty.
     * <li> If the Cursor has just been created by calling {@link OrderedMap#cursor(AbstractKey)}, then the next
     *   record is the one with the given key; or if there is no such record, the record with the next larger key;
     *   or null if there is no such record.
     * <li> Otherwise, the next record is the one with the smallest key larger than that of the previously returned
     *   record, (obtained by calling either next() or previous()); or null if there is no such record.
     * </ul>
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract AbstractRecord next() throws IOException, InterruptedException;

    /**
     * Move to and return the previous record of the sequence of records represented by this Cursor, where "previous"
     * is defined as follows:
     * <ul>
     * <li> null if the Cursor has been explicitly closed, by calling {@link Cursor#close()}.
     * <li> If the Cursor has just been created by calling {@link OrderedMap#first()}, then the previous record
     *   is the record with the smallest key, or null if the map is empty.
     * <li> If the Cursor has just been created by calling {@link OrderedMap#last()}, then the previous record is the
     *   record with the largest key, or null if the map is empty.
     * <li> If the Cursor has just been created by calling {@link OrderedMap#cursor(AbstractKey)}, then the previous
     *   record is the one with the given key; or if there is no such record, the record with the next smaller key;
     *   or null if there is no such record.
     * <li> Otherwise, the previous record is the one with the largest key smaller than that of the previously returned
     *   record, (obtained by calling either next() or previous()); or null if there is no such record.
     * </ul>
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract AbstractRecord previous() throws IOException, InterruptedException;

    /**
     * Closes the cursor. After close() returns, the cursor is not positioned on any record, and subsequent
     * calls to {@link #next()} and {@link #previous()} will return null.
     */
    public abstract void close();
}
