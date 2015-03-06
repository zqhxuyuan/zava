/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

import com.geophile.erdo.AbstractKey;
import com.geophile.erdo.map.LazyRecord;
import com.geophile.erdo.map.MapCursor;
import com.geophile.erdo.map.forestmap.TimestampMerger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MergeCursor extends MapCursor
{
    // Cursor interface

    @Override
    public LazyRecord next() throws IOException, InterruptedException
    {
        LazyRecord next = null;
        if (state != State.DONE) {
            if (!forward) {
                restartAtStartKey(true);
            }
            next = neighbor();
        }
        return next;
    }

    @Override
    public LazyRecord previous() throws IOException, InterruptedException
    {
        LazyRecord previous = null;
        if (state != State.DONE) {
            if (forward) {
                restartAtStartKey(false);
            }
            previous = neighbor();
        }
        return previous;
    }

    @Override
    public void goToFirst() throws IOException, InterruptedException
    {
        super.goToFirst();
        for (MapCursor input : inputs) {
            input.goToFirst();
        }
        start();
    }

    @Override
    public void goToLast() throws IOException, InterruptedException
    {
        super.goToLast();
        for (MapCursor input : inputs) {
            input.goToLast();
        }
        start();
    }

    @Override
    public void goTo(AbstractKey key) throws IOException, InterruptedException
    {
        super.goTo(key);
        for (MapCursor input : inputs) {
            input.goTo(key);
        }
        start();
    }

    @Override
    protected boolean isOpen(AbstractKey key)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close()
    {
        if (state != State.DONE) {
            super.close();
            if (inputs != null) {
                for (MapCursor input : inputs) {
                    input.close();
                }
                inputs = null;
                root = null;
            }
        }
    }

    // MergeCursor interface

    public void addInput(MapCursor input)
    {
        inputs.add(input);
    }

    public void start() throws IOException, InterruptedException
    {
        // Number of nodes at leaf level is the smallest power of 2 >= inputs.size().
        int nLeaves = 1;
        while (nLeaves < inputs.size()) {
            nLeaves *= 2;
        }
        int nNodes = 2 * nLeaves - 1;
        firstLeaf = nNodes / 2;
        // Create tree
        root = createNode(0);
        // Move records up the tree
        root.prime();
    }

    public MergeCursor(AbstractKey startKey, boolean forward)
    {
        this(TimestampMerger.only(), startKey, forward);
    }

    // For use by this package

    Node mergeNode(int position, Node left, Node right, boolean forward)
    {
        return new MergeNode(this, position, left, right, forward);
    }

    Node inputNode(int position, MapCursor input, boolean forward)
    {
        return new InputNode(position, input, forward);
    }

    Node fillerNode(int position)
    {
        return new FillerNode(position);
    }

    MergeCursor(Merger merger, AbstractKey startKey, boolean forward)
    {
        super(startKey, false);
        this.merger = merger;
        this.forward = forward;
    }

    // For use by this class

    private Node createNode(int position)
    {
        return
            position < firstLeaf
            ? mergeNode(position, createNode(2 * position + 1), createNode(2 * position + 2), forward)
            : position < firstLeaf + inputs.size()
              ? inputNode(position, inputs.get(position - firstLeaf), forward)
              : fillerNode(position);
    }

    private void restartAtStartKey(boolean forward) throws IOException, InterruptedException
    {
        this.forward = forward;
        if (startKey == null) {
            if (unboundStartAtFirstKey) {
                goToFirst();
            } else {
                goToLast();
            }
        } else {
            goTo(startKey);
            // Get past the startKey if it has already been visited.
            LazyRecord current = root.record;
            if (current != null && current.key().equals(startKey)) {
                neighbor();
            }
        }
    }

    private LazyRecord neighbor() throws IOException, InterruptedException
    {
        LazyRecord neighbor = null;
        switch (state) {
            case NEVER_USED:
                state = State.IN_USE;
                break;
            case IN_USE:
                root.promote();
                break;
            case DONE:
                break;
        }
        if (root != null) {
            neighbor = root.record;
        }
        if (neighbor == null) {
            close();
        } else {
            startKey = neighbor.key();
        }
        return neighbor;
    }

    // Object state

    final Merger merger;
    private boolean forward;
    private List<MapCursor> inputs = new ArrayList<>();
    private int firstLeaf;
    private Node root;
}
