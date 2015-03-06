/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.immutableitemcache;

class Id
{
    public String toString()
    {
        return String.format("id(%s)", value);
    }

    public int hashCode()
    {
        return value;
    }

    public boolean equals(Object o)
    {
        return o != null && o instanceof Id && ((Id)o).value == value;
    }

    public int value()
    {
        return value;
    }

    public Id(int value)
    {
        this.value = value;
    }

    private final int value;
}
