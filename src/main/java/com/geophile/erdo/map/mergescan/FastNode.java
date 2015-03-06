/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.map.MapCursor;

import java.io.IOException;

abstract class FastNode extends Node
{
    public final void promote() throws IOException, InterruptedException
    {
        if (multiRecordScan == null) {
            fastPromote();
        } else {
            record = multiRecordScan.next();
            if (record == null) {
                multiRecordScan = null;
                fastPromote();
            }
        }
        if (record != null) {
            key = record.key();
        }
    }

    public abstract void fastPromote() throws IOException, InterruptedException;

    FastNode(int position, boolean forward)
    {
        super(position, forward);
        assert forward;
    }

    protected final void goSlow() throws IOException, InterruptedException
    {
        if (record instanceof AbstractMultiRecord) {
            multiRecordScan = ((AbstractMultiRecord) record).cursor();
            record = multiRecordScan.next();
            if (record != null) {
                key = record.key();
            }
        }
    }

    // Object state

    private MapCursor multiRecordScan;
}
