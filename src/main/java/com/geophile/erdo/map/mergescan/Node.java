/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.LazyRecord;

import java.io.IOException;

abstract class Node
{
    public abstract void prime() throws IOException, InterruptedException;

    public abstract void promote() throws IOException, InterruptedException;

    public final void dump()
    {
        dump(0);
    }

    Node(int position, boolean forward)
    {
        this.position = position;
        this.forward = forward;
    }

    protected void dump(int level)
    {
        for (int i = 0; i < level; i++) {
            System.out.print("    ");
        }
        System.out.println(this);
    }

    // Object state

    protected final int position;
    protected final boolean forward;
    protected AbstractKey key = null;
    protected LazyRecord record = null;
}
