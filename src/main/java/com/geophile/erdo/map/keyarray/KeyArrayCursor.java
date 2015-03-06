/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.keyarray;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.map.KeyOnlyRecord;
import com.geophile.erdo.map.MapCursor;

public class KeyArrayCursor extends MapCursor
{
    // MapCursor interface

    public AbstractRecord next()
    {
        return neighbor(true);
    }

    @Override
    public AbstractRecord previous()
    {
        return neighbor(false);
    }

    public void close()
    {
        if (state != State.DONE) {
            current = keys.size();
            keys = null;
            super.close();
        }
    }

    // KeyArrayCursor interface

    KeyArrayCursor(KeyArray keys, AbstractKey startKey)
    {
        super(startKey, false);
        this.keys = keys;
    }

    // For use by this class

    private AbstractRecord neighbor(boolean forward)
    {
        AbstractKey next = null;
        switch (state) {
            case NEVER_USED:
                if (startKey == null) {
                    current = forward ? 0 : keys.size() - 1;
                } else {
                    current = keys.binarySearch(startKey);
                    if (current < 0) {
                        current = 
                            forward 
                            ? -current - 1
                            : -current - 2;
                    }
                }
                state = State.IN_USE;
                break;
            case IN_USE:
                if (forward) {
                    current++;
                } else {
                    current--;
                }
                break;
            case DONE:
                return null;
        }
        if (current >= 0 && current < keys.size()) {
            // Why null is passed to keys.key: We could have currentKey be a field, and then reuse the
            // key. But we're returning a KeyOnlyRecord containing a key. If multiple KeyOnlyRecords
            // wrap the same AbstractKey object, that's bad. null forces allocation of a new key.
            AbstractKey currentKey = keys.key(current, null);
            if (isOpen(currentKey)) {
                next = currentKey;
            } else {
                close();
            }
        } else {
            close();
        }
        return next == null ? null : new KeyOnlyRecord(next);
    }

    // Object state

    private KeyArray keys;
    private int current;
}
