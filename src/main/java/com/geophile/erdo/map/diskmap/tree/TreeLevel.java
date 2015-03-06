/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.diskmap.tree;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.diskmap.Manifest;
import com.geophile.erdo.util.LongArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TreeLevel
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("%s/L%s", tree, level);
    }

    // TreeLevel interface

    public int levelNumber()
    {
        return level;
    }

    public boolean isLeaf()
    {
        return level == 0;
    }

    public int segments()
    {
        return segments.size();
    }

    public TreeSegment segment(int segmentNumber)
    {
        return segments.get(segmentNumber);
    }

    public boolean leafLevelEmpty()
    {
        assert level == 0 : this;
        return segment(0).summary().keyCount() == 0;
    }

    public boolean keyPossiblyPresent(AbstractKey key)
    {
        assert level == 0 : this;
        TreeSegment segment = segmentPossiblyContaining(key);
        return segment != null && segment.keyPossiblyPresent(key);
    }

    public void destroy()
    {
        for (TreeSegment segment : segments) {
            segment.destroy();
        }
    }

    public static TreeLevel recover(Tree tree, int level, Manifest manifest)
        throws IOException, InterruptedException
    {
        TreeLevel treeLevel = new TreeLevel(tree, level);
        treeLevel.recover(manifest);
        return treeLevel;
    }

    // For use by this package

    Tree tree()
    {
        return tree;
    }

    // For use by subclasses

    protected TreeSegment lastSegment()
    {
        return segments.get(segments.size() - 1);
    }

    protected TreeLevel(Tree tree, int level)
    {
        this.tree = tree;
        this.level = level;
    }

    // For use by this class

    private void recover(Manifest manifest) throws IOException, InterruptedException
    {
        LongArray segmentIds = manifest.segmentIds(level);
        for (int s = 0; s < segmentIds.size(); s++) {
            TreeSegment segment = TreeSegment.recover(this, s, manifest);
            segments.add(segment);
        }
    }

    private TreeSegment segmentPossiblyContaining(AbstractKey key)
    {
        // Adapted from Arrays.binarySearch. Each segment records the last key actually present. The goal is to find
        // the leftmost segment whose leafLastKey >= key.
        int lo = 0;
        int hi = segments.size() - 1;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int c = segments.get(mid).leafLastKey().compareTo(key);
            if (c < 0) {
                lo = mid + 1;
            } else if (c > 0) {
                hi = mid - 1;
            } else {
                return segments.get(mid); // key found
            }
        }
        return lo == segments.size() ? null : segments.get(lo);
    }

    // Object state

    protected final Tree tree;
    protected final int level;
    protected final List<TreeSegment> segments = new ArrayList<TreeSegment>();
}
