/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.systemtest.lockmanagement;

import com.geophile.erdo.AbstractRecord;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class Account extends AbstractRecord<AccountId>
{
    // Object interface

    public String toString()
    {
        return String.format("(%s: %s)", key(), balance);
    }

    // AbstractRecord interface

    @Override
    public long estimatedSizeBytes()
    {
        return super.estimatedSizeBytes() + 8;
    }

    @Override
    public void readFrom(ByteBuffer buffer) throws BufferUnderflowException
    {
        super.readFrom(buffer);
        balance = buffer.getLong();
    }

    @Override
    public void writeTo(ByteBuffer buffer) throws BufferOverflowException
    {
        super.writeTo(buffer);
        buffer.putLong(balance);
    }

    @Override
    public AbstractRecord copy()
    {
        return new Account(this);
    }

    // Account interface

    public void balance(long balance)
    {
        this.balance = balance;
    }

    public long balance()
    {
        return balance;
    }

    public Account(AccountId key)
    {
        super(key);
    }
    
    public Account(Account account)
    {
        super(account);
        this.balance = account.balance();
    }

    public Account()
    {}

    // Object state

    private long balance;
}
