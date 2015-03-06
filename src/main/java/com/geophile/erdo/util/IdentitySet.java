/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import java.util.IdentityHashMap;

public class IdentitySet<T> extends IdentityHashMap<T, T>
{
    public T add(T t)
    {
        return put(t, t);
    }

    public boolean contains(T t)
    {
        return containsKey(t);
    }
}
