/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.apiimpl;

import com.geophile.erdo.*;
import com.geophile.erdo.forest.Forest;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.map.diskmap.DBStructure;
import com.geophile.erdo.transaction.Transaction;
import com.geophile.erdo.transaction.TransactionManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DatabaseImpl extends Database
{
    // Database interface

    public static DatabaseOnDisk createDatabase(File dbDirectory,
                                                Configuration configuration,
                                                Class<? extends Factory> factoryClass)
        throws IOException, InterruptedException
    {
        setPID();
        synchronized (STATIC_LOCK) {
            checkDatabaseClosed();
            return DatabaseOnDisk.createDatabase(dbDirectory, factory(factoryClass, configuration));
        }
    }

    public static DatabaseOnDisk useDatabase(File dbDirectory,
                                             Configuration configuration,
                                             Class<? extends Factory> factoryClass)
        throws IOException, InterruptedException
    {
        setPID();
        synchronized (STATIC_LOCK) {
            checkDatabaseClosed();
            return DatabaseOnDisk.openDatabase
                (dbDirectory, factory(factoryClass, mergedConfiguration(dbDirectory, configuration)));
        }
    }

    // synchronized to prevent race condition when two threads create maps with the same name at the
    // same time.
    @Override
    public synchronized OrderedMap createMap(String mapName, RecordFactory recordFactory)
        throws UsageError, IOException, InterruptedException
    {
        LOG.log(Level.INFO, "Creating OrderedMap {0}", mapName);
        checkDatabaseOpen();
        if (maps.containsKey(mapName)) {
            throw new UsageError(String.format(
                "Cannot create map %s because it already exists", mapName));
        }
        OrderedMapImpl map = new OrderedMapImpl(this);
        factory.registerRecordFactory(map.erdoId(), recordFactory);
        maps.put(mapName, map);
        return map;
    }

    // synchronized so that two threads opening the same map at the same time get the same OrderedMap object.
    @Override
    public synchronized OrderedMap useMap(String mapName)
        throws UsageError, IOException, InterruptedException
    {
        checkDatabaseOpen();
        OrderedMapImpl map = maps.get(mapName);
        if (map == null) {
            throw new UsageError(String.format("Map %s does not exist", mapName));
        }
        return map;
    }

    @Override
    public void lock(AbstractKey key)
        throws InterruptedException,
               com.geophile.erdo.transaction.DeadlockException,
               TransactionRolledBackException
    {
        forest.lock(key);
    }

    @Override
    public final void commitTransactionAsynchronously(TransactionCallback transactionCallback, Object commitInfo)
        throws IOException, InterruptedException
    {
        checkDatabaseOpen();
        forest.commitTransaction(transactionCallback, commitInfo);
    }

    @Override
    public final void rollbackTransaction() throws IOException, InterruptedException
    {
        checkDatabaseOpen();
        forest.rollbackTransaction();
    }

    @Override
    public final void flush() throws IOException, InterruptedException
    {
        checkDatabaseOpen();
        forest.flush();
    }

    @Override
    public void consolidateAll() throws IOException, InterruptedException
    {
        checkDatabaseOpen();
        forest.consolidateAll();
    }

    @Override
    public void close() throws IOException, InterruptedException
    {
        synchronized (STATIC_LOCK) {
            if (databaseOpen) {
                databaseOpen = false;
                deadlockFixer.stop();
                forest.close();
            }
        }
    }

    @Override
    public final Configuration configuration()
    {
        return factory.configuration();
    }

    // DatabaseImpl interface

    public final Factory factory()
    {
        return factory;
    }

    public final void reportCrash(Throwable crash)
    {
        this.crash.set(crash);
    }

    public static int pid()
    {
        return pid;
    }
    
    // For testing
    public static void reset()
    {
        databaseOpen = false;
    }
    
    // For use by this subclasses

    protected DatabaseImpl(Factory factory)
        throws IOException, InterruptedException
    {
        this.factory = factory;
        this.deadlockFixer = new DeadlockFixer(factory.lockManager());
        Transaction.initialize(factory);
        databaseOpen = true;
    }

    // For use by this package

    TransactionManager transactionManager()
    {
        return forest;
    }

    void checkDatabaseOpen() throws IOException, InterruptedException
    {
        if (crash.get() != null) {
            close();
            LOG.log(Level.SEVERE, "Shutting down because of crash in thread managed by Erdo", crash.get());
        }
        if (!databaseOpen) {
            throw new UsageError("No database is open");
        }
    }

    // For use by this class

    private static void setPID()
    {
        if (PID_SYSTEM_PROPERTY == null) {
            throw new UsageError("The pid system variable must be set to the process id of the current process.");
        }
        try {
            pid = Integer.parseInt(PID_SYSTEM_PROPERTY);
        } catch (NumberFormatException e) {
            throw new UsageError(
                String.format("The pid system variable must be set to the process id of the current process. " +
                              "pid was set to %s",
                              PID_SYSTEM_PROPERTY));
        }
    }

    private static void checkDatabaseClosed()
    {
        if (databaseOpen) {
            throw new UsageError("A database is already open.");
        }
    }

    private static Factory factory(Class<? extends Factory> factoryClass, Configuration configuration)
    {
        try {
            Constructor<? extends Factory> factoryConstructor = factoryClass.getConstructor(Configuration.class);
            return factoryConstructor.newInstance(configuration);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new UsageError(e);
        }
    }

    private static ConfigurationImpl mergedConfiguration(File dbDirectory, Configuration overrideConfiguration)
        throws IOException
    {
        ConfigurationImpl mergedConfiguration = new ConfigurationImpl();
        DBStructure dbStructure = new DBStructure(dbDirectory);
        try {
            mergedConfiguration.read(dbStructure.dbPropertiesFile());
        } catch (IOException e) {
            throw new UsageError(e);
        }
        if (overrideConfiguration != null) {
            mergedConfiguration.override(overrideConfiguration);
        }
        return mergedConfiguration;
    }

    // Class state

    protected static final Logger LOG = Logger.getLogger(DatabaseImpl.class.getName());
    private static final Object STATIC_LOCK = new Object();
    private static final String PID_SYSTEM_PROPERTY = System.getProperty("pid", null);
    private static int pid;
    private static volatile boolean databaseOpen = false;

    // Object state

    protected final Factory factory;
    protected final Map<String, OrderedMapImpl> maps = new HashMap<>();
    protected Forest forest;
    private final DeadlockFixer deadlockFixer;
    private final AtomicReference<Throwable> crash = new AtomicReference<>(null);
}
