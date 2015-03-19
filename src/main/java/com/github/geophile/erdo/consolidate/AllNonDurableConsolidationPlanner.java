/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.geophile.erdo.consolidate;

import java.util.List;

public class AllNonDurableConsolidationPlanner extends ConsolidationPlanner
{
    // ConsolidationPlanner interface

    @Override
    public boolean planConsolidation(Consolidation.Element newElement)
    {
        assert newElement == null : newElement;
        consolidationElements = consolidationSet.nonDurable().availableForConsolidation();
        long totalCount = 0;
        for (Consolidation.Element element : consolidationElements) {
            totalCount += element.count();
        }
        if (totalCount == 0) {
            consolidationElements = null;
        }
        return consolidationElements != null;
    }

    @Override
    public List<Consolidation.Element> elementsToConsolidate()
    {
        return consolidationElements;
    }

    @Override
    public String type()
    {
        return "allNonDurable";
    }

    // AllNonDurableConsolidationPlanner interface

    public static AllNonDurableConsolidationPlanner newPlanner(ConsolidationSet consolidationSet)
    {
        return new AllNonDurableConsolidationPlanner(consolidationSet);
    }

    // For use by this class

    private AllNonDurableConsolidationPlanner(ConsolidationSet consolidationSet)
    {
        super(consolidationSet, false, true);
    }

    // Object state

    private List<Consolidation.Element> consolidationElements;
}
