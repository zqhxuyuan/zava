/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.emptymap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;

import java.io.IOException;

// EmptyMap has a specific purpose, to carry the timestamps of maps resulting from readonly transactions. This is
// the corresponding cursor class. It is also used in various places when a cursor over any empty map is needed.

public class EmptyMapCursor extends MapCursor
{
    // MapCursor interface

    @Override
    public LazyRecord next()
    {
        return null;
    }

    @Override
    public LazyRecord previous() throws IOException, InterruptedException
    {
        return null;
    }

    @Override
    public void goToFirst() throws IOException, InterruptedException
    {}

    @Override
    public void goToLast() throws IOException, InterruptedException
    {}

    @Override
    public void goTo(AbstractKey key) throws IOException, InterruptedException
    {}

    // EmptyMapCursor interface

    public EmptyMapCursor()
    {
        super(null, false);
    }
}
