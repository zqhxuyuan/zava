/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import java.io.IOException;

class MergeNode extends Node
{
    public String toString()
    {
        return String.format("MergeNode(#%s)", position);
    }

    public void prime() throws IOException, InterruptedException
    {
        left.prime();
        right.prime();
        promote();
    }

    public void promote() throws IOException, InterruptedException
    {
        if (left.key != null && right.key != null) {
            int c = left.key.compareTo(right.key);
            if (!forward) {
                c = -c;
            }
            if (c < 0) {
                key = left.key;
                record = left.record;
                left.promote();
            } else if (c > 0) {
                key = right.key;
                record = right.record;
                right.promote();
            } else {
                Node keep = null;
                switch (mergeCursor.merger.merge(left.key, right.key)) {
                    case LEFT:
                        keep = left;
                        break;
                    case RIGHT:
                        keep = right;
                        break;
                }
                key = keep.key;
                record = keep.record;
                left.promote();
                right.promote();
            }
        } else if (left.key == null) {
            key = right.key;
            record = right.record;
            right.promote();
        } else {
            key = left.key;
            record = left.record;
            left.promote();
        }
    }

    @Override
    protected void dump(int level)
    {
        super.dump(level);
        left.dump(level + 1);
        right.dump(level + 1);
    }

    public MergeNode(MergeCursor mergeCursor, int position, Node left, Node right, boolean forward)
    {
        super(position, forward);
        this.mergeCursor = mergeCursor;
        this.left = left;
        this.right = right;
    }

    // Object state

    private final MergeCursor mergeCursor;
    private final Node left;
    private final Node right;
}
