/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.systemtest.singleprocess;

import com.geophile.erdo.Database;
import com.geophile.erdo.util.FileUtil;

import java.io.File;
import java.io.IOException;

// Assuming test driver creates a database, and then in a separate process,
// opens a database within PAUSE_TIME_MSEC.

public class ProcessExclusionTest
{
    public static void main(String[] args) throws IOException, InterruptedException
    {
        DB_DIRECTORY = new File(FileUtil.tempDirectory(), DB_NAME);
        new ProcessExclusionTest(args).run();
    }

    private ProcessExclusionTest(String[] args)
    {
        createDB = args.length > 0 && args[0].equals("create");
    }

    private void run() throws IOException, InterruptedException
    {
        Database db;
        if (createDB) {
            FileUtil.deleteDirectory(DB_DIRECTORY);
            db = Database.createDatabase(DB_DIRECTORY);
            System.out.println(String.format("pid %s: %s created", pid(), db));
            Thread.sleep(PAUSE_TIME_MSEC);
        } else {
            db = Database.useDatabase(DB_DIRECTORY);
            System.out.println(String.format("pid %s: %s opened", pid(), db));
        }
    }

    private String pid()
    {
        return System.getProperty("pid");
    }

    private static final String DB_NAME = "erod";
    private static File DB_DIRECTORY;
    private static final int PAUSE_TIME_MSEC = 5_000;

    private boolean createDB;
}
