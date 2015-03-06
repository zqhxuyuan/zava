/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.segmentfilemanager.pagememorymanager;

import com.geophile.erdo.Configuration;

import java.nio.ByteBuffer;

public abstract class PageMemoryManager
{
    // PageMemoryManager interface

    public abstract ByteBuffer takePageBuffer();

    public abstract void returnPageBuffer(ByteBuffer pageBuffer);

    // For testing
    public abstract void reset();

    // For use by subclasses

    protected PageMemoryManager(Configuration configuration)
    {
        this.pageSize = configuration.diskPageSizeBytes();
        this.cacheSize = configuration.diskCacheSizeBytes();
    }

    // Object state

    protected final int pageSize;
    protected final long cacheSize;
}
