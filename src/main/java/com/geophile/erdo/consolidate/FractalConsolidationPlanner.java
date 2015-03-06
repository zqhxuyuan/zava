/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import java.util.List;

import static com.geophile.erdo.consolidate.Consolidation.Element;

class FractalConsolidationPlanner extends ConsolidationPlanner
{
    // ConsolidationPlanner interface

    @Override
    public boolean planConsolidation(Element newElement)
    {
        keepSmallest.planConsolidation(newElement);
        if (keepSmallest.elementsToConsolidate() == null) {
            dropLargest.planConsolidation(newElement);
            consolidationElements = dropLargest.elementsToConsolidate();
        } else {
            consolidationElements = keepSmallest.elementsToConsolidate();
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
        return "fractal";
    }

    // FractalConsolidationPlanner interface

    public static FractalConsolidationPlanner durable(ConsolidationSet consolidationSet,
                                                      int minConsolidationMaps,
                                                      int minConsolidationSize)
    {
        return new FractalConsolidationPlanner
            (consolidationSet,
             KeepSmallestConsolidationPlanner.durable(consolidationSet,
                                                      minConsolidationMaps,
                                                      minConsolidationSize),
             DropLargestConsolidationPlanner.durable(consolidationSet,
                                                     minConsolidationMaps,
                                                     minConsolidationSize));
    }

    public static FractalConsolidationPlanner nonDurable(ConsolidationSet consolidationSet,
                                                         int minConsolidationMaps,
                                                         int minConsolidationSize)
    {
        return new FractalConsolidationPlanner
            (consolidationSet,
             KeepSmallestConsolidationPlanner.nonDurable(consolidationSet,
                                                         minConsolidationMaps,
                                                         minConsolidationSize),
             DropLargestConsolidationPlanner.nonDurable(consolidationSet,
                                                        minConsolidationMaps,
                                                        minConsolidationSize));
    }

    // For use by this class

    private FractalConsolidationPlanner(ConsolidationSet consolidationSet,
                                        KeepSmallestConsolidationPlanner keepSmallest,
                                        DropLargestConsolidationPlanner dropLargest)
    {
        super(consolidationSet, keepSmallest.inputDurable(), keepSmallest.outputDurable());
        assert keepSmallest.inputDurable() == dropLargest.inputDurable();
        assert keepSmallest.outputDurable() == dropLargest.outputDurable();
        this.keepSmallest = keepSmallest;
        this.dropLargest = dropLargest;
    }

    // Object state

    private final KeepSmallestConsolidationPlanner keepSmallest;
    private final DropLargestConsolidationPlanner dropLargest;
    private List<Element> consolidationElements;
}
