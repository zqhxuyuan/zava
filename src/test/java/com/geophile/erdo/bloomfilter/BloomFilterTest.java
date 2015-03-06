/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.bloomfilter;

import org.junit.Test;

public class BloomFilterTest
{
    @Test
    public void test()
    {
        test(1000 * 1000, 0.0001);
        test(1000 * 1000, 0.0005);
        test(1000 * 1000, 0.001);
        test(1000 * 1000, 0.005);
        test(1000 * 1000, 0.01);
        test(1000 * 1000, 0.05);
    }

    private void test(int records, double errorRate)
    {
        int bits = bits(records, errorRate);
        int hashFunctions = hashFunctions(bits, records);
        print("records: %s, errorRate: %s -> bits/record: %s, hashFunctions: %s",
              records, errorRate, (double)bits/records, hashFunctions);
        BloomFilter filter = new BloomFilter(records, errorRate);
        int collisions = 0;
        for (Integer key = 0; key < records; key++) {
            // Add to filter
            if (filter.maybePresent(key)) {
                collisions++;
            } else {
                filter.add(key);
            }
        }
        print("collisions: %s", collisions);
    }

    private int bits(int records, double errorRate)
    {
        return toInt(-records * Math.log(errorRate) / Math.pow(Math.log(2), 2));
    }

    private int hashFunctions(int bits, int records)
    {
        return toInt((bits / records) * Math.log(2));
    }

    private int toInt(double x)
    {
        return (int) (x + 0.5 * Math.signum(x));
    }

    private void print(String template, Object... values)
    {
        // System.out.println(String.format(template, values));
    }
}
