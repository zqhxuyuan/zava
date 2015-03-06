/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

class ImmediateConsolidator extends Consolidator
{
    // ImmediateConsolidator interface

    public static ImmediateConsolidator newConsolidator(ConsolidationSet consolidationSet,
                                                        ConsolidationPlanner planner)
    {
        return new ImmediateConsolidator(consolidationSet, planner);
    }

    // For use by this class

    private ImmediateConsolidator(ConsolidationSet consolidationSet, ConsolidationPlanner planner)
    {
        super(consolidationSet, planner);
    }

}
