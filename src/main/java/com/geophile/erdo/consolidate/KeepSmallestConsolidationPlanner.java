/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.geophile.erdo.consolidate.Consolidation.Element;
import static java.lang.Math.abs;
import static java.lang.Math.max;

abstract class KeepSmallestConsolidationPlanner extends ConsolidationPlanner
{
    // ConsolidationPlanner interface

    @Override
    public String type()
    {
        return "keepSmallest";
    }

    @Override
    public boolean planConsolidation(Element newElement)
    {
        assert newElement == null : newElement;
        PriorityQueue<PartialConsolidation> pq = new PriorityQueue<PartialConsolidation>();
        List<Element> consolidationCandidates = consolidationCandidates();
        long candidateRecords = 0;
        for (Element element : consolidationCandidates) {
            pq.add(new Single(element));
            candidateRecords += element.count();
        }
        // Consolidate as much as possible
        while (pq.size() > 1) {
            PartialConsolidation first = pq.poll();
            PartialConsolidation second = pq.poll();
            if (consolidate(first, second)) {
                pq.add(new Double(first, second));
            } else {
                // Keep the one with the most trees. Do consolidations of other, smaller
                // elements, and maybe the selected PartialConsolidation will continue to be
                // combined later.
                pq.add(mostTrees(first, second));
            }
        }
        // Gather elements to be consolidated and see if there's actually anything to be done.
        consolidationElements = new ArrayList<Element>();
        PartialConsolidation consolidation = pq.poll();
        if (consolidation == null) {
            consolidationElements = null;
        } else {
            consolidation.collectElements(consolidationElements);
            int nElements = consolidationElements.size();
            if (nElements < minMaps || consolidation.size() < minSize) {
                if (LOG.isLoggable(Level.FINER)) {
                    consolidationSet.describe(LOG, Level.FINER, "skip keepSmallest consolidation");
                    LOG.log(Level.FINER,
                            "skip keepSmallest consolidation: {0} element{1}, {2} records",
                            new Object[]{nElements, nElements == 1 ? "" : "s", consolidation.records()}) ;
                }
                consolidationElements = null;
            } else {
                if (LOG.isLoggable(Level.FINE)) {
                    consolidationSet.describe(LOG, Level.FINE, "keepSmallest planning");
                    LOG.log(Level.FINE,
                            "keepSmallest planning {0} / {1}: " +
                            "Consolidation candidates: {2}, consolidation: {3}",
                            new Object[]{consolidation.records(),
                                         candidateRecords,
                                         consolidationCandidates,
                                         consolidationElements});
                }
            }
        }
        return consolidationElements != null;
    }

    @Override
    public List<Element> elementsToConsolidate()
    {
        return consolidationElements;
    }

    // KeepSmallestConsolidationPlanner interface

    public static KeepSmallestConsolidationPlanner durable(ConsolidationSet consolidationSet,
                                                           int minConsolidationMaps,
                                                           int minConsoldiationSize)
    {
        return new Durable(consolidationSet, minConsolidationMaps, minConsoldiationSize);
    }

    public static KeepSmallestConsolidationPlanner nonDurable(ConsolidationSet consolidationSet,
                                                              int minConsolidationMaps,
                                                              int minConsoldiationSize)
    {
        return new NonDurable(consolidationSet, minConsolidationMaps, minConsoldiationSize);
    }

    // For use by subclasses

    protected abstract List<Element> consolidationCandidates();

    protected KeepSmallestConsolidationPlanner(ConsolidationSet consolidationSet,
                                               boolean inputDurable,
                                               boolean outputDurable,
                                               int minConsolidationMaps,
                                               int minConsolidationSize)
    {
        super(consolidationSet, inputDurable, outputDurable);
        minMaps = max(minConsolidationMaps, 1);
        minSize = minConsolidationSize;
    }

    // For use by this class

    private PartialConsolidation mostTrees(PartialConsolidation x, PartialConsolidation y)
    {
        return
            x == null ? y :
            y == null ? x :
            x.trees() < y.trees() ? y : x;
    }

    private boolean consolidate(PartialConsolidation x, PartialConsolidation y)
    {
        long xSize = x.size();
        long ySize = y.size();
        return ((float) abs(xSize - ySize)) / max(xSize, ySize) <= THRESHOLD;
    }

    // Class state
    
    private static final float THRESHOLD = 0.50f;
    private static final Logger LOG = Logger.getLogger(KeepSmallestConsolidationPlanner.class.getName());
    
    // Object state
    
    private final int minMaps;
    private final long minSize;
    private List<Element> consolidationElements;

    // Inner classes

    private static class Durable extends KeepSmallestConsolidationPlanner
    {
        @Override
        protected List<Element> consolidationCandidates()
        {
            return consolidationSet.durable().availableForConsolidation();
        }

        public Durable(ConsolidationSet consolidationSet,
                       int minConsolidationMaps,
                       int minConsolidationSize)
        {
            super(consolidationSet, true, true, minConsolidationMaps, minConsolidationSize);
        }
    }

    private static class NonDurable extends KeepSmallestConsolidationPlanner
    {
        @Override
        protected List<Element> consolidationCandidates()
        {
            return consolidationSet.nonDurable().availableForConsolidation();
        }

        public NonDurable(ConsolidationSet consolidationSet,
                          int minConsolidationMaps,
                          int minConsolidationSize)
        {
            super(consolidationSet, false, false, minConsolidationMaps, minConsolidationSize);
        }
    }

    private static abstract class PartialConsolidation implements Comparable<PartialConsolidation>
    {
        public String toString()
        {
            return Long.toString(records());
        }

        public int compareTo(PartialConsolidation that)
        {
            long thisSize = this.size();
            long thatSize = that.size();
            return thisSize < thatSize ? -1 : thisSize > thatSize ? 1 : 0;
        }

        public int trees()
        {
            return trees;
        }

        public long records()
        {
            return records;
        }

        public long size()
        {
            return size;
        }

        public abstract void collectElements(List<Element> elements);

        protected PartialConsolidation(int trees, long records, long size)
        {
            this.trees = trees;
            this.records = records;
            this.size = size;
        }

        protected final int trees;
        protected final long records;
        protected final long size;
    }
    
    private static class Single extends PartialConsolidation
    {
        @Override
        public void collectElements(List<Element> elements)
        {
            elements.add(element);
        }

        public Single(Element element)
        {
            super(1, element.count(), element.sizeBytes());
            this.element = element;
        }
        
        private final Element element;
    }
    
    private static class Double extends PartialConsolidation
    {
        @Override
        public void collectElements(List<Element> elements)
        {
            left.collectElements(elements);
            right.collectElements(elements);
        }

        public Double(PartialConsolidation left, PartialConsolidation right)
        {
            super(left.trees + right.trees, 
                  left.records + right.records,
                  left.size + right.size);
            this.left = left;
            this.right = right;
        }
        
        private final PartialConsolidation left;
        private final PartialConsolidation right;
    }
}
