/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.immutableitemcache;

import java.io.IOException;

public interface ImmutableItemManager<ID, ITEM>
{
    ITEM getItemForCache(ID id) throws IOException, InterruptedException;
    void cleanupItemEvictedFromCache(ITEM item) throws IOException, InterruptedException;
}
