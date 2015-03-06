/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.diskmap.tree.Tree;

import java.io.IOException;

class DiskMapCursor extends MapCursor
{
    // MapCursor interface

    @Override
    public LazyRecord next() throws IOException, InterruptedException
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
        if (treeLevelCursor != null) {
            treeLevelCursor.close();
            treeLevelCursor = null;
        }
    }

    @Override
    public void goToFirst() throws IOException, InterruptedException
    {
        super.goToFirst();
        treeLevelCursor(null).goToFirst();
    }

    @Override
    public void goToLast() throws IOException, InterruptedException
    {
        super.goToLast();
        treeLevelCursor(null).goToLast();
    }

    @Override
    public void goTo(AbstractKey key) throws IOException, InterruptedException
    {
        super.goTo(key);
        treeLevelCursor(key).goTo(key);
    }

    // DiskMapCursor interface

    DiskMapCursor(Tree tree, MapCursor treeLevelCursor, AbstractKey startKey, boolean singleKey)
    {
        super(startKey, singleKey);
        this.tree = tree;
        this.treeLevelCursor = treeLevelCursor;
    }

    // For use by this class

    private LazyRecord neighbor(boolean forward) throws IOException, InterruptedException
    {
        LazyRecord neighbor = null;
        if (treeLevelCursor != null) {
            neighbor = forward ? treeLevelCursor.next() : treeLevelCursor.previous();
            if (neighbor == null) {
                close();
            } else if (!isOpen(neighbor.key())) {
                neighbor.destroyRecordReference();
                neighbor = null;
                close();
            }
        }
        return neighbor;
    }

    private MapCursor treeLevelCursor(AbstractKey key) throws IOException, InterruptedException
    {
        if (treeLevelCursor == null) {
            treeLevelCursor = tree.cursor(key);
        }
        return treeLevelCursor;
    }

    // Object state

    private final Tree tree;
    private MapCursor treeLevelCursor;
}
