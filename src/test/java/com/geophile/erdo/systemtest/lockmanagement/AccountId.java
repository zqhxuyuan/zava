/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.systemtest.lockmanagement;

import com.geophile.erdo.AbstractKey;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class AccountId extends AbstractKey
{
    // Object interface

    public String toString()
    {
        return String.format("a%s", key);
    }

    @Override
    public int hashCode()
    {
        return key * 9987001;
    }

    // Comparable interface

    @Override
    public int compareTo(AbstractKey key)
    {
        int c = super.compareTo(key);
        if (c == 0) {
            AccountId that = (AccountId) key;
            long diff = (long) this.key - (long) that.key;
            return diff < 0 ? -1 : diff == 0 ? 0 : 1;
        } else {
            return c;
        }
    }

    // Transferrable interface

    public void readFrom(ByteBuffer buffer) throws BufferUnderflowException
    {
        super.readFrom(buffer);
        key = buffer.getInt();
    }

    public void writeTo(ByteBuffer buffer) throws BufferOverflowException
    {
        super.writeTo(buffer);
        buffer.putInt(key);
    }

    // AbstractKey interface

    @Override
    public int estimatedSizeBytes()
    {
        return 4;
    }

    @Override
    public AbstractKey copy()
    {
        return new AccountId(this);
    }

    // AccountId interface

    public int key()
    {
        return key;
    }

    public void key(int key)
    {
        this.key = key;
    }

    public AccountId()
    {
        erdoId(1);
    }
    
    public AccountId(int key)
    {
        this();
        key(key);
    }
    
    // For use by this package
    
    AccountId(AccountId accountId)
    {
        super(accountId);
        this.key = accountId.key;
    }

    // Object state

    private int key;
}
