/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import java.util.HashMap;
import java.util.Map;

// Synchronization is by user

class UsageCounts
{
    public void register(Long id)
    {
        Integer count = counts.get(id);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        counts.put(id, count);
    }

    // Returns true if count goes to 0 and id is removed from counts.
    public boolean unregister(Long id)
    {
        boolean removed;
        Integer count = counts.get(id);
        assert count != null : id;
        if (count == 1) {
            counts.remove(id);
            removed = true;
        } else {
            assert count > 1 : count;
            counts.put(id, count - 1);
            removed = false;
        }
        return removed;
    }

    public boolean unused(Long id)
    {
        return counts.get(id) == null;
    }

    private final Map<Long, Integer> counts = new HashMap<Long, Integer>(); // Consolidation element id -> count
}
