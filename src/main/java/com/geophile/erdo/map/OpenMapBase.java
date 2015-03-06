/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.DeadlockException;
import com.geophile.erdo.TransactionRolledBackException;

import java.io.IOException;

/**
 * Base class for open maps.
 */
public abstract class OpenMapBase extends MapBase implements OpenMap
{
    // OpenMap interface

    public abstract LazyRecord put(AbstractRecord record, boolean returnReplaced)
        throws IOException, InterruptedException, DeadlockException, TransactionRolledBackException;

    public abstract MapCursor cursor(AbstractKey key, boolean singleKey)
        throws IOException, InterruptedException;

    // For use by subclasses

    protected OpenMapBase(Factory factory)
    {
        super(factory);
    }
}
