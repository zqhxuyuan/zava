/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.map.LazyRecord;

public class TreePositionPool extends LazyRecord.Pool
{
    @Override
    public void activate(LazyRecord lazyRecord)
    {
        ((TreePosition) lazyRecord).activateForPool();
    }

    @Override
    public void deactivate(LazyRecord lazyRecord)
    {
        ((TreePosition) lazyRecord).deactivateForPool();
    }

    @Override
    public TreePosition newResource()
    {
        return new TreePosition(this);
    }
}
