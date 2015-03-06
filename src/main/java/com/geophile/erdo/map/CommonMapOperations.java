/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.DeadlockException;
import com.geophile.erdo.TransactionRolledBackException;

import java.io.IOException;

/**
 * Interface common to all maps, whether open or sealed.
 */
public interface CommonMapOperations extends Map
{
    /**
     * Returns an identifier of this map, guaranteed to be unique within the current JVM.
     *
     * @return unique map identifier.
     */
    long mapId();

    /**
     * If singleKey is true, then return a Cursor that will provide access to the record with the given key,
     * or null if there is no such record. If singleKey is false, then the Cursor will provide access to the
     * entire map, starting with the given key.
     * @param key The starting key.
     * @param singleKey true iff the returned cursor will be used to access just the record with the given key.
     * @return A {@link MapCursor} that will visit qualifying records in key order.
     * @throws IOException
     * @throws InterruptedException
     */
    MapCursor cursor(AbstractKey key, boolean singleKey) throws IOException, InterruptedException;

    /**
     * Lock the specified key for writing. This method will block if the key is already locked for
     * writing by another transaction.
     * @param key Key to be locked.
     */
    void lock(AbstractKey key)
        throws InterruptedException, DeadlockException, TransactionRolledBackException;
}
