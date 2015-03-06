/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CurrentStack
{
    public static String asString()
    {
        StringWriter output = new StringWriter();
        new Exception().printStackTrace(new PrintWriter(output));
        return output.toString();
    }
}
