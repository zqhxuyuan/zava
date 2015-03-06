/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import java.io.IOException;

/**
 * An OrderedMap maintains a set of key/value pairs. {@link #put(AbstractRecord)} and
 * {@link #ensurePresent(AbstractRecord)} are used to associate a record with the record's key, so these
 * implement both "insert" and "update" behavior. {@link #put(AbstractRecord)}, unlike
 * {@link #ensurePresent(AbstractRecord)}, returns the record previously associated with the record's key.
 * While {@link #put(AbstractRecord)} is more generally useful, {@link #ensurePresent(AbstractRecord)} is likely
 * to be faster because the implementation does not have to find the previous record.
 *
 * <p> Similarly, {@link #delete(AbstractKey)} and {@link #ensureAbsent(AbstractKey)} both accomplish deletion.
 * {@link #delete(AbstractKey)} returns the record previously associated with the given key, while
 * {@link #ensureAbsent(AbstractKey)} does not. {@link #ensureAbsent(AbstractKey)} is therefore likely to be faster.
 *
 * <p> Keys are locked for write through {@link #put(AbstractRecord)},
 * {@link #ensurePresent(AbstractRecord)}, {@link #delete(AbstractKey)}, and {@link #ensureAbsent(AbstractKey)}.
 * To lock additional keys, call {@link #lock(AbstractKey)}.
 *
 * <p> {@link #find(AbstractKey)} returns the value associated with a given key, or null if the key is not present.
 * {@link #cursor(AbstractKey)} returns a {@link Cursor} positioned at the given key, which can then be moved
 * forward or backward. {@link #first()} and {@link #last()} return Cursors positioned at the first and last keys
 * of the map, respectively.
 */

public abstract class OrderedMap
{
    /**
     * Store the record in the map, associating it with the record's key. If there was already a record
     * associated with the key, the older record is replaced. ensurePresent is usually much faster than
     * {@link #put(AbstractRecord)}, but does not return the replaced record.
     * @param record The record being written to the map.
     */
    public abstract void ensurePresent(AbstractRecord record)
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException;

    /**
     * Store the record in the map, associating it with the record's key. If there was already a record
     * associated with the key, the older record is replaced.
     * put is usually much slower than {@link #ensurePresent(AbstractRecord)}, but it does return the replaced record.
     * @param record The record being written to the map.
     * @return The record previously associated with the key, or null if the key does not currently exist in the map.
     */
    public abstract AbstractRecord put(AbstractRecord record)
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException;

    /**
     * Ensures that there is no record associated with the given key. ensureAbsent is usually much faster than
     * {@link #delete(AbstractKey)}, but does not return the deleted record.
     * @param key The key whose record is to be deleted.
     */
    public abstract void ensureAbsent(AbstractKey key)
        throws IOException, 
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException;

    /**
     * Removes from the map the record associated with the given key. delete is usually much slower than
     * {@link #ensureAbsent(AbstractKey)}, but it does return the deleted record.
     * @param key The key whose record is to be deleted.
     * @return The record associated with the key prior to the deletion, or null if the key does not currently
     *         exist in the map.
     */
    public abstract AbstractRecord delete(AbstractKey key)
        throws IOException, 
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException;

    /**
     * Lock the given key for writing. This method will block if and only if the key is already locked for
     * writing by another transaction.
     * @param key Key to be locked.
     */
    public abstract void lock(AbstractKey key)
        throws InterruptedException,
               DeadlockException,
               TransactionRolledBackException, IOException;

    /**
     * Returns the record with the given key. If the key is not present in the map, then null is returned.
     * @param key The key to search for.
     * @return The record associated with the key, or null if the key is not present in the map.
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract AbstractRecord find(AbstractKey key) throws IOException, InterruptedException;

    /**
     * Returns a cursor positioned at the given key.
     * @param key The key to search for.
     * @return A cursor positioned at the given key
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract Cursor cursor(AbstractKey key)
        throws IOException, InterruptedException;

    /**
     * Returns a cursor positioned at the record with the smallest key present in the map.
     * @return A cursor positioned at the record with the smallest key present in the map.
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract Cursor first() throws IOException, InterruptedException;

    /**
     * Returns a cursor positioned at the record with the largest key present in the map.
     * @return A cursor positioned at the record with the largest key present in the map.
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract Cursor last() throws IOException, InterruptedException;
}
