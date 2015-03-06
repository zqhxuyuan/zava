/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import com.geophile.erdo.util.Interval;

import java.util.*;

import static com.geophile.erdo.consolidate.Consolidation.Element;

// Make non-durable consolidation elements durable for the first time. This has to be done in
// timestamp order. To do this find the maximal set of non-durable elements such that:
// - The minimum timestamp is one greater than the maximum already durable timestamp.
// - The sequence of timestamps has no holes.

public class MakeDurableConsolidationPlanner extends ConsolidationPlanner
{
    // ConsolidationPlanner interface

    @Override
    public boolean planConsolidation(Element newElement)
    {
        assert newElement == null : newElement;
        // Load a priority queue with (Interval, Element) for non-durable elements.
        List<Element> nonDurable = consolidationSet.nonDurable().availableForConsolidation();
        if (nonDurable.isEmpty()) {
            consolidationElements = null;
        } else {
            PriorityQueue<IntervalAndOwner> intervals = new PriorityQueue<>(nonDurable.size());
            for (Element element : nonDurable) {
                for (Interval interval : element.timestamps()) {
                    intervals.add(new IntervalAndOwner(interval, element));
                }
            }
            // Merge the intervals, stopping when a gap is found. A gap immediately following
            // the max durable timestamps is also grounds for stopping. As intervals are processed,
            // add the owning    element to the consolidation candidates.
            long maxTimestampSoFar = maxDurableTimestamp();
            Set<Element> consolidationElementSet = new HashSet<>();
            IntervalAndOwner intervalAndOwner;
            while ((intervalAndOwner = intervals.poll()) != null &&
                   intervalAndOwner.min() == maxTimestampSoFar + 1) {
                consolidationElementSet.add(intervalAndOwner.owner());
                maxTimestampSoFar = intervalAndOwner.max();
            }
            // See if we have enough to bother writing to disk.
            long size = 0;
            for (Element element : consolidationElementSet) {
                size += element.sizeBytes();
            }
            consolidationElements =
                size < minConsolidationSize
                ? null
                : new ArrayList<>(consolidationElementSet);
        }
        return consolidationElements != null;
    }

    @Override
    public List<Element> elementsToConsolidate()
    {
        return consolidationElements;
    }

    @Override
    public String type()
    {
        return "makeDurable";
    }

    // MakeDurableConsolidationPlanner interface

    public static MakeDurableConsolidationPlanner newPlanner(ConsolidationSet consolidationSet)
    {
        return new MakeDurableConsolidationPlanner(consolidationSet);
    }

    // For use by this class

    private long maxDurableTimestamp()
    {
        long maxTimestamp = -1L;
        for (Element element : consolidationSet().durable().elements()) {
            long maxElementTimestamp = element.timestamps().maxTimestamp();
            if (maxElementTimestamp > maxTimestamp) {
                maxTimestamp = maxElementTimestamp;
            }
        }
        return maxTimestamp;
    }

    private MakeDurableConsolidationPlanner(ConsolidationSet consolidationSet)
    {
        super(consolidationSet, false, true);
        minConsolidationSize = consolidationSet.configuration().consolidationMinSizeBytes();
    }

    // Object state

    private final long minConsolidationSize;
    private List<Element> consolidationElements;

    // Inner classes

    private static class IntervalAndOwner extends Interval
    {
        IntervalAndOwner(Interval interval, Element owner)
        {
            super(interval.min(), interval.max());
            this.owner = owner;
        }

        Element owner()
        {
            return owner;
        }

        private final Element owner;
    }
}
