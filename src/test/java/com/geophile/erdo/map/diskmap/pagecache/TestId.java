/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.pagecache;

class TestId
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("id(%s)", value);
    }

    @Override
    public int hashCode()
    {
        return value;
    }

    @Override
    public boolean equals(Object obj)
    {
        return
            obj != null &&
            obj instanceof TestId &&
            ((TestId)obj).value == value;
    }


    // TestId interface

    public int value()
    {
        return value;
    }

    public byte signature()
    {
        return (byte) signature;
    }

    public TestId(int value, int signature)
    {
        this.value = value;
        this.signature = (byte) (signature & 0xff);
    }

    // Object interface

    private final int value;
    private final byte signature;
}
