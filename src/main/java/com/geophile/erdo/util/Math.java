/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

public class Math
{
    public static int ceilLog2(long x)
    {
        int log2;
        // Loop won't work if x = 0x8000000000000000L
        if (x == 0x8000000000000000L) {
            log2 = 63;
        } else {
            log2 = 0;
            long p = 1;
            while (p < x) {
                log2++;
                p <<= 1;
            }
        }
        return log2;
    }

    public static double log2(long x)
    {
        return java.lang.Math.log(x) / LOG2;
    }

    private static final double LOG2 = java.lang.Math.log(2);
}
