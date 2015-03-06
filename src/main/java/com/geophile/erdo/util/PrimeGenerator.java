/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import static java.lang.Math.sqrt;

public class PrimeGenerator
{
    public static void main(String[] args)
    {
        new PrimeGenerator(args).run();
    }

    private PrimeGenerator(String[] args)
    {
        int a = 0;
        min = Integer.parseInt(args[a++]);
        count = Integer.parseInt(args[a++]);
    }

    private void run()
    {
        long start = System.currentTimeMillis();
        byte[] sieve = new byte[1 + MAX / 8];
        int limit = (int) sqrt(MAX) + 1;
        for (int x = 2; x <= limit; x++) {
            boolean xPrime = (sieve[(x / 8)] & MASK[x % 8]) == 0;
            if (xPrime) {
                int y = 2 * x;
                while (y < MAX && y > 0) {
                    sieve[y / 8] |= MASK[y % 8];
                    y += x;
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(String.format("%s msec", end - start));
        int found = 0;
        int x = min;
        while (found < count && x <= MAX && x > 0) {
            if ((sieve[x / 8] & MASK[x % 8]) == 0) {
                System.out.println(x);
                found++;
            }
            x++;
        }
    }

    private static final int[] MASK = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80};
    private static final int MAX = Integer.MAX_VALUE;

    private final int min;
    private final int count;
}
