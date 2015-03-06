/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.transaction.Transaction;

import java.io.IOException;
import java.util.List;

public interface Consolidation
{
    public interface Element
    {
        long id();

        List<Transaction> transactions();

        TimestampSet timestamps();

        long count(); // Number of records

        long sizeBytes(); // Estimated or actual size on disk

        void destroyPersistentState();

        boolean durable();

        /**
         * Records with this map some of the transactions that contributed updates to it.
         * The complete set of transactions is expected to be supplied across several invocations.
         * (So an implementation should add transactions to the complete set, not replace.)
         * @param transactions A subset of the transactions that contributed updates to this map.
         */
        void registerTransactions(List<Transaction> transactions);

        /**
         * markDurable will eventually be called exactly once for every consolidation element
         * that starts out non-durable.
         */
        void markDurable();
    }

    public interface Container
    {
        Configuration configuration();
        Factory factory();
        Element consolidate(List<Element> elements, boolean inputDurable, boolean outputDurable)
            throws IOException, InterruptedException;
        void replaceObsolete(List<Element> obsolete, Element replacement);
        void reportCrash(Throwable crash);
    }
}
