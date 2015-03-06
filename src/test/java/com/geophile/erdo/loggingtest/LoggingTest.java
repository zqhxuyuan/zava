/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.loggingtest;

import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingTest
{
    @Test
    public void test()
    {
        LOG.log(Level.SEVERE, "LOG.severe");
        LOG.log(Level.WARNING, "LOG.warning");
        LOG.log(Level.INFO, "LOG.info");
        LOG.log(Level.FINE, "LOG.fine");
        LOG.log(Level.FINER, "LOG.finer");
        LOG.log(Level.FINEST, "LOG.finest");
        PARENT.log(Level.SEVERE, "PARENT.severe");
        PARENT.log(Level.WARNING, "PARENT.warning");
        PARENT.log(Level.INFO, "PARENT.info");
        PARENT.log(Level.FINE, "PARENT.fine");
        PARENT.log(Level.FINER, "PARENT.finer");
        PARENT.log(Level.FINEST, "PARENT.finest");
        CHILD.log(Level.SEVERE, "CHILD.severe");
        CHILD.log(Level.WARNING, "CHILD.warning");
        CHILD.log(Level.INFO, "CHILD.info");
        CHILD.log(Level.FINE, "CHILD.fine");
        CHILD.log(Level.FINER, "CHILD.finer");
        CHILD.log(Level.FINEST, "CHILD.finest");
    }

    private static final Logger LOG = Logger.getLogger("com.geophile.erdo.loggingtest");
    private static final Logger PARENT = Logger.getLogger("com.geophile.erdo");
    private static final Logger CHILD = Logger.getLogger("com.geophile.erdo.loggingtest.child");
}
