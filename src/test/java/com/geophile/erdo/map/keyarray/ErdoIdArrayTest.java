/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.keyarray;

import com.geophile.erdo.util.ErdoIdArray;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ErdoIdArrayTest
{
    @Test
    public void testEmpty()
    {
        ErdoIdArray a = new ErdoIdArray();
        try {
            a.at(0);
            fail();
        } catch (IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void testSingleErdoId()
    {
        final int MAX = 1000;
        final int ERDO_ID = 123;
        for (int n = 0; n < MAX; n++) {
            ErdoIdArray a = new ErdoIdArray();
            for (int i = 0; i < n; i++) {
                a.append(ERDO_ID);
            }
            for (int i = 0; i < n; i++) {
                assertEquals(ERDO_ID, a.at(i));
            }
        }
    }

    @Test
    public void testMultipleErdoIds()
    {
        final int MAX_ERDO_IDS = 20;
        for (int nErdoIds = 1; nErdoIds <= MAX_ERDO_IDS; nErdoIds++) {
            ErdoIdArray a = new ErdoIdArray();
            for (int erdoId = 1; erdoId <= nErdoIds; erdoId++) {
                for (int i = 0; i < erdoId; i++) {
                    a.append(erdoId);
                }
            }
            int p = 0;
            for (int erdoId = 1; erdoId <= nErdoIds; erdoId++) {
                for (int i = 0; i < erdoId; i++) {
                    assertEquals(erdoId, a.at(p++));
                }
            }
        }
    }

    @Test
    public void testRemove()
    {
        int[] expected = new int[]{1, 2, 2, 3, 3, 3, 1, 2, 2, 3, 3, 3};
        ErdoIdArray a = new ErdoIdArray();
        for (int x : expected) {
            a.append(x);
        }
        int n = expected.length;
        while (n >= 0) {
            for (int i = 0; i < n; i++) {
                assertEquals(expected[i], a.at(i));
            }
            n--;
            if (n > 0) {
                a.removeLast();
            }
        }
    }
}
