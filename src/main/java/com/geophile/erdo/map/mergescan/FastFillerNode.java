/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map.mergescan;

class FastFillerNode extends FastNode
{
    public String toString()
    {
        return String.format("Filler(#%s)", position);
    }

    public void prime()
    {
    }

    public void fastPromote()
    {
    }

    public FastFillerNode(int position)
    {
        super(position, true);
    }
}
