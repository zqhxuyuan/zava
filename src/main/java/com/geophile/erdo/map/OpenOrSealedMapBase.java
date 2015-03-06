/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.AbstractRecord;

public abstract class OpenOrSealedMapBase extends SealedMapBase implements OpenOrSealedMap
{
    // OpenOrSealedMap interface

    public abstract LazyRecord put(AbstractRecord record, boolean returnReplaced);

    public abstract MapCursor cursor(AbstractKey startKey, boolean singleKey);

    public abstract long recordCount();

    // For use by subclasses

    protected OpenOrSealedMapBase(Factory factory)
    {
        super(factory);
    }
}
