/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

// Represents a tree, which records updates from one or more transactions. The updates from one
// transaction are completely contained in one tree at a time.

public interface TransactionUpdates
{
    /**
     * Sets the timestamp of a tree of updates not created by consolidation.
     * @param timestamp Timestamp of the updates.
     */
    void transactionTimestamp(long timestamp);

    /**
     * The timestamps of transactions represented by this map. The state of a transaction is
     * always entirely contained in either one or two maps. In the case of two maps,
     * one of the maps is being consolidated into the other.
     *
     * @return The timestamps of transactions represented by this map.
     */
    TimestampSet timestamps();

    /**
     * Returns the number of records contained by the map. This is not the same as cardinality,
     * since some of the contained records may have deleted() = true.
     *
     * @return Number of records contained by the map.
     */
    long recordCount();
}
