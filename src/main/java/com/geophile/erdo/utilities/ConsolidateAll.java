/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.utilities;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.Database;
import com.geophile.erdo.apiimpl.DatabaseOnDisk;

import java.io.File;
import java.io.IOException;

public class ConsolidateAll
{
    public static void main(String[] args) throws Exception
    {
        new ConsolidateAll(args).run();
    }

    private ConsolidateAll(String[] args) throws IOException
    {
        int a = 0;
        dbDirectory = new File(args[0]);
        if (!dbDirectory.exists()) {
            throw new IOException(String.format("%s does not exist", dbDirectory));
        }
        if (!dbDirectory.isDirectory()) {
            throw new IOException(String.format("%s is not a directory", dbDirectory));
        }
    }

    private void run() throws IOException, InterruptedException
    {
        // Turn off all background consolidation
        Configuration configuration = Configuration.emptyConfiguration();
        configuration.consolidationThreads(0);
        DatabaseOnDisk db = (DatabaseOnDisk) Database.useDatabase(dbDirectory, configuration);
        try {
            db.consolidateAll();
        } finally {
            db.close();
        }
    }

    private final File dbDirectory;
}
