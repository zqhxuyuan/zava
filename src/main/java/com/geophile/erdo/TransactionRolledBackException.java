/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

/**
 * Thrown by a transaction that is rolled back due to a concurrency conflict.
 *
 * This exception is not thrown in case of a deadlock. A deadlock victim throws
 * {@link com.geophile.erdo.DeadlockException} instead.
 */

public class TransactionRolledBackException extends Exception
{
    public TransactionRolledBackException(String message)
    {
        super(message);
    }
}
