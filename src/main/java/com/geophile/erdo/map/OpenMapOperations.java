/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.DeadlockException;
import com.geophile.erdo.TransactionRolledBackException;

import java.io.IOException;

public interface OpenMapOperations extends Map
{
    /**
     * Store the record in the map, associating it with the record's key. Put is used for all update operations:
     * inserting, removing and replacing records. Removal is accomplished by passing a record with deleted() = true.
     *
     *
     * @param record         The record being written to the map.
     * @param returnReplaced If true, then return the record currently in the map whose key is record.key(),
     *                       or null if there is no such record. If false, then return null.
     *                       returnReplaced = false will usually
     *                       result in much higher performance.
     * @return If returnReplaced is false, then null. Otherwise, return the record associated with the
     *         key before the replace is executed. If there is no such record, then null is returned.
     */
    LazyRecord put(AbstractRecord record, boolean returnReplaced)
        throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException;
}
