/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.geophile.erdo.consolidate.Consolidation.Element;

public class ConsolidationSetSnapshot
{
    // Object interface

    @Override
    public String toString()
    {
        return String.format("ConsolidationSetSnapshot(%s)", snapshotId);
    }

    public List<Element> elements()
    {
        return snapshot;
    }

    // ConsolidationSetSnapshot interface

    public void cleanup()
    {
        LOG.log(Level.FINE, "Cleaning up {0}", this);
        owner.unregister(snapshot);
    }

    public ConsolidationSetSnapshot(ConsolidationSet owner, List<Element> snapshot)
    {
        LOG.log(Level.FINE, "Creating {0} of {1} using {2}", new Object[]{this, owner, snapshot});
        this.owner = owner;
        this.snapshot = snapshot;
    }

    // Class state

    private static final AtomicLong snapshotIdCounter = new AtomicLong(0L);
    private static final Logger LOG = Logger.getLogger(ConsolidationSetSnapshot.class.getName());

    // Object state

    private final long snapshotId = snapshotIdCounter.getAndIncrement();
    private final ConsolidationSet owner;
    private final List<Element> snapshot;
}
