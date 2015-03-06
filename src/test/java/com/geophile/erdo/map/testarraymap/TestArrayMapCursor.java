/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.testarraymap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;

import java.io.IOException;

public class TestArrayMapCursor extends MapCursor
{
    // MapCursor interface

    @Override
    public LazyRecord next() throws IOException, InterruptedException
    {
        return neighbor(true);
    }

    @Override
    public LazyRecord previous() throws IOException, InterruptedException
    {
        return neighbor(false);
    }

    // ArrayMapCursor interface

    TestArrayMapCursor(TestArrayMap map, AbstractKey startKey, boolean singleKey)
    {
        super(startKey, singleKey);
        this.map = map;
        if (startKey == null) {
            this.position = 0;
        } else {
            this.position = map.keys.binarySearch(startKey);
            if (this.position < 0) {
                this.position = -this.position - 1;
            }
        }
    }

    // For use by this class

    private LazyRecord neighbor(boolean forward) throws IOException, InterruptedException
    {
        LazyRecord neighbor = null;
        switch (state) {
            case NEVER_USED:
                if (startKey == null) {
                    position = forward ? 0 : (int) map.recordCount() - 1;
                } else {
                    position = binarySearch(startKey);
                    if (position < 0) {
                        position = forward ? -position - 1 : -position - 2;
                    }
                }
                state = State.IN_USE;
                break;
            case IN_USE:
                position += forward ? 1 : -1;
                break;
            case DONE:
                return null;
        }
        if (position >= 0 && position < map.recordCount()) {
            neighbor = map.records.get(position);
            if (!isOpen(neighbor.key())) {
                neighbor = null;
            }
        }
        if (neighbor == null) {
            close();
        }
        return neighbor;
    }

    // Adapted from java.util.Arrays.binarySearch0
    private int binarySearch(AbstractKey key) throws IOException, InterruptedException
    {
        int low = 0;
        int high = map.records.size() - 1;
        AbstractKey midKey;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            midKey = map.records.get(mid).key();
            int c = midKey.compareTo(key);
            if (c < 0) {
                low = mid + 1;
            } else if (c > 0) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1);  // key not found.
    }

    // Object state

    private final TestArrayMap map;
    private int position;
}
