/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.geophile.erdo.examples.helloworld;

import com.github.geophile.erdo.Database;
import com.github.geophile.erdo.RecordFactory;
import com.github.geophile.erdo.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class CreateMap
{
    public static void main(String[] args) throws IOException, InterruptedException
    {
        File DB_DIRECTORY = new File(FileUtil.tempDirectory(), "mydb");
        Database db = Database.useDatabase(DB_DIRECTORY);
        db.createMap("musicians", RecordFactory.simpleRecordFactory(Name.class, Person.class));
        db.close();
        System.out.println(String.format("Created 'musicians' map in database %s", DB_DIRECTORY));
    }
}
