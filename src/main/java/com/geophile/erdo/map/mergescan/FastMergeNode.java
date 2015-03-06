/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.AbstractKey;

import java.io.IOException;

class FastMergeNode extends FastNode
{
    public String toString()
    {
        return String.format("FastMergeNode(#%s)", position);
    }

    public void prime() throws IOException, InterruptedException
    {
        left.prime();
        right.prime();
        promote();
    }

    public void fastPromote() throws IOException, InterruptedException
    {
        if (left.key != null && right.key != null) {
            int c = compareChildKeys();
            if ((c & LEFT_BEFORE_RIGHT) != 0) {
                key = left.key;
                record = left.record;
                left.promote();
            } else if ((c & RIGHT_BEFORE_LEFT) != 0) {
                key = right.key;
                record = right.record;
                right.promote();
            } else {
                if ((c & KEY_RANGE) != 0) {
                    left.goSlow();
                    right.goSlow();
                    promote();
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

    public FastMergeNode(MergeCursor mergeCursor, int position, FastNode left, FastNode right, boolean forward)
    {
        super(position, forward);
        this.mergeCursor = mergeCursor;
        this.left = left;
        this.right = right;
    }

    private int compareChildKeys()
    {
        AbstractKey leftKey = left.key;
        AbstractKey rightKey = right.key;
        boolean isLeftRange = leftKey instanceof MultiRecordKey;
        boolean isRightRange = rightKey instanceof MultiRecordKey;
        int c;
        if (isLeftRange == isRightRange) {
            c = leftKey.compareTo(rightKey);
        } else if (isLeftRange) {
            MultiRecordKey leftMultiRecordKey = (MultiRecordKey) leftKey;
            c = leftMultiRecordKey.hi() != null && leftMultiRecordKey.hi().compareTo(rightKey) <= 0 ? -1 :
                leftMultiRecordKey.lo() != null && leftMultiRecordKey.lo().compareTo(rightKey) > 0 ? 1 : 0;
        } else /* isRightRange */ {
            MultiRecordKey rightMultiRecordKey = (MultiRecordKey) rightKey;
            c = rightMultiRecordKey.lo() != null && leftKey.compareTo(rightMultiRecordKey.lo()) < 0 ? -1 :
                rightMultiRecordKey.hi() != null && leftKey.compareTo(rightMultiRecordKey.hi()) >= 0 ? 1 : 0;
        }
        if (!forward) {
            c = -c;
        }
        boolean leftBeforeRight = c < 0;
        boolean rightBeforeLeft = c > 0;
        return
            (isLeftRange || isRightRange ? KEY_RANGE : 0) |
            (leftBeforeRight ? LEFT_BEFORE_RIGHT : 0) |
            (rightBeforeLeft ? RIGHT_BEFORE_LEFT : 0);
    }

    // Class state

    private static final int KEY_RANGE = 0x1;
    private static final int LEFT_BEFORE_RIGHT = 0x2;
    private static final int RIGHT_BEFORE_LEFT = 0x4;

    // Object state

    private final MergeCursor mergeCursor;
    private FastNode left;
    private FastNode right;
}
