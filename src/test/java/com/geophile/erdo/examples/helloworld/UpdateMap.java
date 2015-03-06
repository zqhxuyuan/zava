/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.examples.helloworld;

import com.geophile.erdo.*;
import com.geophile.erdo.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class UpdateMap
{
    public static void main(String[] args)
        throws IOException,
               InterruptedException,
               DeadlockException,
               TransactionRolledBackException
    {
        File DB_DIRECTORY = new File(FileUtil.tempDirectory(), "mydb");
        Database db = Database.useDatabase(DB_DIRECTORY);
        OrderedMap musicians = db.useMap("musicians");

        // Add records to map
        musicians.put(new Person("James Booker", "December 17, 1939"));
        musicians.put(new Person("Louis Armstrong", "August 4, 1901"));
        musicians.put(new Person("Elvis Costello", "August 25, 1954"));
        musicians.put(new Person("Dick Dale", "May 4, 1937"));
        db.commitTransaction();

        // Scan and print map contents
        printDatabase(musicians, "original state");

        // Add a record, but then roll back the transaction
        musicians.put(new Person("Kenny Rogers", "August 21, 1938"));
        printDatabase(musicians, "record added but not committed");
        db.rollbackTransaction();

        // Scan again, seeing only the original records
        printDatabase(musicians, "after rollback");

        // Shut down
        db.close();
    }

    private static void printDatabase(OrderedMap family, String label)
        throws IOException, InterruptedException
    {
        System.out.println(label);
        Cursor cursor = family.first();
        Person person;
        while ((person = (Person) cursor.next()) != null) {
            Name name = person.key();
            System.out.println(String.format("    %s: %s", name.name, person.birthDate));
        }
    }
}
