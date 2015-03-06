/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

/**
 * Indicates that write/write lock dependencies formed a cycle. The transaction that throws this exception
 * was rolled back to break the cycle.
 */

public class DeadlockException extends Exception
{
    protected DeadlockException(String message)
    {
        super(message);
    }
}
