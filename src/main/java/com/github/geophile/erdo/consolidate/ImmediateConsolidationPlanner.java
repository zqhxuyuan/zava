/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.geophile.erdo.consolidate;

import java.util.ArrayList;
import java.util.List;

// ConsolidationPlanner to be used when a synchronous commit adds its private tree to the forest.
// The element provided to the constructor is guaranteed to be durable on return from the
// consolidation using this planner.

class ImmediateConsolidationPlanner extends ConsolidationPlanner
{
    // ConsolidationPlanner interface

    @Override
    public boolean planConsolidation(Consolidation.Element newElement)
    {
        assert newElement != null;
        assert !newElement.durable() : newElement;
        justCommitted.clear();
        justCommitted.add(newElement);
        return true;
    }

    @Override
    public List<Consolidation.Element> elementsToConsolidate()
    {
        return justCommitted;
    }

    @Override
    public String type()
    {
        return "immediate";
    }

    // ImmediateConsolidationPlanner interface

    public static ImmediateConsolidationPlanner newPlanner(ConsolidationSet consolidationSet)
    {
        return new ImmediateConsolidationPlanner(consolidationSet);
    }

    // For use by this class

    private ImmediateConsolidationPlanner(ConsolidationSet consolidationSet)
    {
        super(consolidationSet, false, true);
    }

    // Object state

    private final List<Consolidation.Element> justCommitted = new ArrayList<Consolidation.Element>(1);
}
