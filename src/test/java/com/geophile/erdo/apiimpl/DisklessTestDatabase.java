/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.apiimpl;

import com.geophile.erdo.forest.Forest;
import com.geophile.erdo.map.Factory;

import java.io.IOException;

public class DisklessTestDatabase extends DatabaseImpl
{
    public DisklessTestDatabase(Factory factory) throws IOException, InterruptedException
    {
        super(factory);
        forest = Forest.create(this);
        factory.transactionManager(forest);
    }
}
