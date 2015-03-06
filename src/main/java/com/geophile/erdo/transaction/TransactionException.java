/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.transaction;

public abstract class TransactionException extends Exception
{
    @Override
    public String getMessage()
    {
        return String.format("%s: %s", getClass().getName(), transaction);
    }

    protected TransactionException(Transaction transaction)
    {
        this.transaction = transaction;
    }
    
    private Transaction transaction;

}
