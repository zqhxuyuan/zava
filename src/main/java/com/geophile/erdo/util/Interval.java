/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

public class Interval implements Comparable<Interval>
{
    // Object interface

    public String toString()
    {
        return min == max ? Long.toString(min) : String.format("%s-%s", min, max);
    }

    // Comparable interface

    public int compareTo(Interval that)
    {
        int c;
        if (this.min <= that.min && that.max <= this.max ||
            that.min <= this.min && this.max <= that.max) {
            c = 0;
        } else if (this.max <= that.min) {
            c = -1;
        } else if (that.max <= this.min) {
            c = 1;
        } else {
            assert false;
            c = 0;
        }
        return c;
    }

    // Interval interface

    public long min()
    {
        return min;
    }

    public long max()
    {
        return max;
    }

    public Interval(long x)
    {
        this(x, x);
    }

    public Interval(long min, long max)
    {
        this.min = min;
        this.max = max;
    }

    // Object state

    private final long min;
    private final long max;
}
