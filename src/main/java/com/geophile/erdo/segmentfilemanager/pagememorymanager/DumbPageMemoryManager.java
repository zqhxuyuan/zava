/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.segmentfilemanager.pagememorymanager;

import com.geophile.erdo.Configuration;

import java.nio.ByteBuffer;

public class DumbPageMemoryManager extends PageMemoryManager
{
    // PageMemoryManager interface


    @Override
    public ByteBuffer takePageBuffer()
    {
        return ByteBuffer.wrap(new byte[pageSize]);
    }

    @Override
    public void returnPageBuffer(ByteBuffer pageBuffer)
    {
    }

    @Override
    public void reset()
    {
    }

    // DumbPageMemoryManager interface

    public DumbPageMemoryManager(Configuration configuration)
    {
        super(configuration);
    }
}
