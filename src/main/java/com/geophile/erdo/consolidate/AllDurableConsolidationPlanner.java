/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import java.util.List;

import static com.geophile.erdo.consolidate.Consolidation.Element;

public class AllDurableConsolidationPlanner extends ConsolidationPlanner
{
    // ConsolidationPlanner interface

    @Override
    public boolean planConsolidation(Element newElement)
    {
        assert newElement == null : newElement;
        consolidationElements = consolidationSet.durable().availableForConsolidation();
        long totalCount = 0;
        for (Element element : consolidationElements) {
            totalCount += element.count();
        }
        if (totalCount == 0) {
            consolidationElements = null;
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
        return "allDurable";
    }

    // AllDurableConsolidationPlanner interface

    public static AllDurableConsolidationPlanner newPlanner(ConsolidationSet consolidationSet)
    {
        return new AllDurableConsolidationPlanner(consolidationSet);
    }

    // For use by this class

    private AllDurableConsolidationPlanner(ConsolidationSet consolidationSet)
    {
        super(consolidationSet, true, true);
    }

    // Object state

    private List<Element> consolidationElements;
}
