/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import java.util.ArrayDeque;
import java.util.Deque;

// A pool of interchangeable resources

public abstract class AbstractPool<RESOURCE>
{
    public final synchronized RESOURCE takeResource()
    {
        RESOURCE resource;
        if (resourceList.isEmpty()) {
            resource = newResource();
        } else {
            resource = resourceList.removeFirst();
            RESOURCE removed = resourceSet.remove(resource);
            assert removed == resource : resource;
        }
        activate(resource);
        return resource;
    }

    public final synchronized void returnResource(RESOURCE resource)
    {
        deactivate(resource);
        resourceList.addLast(resource);
        RESOURCE replaced = resourceSet.add(resource);
        assert replaced == null : resource;
    }

    public abstract RESOURCE newResource();

    public void activate(RESOURCE resource)
    {}

    public void deactivate(RESOURCE resource)
    {}

    private final Deque<RESOURCE> resourceList = new ArrayDeque<>();
    // To check uniqueness
    private final IdentitySet<RESOURCE> resourceSet = new IdentitySet<>();
}
