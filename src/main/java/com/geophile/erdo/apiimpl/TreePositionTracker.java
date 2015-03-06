/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.apiimpl;

import com.geophile.erdo.Cursor;
import com.geophile.erdo.map.diskmap.tree.TreePosition;
import com.geophile.erdo.util.IdentitySet;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

// Tracks TreePositions used to implement some operation. Any TreePositions left by the end of the operation
// are reclaimed (returned to the pool) by destroyRemainingTreePositions. Usually, the operation in question
// is a Cursor operation, e.g. next. But there are exceptions, e.g. recovery and consolidation. In these
// cases, the context is null.

public class TreePositionTracker
{
    public static void registerTreePosition(Cursor context, TreePosition treePosition)
    {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.log(Level.FINEST, "Register TreePosition {0} -> {1})", new Object[]{context, treePosition});
        }
        IdentityHashMap<Cursor, IdentitySet<TreePosition>> threadTreePositions = TREE_POSITIONS.get();
        if (threadTreePositions == null) {
            threadTreePositions = new IdentityHashMap<>(); 
            TREE_POSITIONS.set(threadTreePositions);
        }
        IdentitySet<TreePosition> contextTreePositions = threadTreePositions.get(context);
        if (contextTreePositions == null) {
            contextTreePositions = new IdentitySet<>();
            threadTreePositions.put(context, contextTreePositions);
        }
        TreePosition replaced = contextTreePositions.add(treePosition);
        assert replaced == null : context;
    }

    public static void unregisterTreePosition(Cursor context, TreePosition treePosition)
    {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.log(Level.FINEST, "Unregister TreePosition {0} -> {1}", new Object[]{context, treePosition});
        }
        IdentityHashMap<Cursor, IdentitySet<TreePosition>> threadTreePositions = TREE_POSITIONS.get();
        assert threadTreePositions != null : treePosition;
        IdentitySet<TreePosition> contextTreePositions = threadTreePositions.get(context);
        assert contextTreePositions != null : context;
        TreePosition removed = contextTreePositions.remove(treePosition);
        assert removed == treePosition
            : String.format("context: %s, treePosition: %s, removed: %s", context, treePosition, removed);
        if (contextTreePositions.isEmpty()) {
            threadTreePositions.remove(context);
        }
    }

    public static void destroyRemainingTreePositions(Cursor context)
    {
        IdentityHashMap<Cursor, IdentitySet<TreePosition>> threadTreePositions = TREE_POSITIONS.get();
        if (threadTreePositions != null) {
            IdentitySet<TreePosition> contextTreePositions = threadTreePositions.get(context);
            if (contextTreePositions != null) {
                // Need a copy because TreePosition.destroyRecordReference() removes an element 
                // from contextTreePositions.
                for (TreePosition treePosition : new ArrayList<>(contextTreePositions.values())) {
                    treePosition.destroyRecordReference();
                    if (LOG.isLoggable(Level.FINEST)) {
                        LOG.log(Level.FINEST, "Released TreePosition {0} -> {1}", 
                                new Object[]{context, treePosition});
                    }
                }
            }
        }
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(TreePositionTracker.class.getName());
    private static final ThreadLocal<IdentityHashMap<Cursor, IdentitySet<TreePosition>>> TREE_POSITIONS =
        new ThreadLocal<>();
}
