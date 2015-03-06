/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.examples.helloworld;

import com.geophile.erdo.AbstractRecord;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class Person extends AbstractRecord<Name>
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("%s: %s", key(), birthDate);
    }

    // AbstractRecord interface

    @Override
    public long estimatedSizeBytes()
    {
        return super.estimatedSizeBytes() + birthDate.length() + 4; // birthDate length
    }

    @Override
    public void readFrom(ByteBuffer buffer) throws BufferUnderflowException
    {
        super.readFrom(buffer);
        int size = buffer.getInt();
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        birthDate = new String(bytes);
    }

    @Override
    public void writeTo(ByteBuffer buffer) throws BufferOverflowException
    {
        super.writeTo(buffer);
        byte[] bytes = birthDate.getBytes();
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }

    @Override
    public Person copy()
    {
        return new Person(key().name, birthDate);
    }

    // Person interface

    public Person(String name, String birthDate)
    {
        super(new Name(name));
        this.birthDate = birthDate;
    }

    public Person()
    {}

    // Object state

    String birthDate;
}
