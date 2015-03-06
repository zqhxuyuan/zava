# Erdo

Erdo is a key/value store, accessed through a Java API. A store, or
*database* consists of zero or more *maps*, each storing a set of
key/value records.

## Features

* **Key-Ordered:** Records are stored in key order. Iterating over a map
visits records in key order.

* **Transactions:** All access to an Erdo database is done in the
scope of a transaction. Transactions provide [snapshot
isolation](http://en.wikipedia.org/wiki/Snapshot_isolation),
implemented via a multi-version concurrency control mechanism. A
transaction operates against the state of the database as it existed
when the transaction started. Updates may be committed in the absence
of conflicting updates from other transactions. Read-only transactions
never block and are never rolled back.

* **Update behavior:** The Erdo API provides two kinds of update
methods, which differ in whether they return the original record,
(the state of the record immediately preceding the update). Updates that do not
return the original value will usually be faster than updates that do
return the original value.

## Installation

Erdo can be built from source using [maven](http://maven.apache.org):

        mvn install

This creates `target/erdo-1.0.jar`.

To create Javadoc files:

        mvn javadoc:javadoc

These files are located in `target/site/apidocs`.

## Example

The Java sources of a simple Erdo application are located in
`src/test/java/com/geophile/erdo/examples/helloworld`. The scripts
running the example are in `src/test/examples`. To run the examples,
set the CLASSPATH to contain target/erdo-1.0.jar and
target/test-classes. (The scripts are written in bash.)

### Creating a database

The following program creates an empty Erdo database.

        package com.geophile.erdo.examples.helloworld;

        import com.geophile.erdo.Database;
        
        import java.io.File;
        import java.io.IOException;

        public class CreateDatabase
        {
            public static void main(String[] args) throws IOException, InterruptedException
            {
                File DB_DIRECTORY = new File(FileUtil.tempDirectory(), "mydb");
                Database db = Database.createDatabase(DB_DIRECTORY);
                db.close();
                System.out.println(String.format("Database created in %s", DB_DIRECTORY));
            }
        }

This program can be run using the script
`src/test/examples/helloworld.createdb`.  After running it, the
directory `/tmp/mydb` will contain the file `database.properties`, a
Java properties file describing the database's configuration.

### Creating a map

The following program opens an existing Erdo database and creates an
empty map within it:

        package com.geophile.erdo.examples.helloworld;
        
        import com.geophile.erdo.Database;
        
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

This program can be run using the script `src/test/examples/helloworld.createmap`
It creates a new map, named "musicians", which can contain keys of type `helloworld.Name`,
and values of type `helloworld.Person`.

### Updating a map

The following program opens an existing Erdo map and creates some records.
    
        package com.geophile.erdo.examples.helloworld;
        
        import com.geophile.erdo.*;
        
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
                Scan cursor = family.cursor();
                Person person;
                while ((person = (Person) cursor.next()) != null) {
                    Name name = person.key();
                    System.out.println(String.format("    %s: %s", 
                                                     name.name, person.birthDate));
                }
            }
        }

This program can be run using the script `src/test/examples/helloworld.updatemap`.    
The `musicians` map is opened and then updated in two stages. First, four
`Person` objects are added. After the `Person` objects are
placed in the map, using `musicians.put`, the updates are made public by
calling `db.commitTransaction()`. Once `commitTransaction` returns, the
`Person` objects are visible to any concurrent users of the `/tmp/mydb`
database. `printDatabase` prints database contents:

        original state
            Dick Dale: May 4, 1937
            Elvis Costello: August 25, 1954
            James Booker: December 17, 1939
            Louis Armstrong: August 4, 1901
    
Next, another `Person` is added. Database contents (as
visible to the current transaction) are printed, and now five records
are visible, the original four, and the new record:
    
        record added but not committed
            Dick Dale: May 4, 1937
            Elvis Costello: August 25, 1954
            James Booker: December 17, 1939
            Kenny Rogers: August 21, 1938
            Louis Armstrong: August 4, 1901
    
However, instead of committing, the transaction is rolled
back. Printing database contents afterward shows that the last update
is no longer present in the database.

        after rollback
            Dick Dale: May 4, 1937
            Elvis Costello: August 25, 1954
            James Booker: December 17, 1939
            Louis Armstrong: August 4, 1901
