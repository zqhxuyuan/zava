/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.geophile.erdo.consolidate.Consolidation.Element;
import static java.lang.Math.max;

abstract class DropLargestConsolidationPlanner extends ConsolidationPlanner
{
    // ConsolidationPlanner interface

    @Override
    public String type()
    {
        return "dropLargest";
    }

    @Override
    public boolean planConsolidation(Element newElement)
    {
        assert newElement == null : newElement;
        consolidationElements = new ArrayList<Element>();
        // Candidates sorted with largest elements first
        List<Element> candidates = consolidationSet.nonDurable().availableForConsolidation();
        Collections.sort(candidates, LARGEST_ELEMENTS_FIRST);
        // Compute consolidation size assuming all candidates included.
        long maxConsolidationSize = 0;
        long candidateRecords = 0;
        for (Element element : candidates) {
            maxConsolidationSize += element.sizeBytes();
            candidateRecords += element.count();
        }
        // Remove large candidates and include what's left
        boolean discard = true;
        long consolidationRecords = 0;
        for (Element candidate : candidates) {
            if (discard) {
                if (includeCandidate(candidate, maxConsolidationSize)) {
                    discard = false;
                } else {
                    maxConsolidationSize -= candidate.sizeBytes();
                }
            }
            if (!discard) {
                consolidationElements.add(candidate);
                consolidationRecords += candidate.count();
            }
        }
        int nElements = consolidationElements.size();
        if (nElements < minMaps || maxConsolidationSize < minSize) {
            if (LOG.isLoggable(Level.FINER)) {
                consolidationSet.describe(LOG, Level.FINER, "skip dropLargest consolidation");
                LOG.log(Level.FINER,
                        "skip dropLargest consolidation: {0} element{1}, {2} records",
                        new Object[]{nElements, nElements == 1 ? "" : "s", consolidationRecords});
            }
            consolidationElements = null;
        } else {
            if (LOG.isLoggable(Level.FINE)) {
                consolidationSet.describe(LOG, Level.FINE, "dropLargest planning");
                LOG.log(Level.FINE,
                        "dropLargest planning {0} / {1}: " +
                        "Consolidation candidates: {0}, consolidation: {1}",
                        new Object[]{consolidationRecords,
                                     candidateRecords,
                                     consolidationElements,
                                     consolidationRecords});
            }
        }
        return consolidationElements != null;
    }

    @Override
    public List<Element> elementsToConsolidate()
    {
        return consolidationElements;
    }

    // FractalConsolidationPlanner interface

    public static DropLargestConsolidationPlanner durable(ConsolidationSet consolidationSet,
                                                          int minConsolidationMaps,
                                                          int minConsolidationSize)
    {
        return new Durable(consolidationSet, minConsolidationMaps, minConsolidationSize);
    }

    public static DropLargestConsolidationPlanner nonDurable(ConsolidationSet consolidationSet,
                                                             int minConsolidationMaps,
                                                             int minConsolidationSize)
    {
        return new NonDurable(consolidationSet, minConsolidationMaps, minConsolidationSize);
    }

    // For use by subclasses

    protected abstract List<Element> consolidationCandidates();

    protected DropLargestConsolidationPlanner(ConsolidationSet consolidationSet,
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

    private boolean includeCandidate(Element candidate, long consolidationSize)
    {
        return (float) candidate.sizeBytes() / consolidationSize <= THRESHOLD;
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(DropLargestConsolidationPlanner.class.getName());
    private static final float THRESHOLD = 0.5f;
    private static final Comparator<Element> LARGEST_ELEMENTS_FIRST =
        new Comparator<Element>()
        {
            public int compare(Element x, Element y)
            {
                long xSize = x.sizeBytes();
                long ySize = y.sizeBytes();
                return xSize < ySize ? 1 : xSize > ySize ? -1 : 0;
            }
        };

    // Object state

    private final int minMaps;
    private final long minSize;
    private List<Element> consolidationElements;

    // Inner classes

    private static class Durable extends DropLargestConsolidationPlanner
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

    private static class NonDurable extends DropLargestConsolidationPlanner
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
}
