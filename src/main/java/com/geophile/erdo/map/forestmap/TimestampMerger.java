/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.forestmap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.mergescan.Merger;

public class TimestampMerger implements Merger
{
    public static TimestampMerger only()
    {
        return ONLY;
    }

    public Side merge(AbstractKey left, AbstractKey right)
    {
        assert left.compareTo(right) == 0;
        long leftTimestamp = left.transactionTimestamp();
        long rightTimestamp = right.transactionTimestamp();
        assert leftTimestamp != rightTimestamp : String.format("%s, %s", left, right);
        return leftTimestamp < rightTimestamp ? Side.RIGHT : Side.LEFT;
    }

    private TimestampMerger()
    {
    }

    private static final TimestampMerger ONLY = new TimestampMerger();
}
