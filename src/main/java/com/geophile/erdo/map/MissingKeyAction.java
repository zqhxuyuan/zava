/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

public enum MissingKeyAction
{
    FORWARD,
    BACKWARD,
    CLOSE;

    public boolean forward()
    {
        // Handle CLOSE the same as FORWARD
        return this != BACKWARD;
    }
}
