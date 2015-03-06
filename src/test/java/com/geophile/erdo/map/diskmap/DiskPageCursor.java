/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.AbstractRecord;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;

import java.io.IOException;

class DiskPageCursor extends MapCursor
{
    @Override
    public AbstractRecord next() throws IOException, InterruptedException
    {
        return neighbor(true);
    }

    @Override
    public LazyRecord previous() throws IOException, InterruptedException
    {
        return neighbor(false);
    }

    @Override
    public void close()
    {
        page = null;
        pageAccessBuffers = null;
    }

    DiskPageCursor(DiskPage page)
    {
        super(null, false);
        this.page = page;
        this.pageAccessBuffers = page.accessBuffers();
        this.recordNumber = 0;
    }

    private AbstractRecord neighbor(boolean forward)
    {
        AbstractRecord neighbor = null;
        if (page != null && recordNumber >= 0 && recordNumber < page.nRecords()) {
            neighbor = page.readRecord(recordNumber, pageAccessBuffers);
            if (forward) {
                recordNumber++;
            } else {
                recordNumber--;
            }
        }
        return neighbor;
    }

    private DiskPage page;
    private DiskPage.AccessBuffers pageAccessBuffers;
    private int recordNumber;
}
