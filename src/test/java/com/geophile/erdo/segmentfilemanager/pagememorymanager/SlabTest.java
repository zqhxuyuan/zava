/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.segmentfilemanager.pagememorymanager;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static org.junit.Assert.*;

public class SlabTest
{
    @Test
    public void testInitialAllocations()
    {
        Slab slab = new Slab(0, SLAB_SIZE, PAGE_SIZE);
        int[] arrayOffsets = new int[PAGES];
        for (int p = 0; p < PAGES; p++) {
            ByteBuffer buffer = slab.takePageBuffer();
            assertNotNull(buffer);
            assertEquals(0, buffer.position());
            assertEquals(PAGE_SIZE, buffer.limit());
            arrayOffsets[p] = buffer.arrayOffset();
        }
        assertNull(slab.takePageBuffer());
        Arrays.sort(arrayOffsets);
        int expectedArrayOffset = 0;
        for (int b = 0; b < PAGES; b++) {
            assertEquals(expectedArrayOffset, arrayOffsets[b]);
            expectedArrayOffset += PAGE_SIZE;
        }
    }

    @Test
    public void testReturns()
    {
        Slab slab = new Slab(0, SLAB_SIZE, PAGE_SIZE);
        ByteBuffer[] pages = new ByteBuffer[PAGES];
        // Take all pages
        for (int p = 0; p < PAGES; p++) {
            pages[p] = slab.takePageBuffer();
        }
        // Put them back
        for (int p = 0; p < PAGES; p++) {
            slab.returnPageBuffer(pages[p]);
        }
        // Take them again
        int[] arrayOffsets = new int[PAGES];
        for (int p = 0; p < PAGES; p++) {
            ByteBuffer buffer = slab.takePageBuffer();
            assertNotNull(buffer);
            assertEquals(0, buffer.position());
            assertEquals(PAGE_SIZE, buffer.limit());
            arrayOffsets[p] = buffer.arrayOffset();
        }
        assertNull(slab.takePageBuffer());
        Arrays.sort(arrayOffsets);
        int expectedArrayOffset = 0;
        for (int b = 0; b < PAGES; b++) {
            assertEquals(expectedArrayOffset, arrayOffsets[b]);
            expectedArrayOffset += PAGE_SIZE;
        }
    }

    private final int SLAB_SIZE = 1000;
    private final int PAGE_SIZE = 10;
    private final int PAGES = SLAB_SIZE / PAGE_SIZE;
}
