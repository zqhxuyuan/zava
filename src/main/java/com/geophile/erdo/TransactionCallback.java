/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

/**
 * A TransactionCallback has a method that is invoked after the records from a transaction, committed asynchronously,
 * have been made durable.
 */

public interface TransactionCallback
{
    /**
     * Invoked when the records from a transaction, committed asynchronously,
     * have been made durable.
     * @param commitInfo Second argument to
     *     {@link com.geophile.erdo.Database#commitTransactionAsynchronously(TransactionCallback, Object)}. Intended to identify
     *     the transaction whose records have just become durable. If the transaction was committed using
     *     {@link Database#commitTransactionAsynchronously(TransactionCallback)}, then commitInfo is null.
     */
    void whenDurable(Object commitInfo);

    /**
     * Transaction callback that does nothing.
     */
    static TransactionCallback DO_NOTHING = new TransactionCallback()
    {
        public void whenDurable(Object commitInfo)
        {
        }
    };
}
