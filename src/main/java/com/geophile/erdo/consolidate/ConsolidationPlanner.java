/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import java.util.List;

import static com.geophile.erdo.consolidate.Consolidation.Element;

abstract class ConsolidationPlanner
{
    // Object interface

    @Override
    public String toString()
    {
        return description;
    }

    // ConsolidationPlanner interface

    public final ConsolidationSet consolidationSet()
    {
        return consolidationSet;
    }

    /**
     * Determine the values to be returned by elementsToConsolidate and durable. This is
     * done under the consolidation container's synchronization. Calls to elementsToConsolidate
     * and durable are done later and may not be synchronized.
     * @param newElement A non-durable element that has just committed, or null if the caller
     *                   has no such element.
     * @return true if a consolidation needs to be performed, false otherwise.
     */
    public abstract boolean planConsolidation(Element newElement);

    /**
     * Returns a list of Elements to be consolidated that was determined during planConsolidation()
     * @return Elements to be consolidated.
     */
    public abstract List<Element> elementsToConsolidate();

    /**
     * Indicates whether consolidation inputs are durable or non-durable.
     * @return true if the consolidation input elements are durable, false otherwise.
     */
    public final boolean inputDurable()
    {
        return inputDurable;
    }
    
    /**
     * Indicates whether the consolidation should produce a durable or non-durable result.
     * This was worked out during planConsolidation().
     * @return true if the consolidated element is durable, false otherwise.
     */
    public final boolean outputDurable()
    {
        return outputDurable;
    }

    public abstract String type();

    // For use by subclasses

    protected ConsolidationPlanner(ConsolidationSet consolidationSet,
                                   boolean inputDurable,
                                   boolean outputDurable)
    {
        this.consolidationSet = consolidationSet;
        this.inputDurable = inputDurable;
        this.outputDurable = outputDurable;
        this.description = String.format("%s%s:%s",
                                         inputDurable ? 'd' : 'n',
                                         outputDurable ? 'd' : 'n',
                                         type());
    }

    // Object state

    protected final ConsolidationSet consolidationSet;
    private final String description;
    private final boolean inputDurable;
    private final boolean outputDurable;
}
