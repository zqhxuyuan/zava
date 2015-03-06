/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo;

import com.geophile.erdo.apiimpl.DatabaseImpl;
import com.geophile.erdo.apiimpl.DefaultFactory;

import java.io.File;
import java.io.IOException;

/**
 * All state is managed in the context of a database. An application can access only one database at a time.
 * Database objects are used to create and open maps, to control configuration, and to manage transactions.
 */

public abstract class Database
{
    /**
     * Create a new Database, using the default configuration.
     * The pid system variable must be set to the pid of the current process.
     * @param dbDirectory The directory to contain the database. It must not exist upon invocation.
     * @return A new Database.
     * @throws IOException
     * @throws InterruptedException
     */
    public static Database createDatabase(File dbDirectory)
        throws IOException, InterruptedException
    {
        return DatabaseImpl.createDatabase(dbDirectory, Configuration.defaultConfiguration(), DefaultFactory.class);
    }

    /**
     * Create a new Database, using the given configuration.
     * The pid system variable must be set to the pid of the current process.
     * @param dbDirectory The directory to contain the database. It must not exist upon invocation.
     * @param configuration The configuration of the database.
     * @return A new Database.
     * @throws IOException
     * @throws InterruptedException
     */
    public static Database createDatabase(File dbDirectory, Configuration configuration)
        throws IOException, InterruptedException
    {
        return DatabaseImpl.createDatabase(dbDirectory, configuration, DefaultFactory.class);
    }

    /**
     * Provides access to an existing Database. The configuration will be the one provided when the database
     * was created, (or the default configuration if none was supplied).
     * The pid system variable must be set to the pid of the current process.
     * @param dbDirectory The directory containing the database.
     * @return The database contained in the given directory.
     * @throws IOException
     * @throws InterruptedException
     */
    public static Database useDatabase(File dbDirectory)
        throws IOException, InterruptedException
    {
        return DatabaseImpl.useDatabase(dbDirectory, null, DefaultFactory.class);
    }

    /**
     * Provides access to an existing Database. The configuration will be the one provided when the database
     * was created, (or the default configuration if none was supplied),
     * but with overrides from the given configuration.
     * The pid system variable must be set to the pid of the current process.
     * @param dbDirectory The directory containing the database.
     * @param configuration Overrides to the current database configuration, which apply only to the current process.
     * Only the following configuration properties may be specified:
     * <ul>
     *     <li>disk.cacheSizeBytes
     *     <li>disk.cacheSlabSizeBytes
     *     <li>consolidation.threads
     *     <li>consolidation.minSizeBytes
     *     <li>consolidation.maxPendingCommittedSizeBytes
     *     <li>consolidation.minMapsToConsolidate
     *     <li>consolidation.idleTimeSec
     * </ul>
     * @return The database contained in the given directory.
     * @throws IOException
     * @throws InterruptedException
     */
    public static Database useDatabase(File dbDirectory, Configuration configuration)
        throws IOException, InterruptedException
    {
        return DatabaseImpl.useDatabase(dbDirectory, configuration, DefaultFactory.class);
    }

    /**
     * Creates a new map in the database.
     * @param mapName The name of the map.
     * @param recordFactory Used to create new keys and records for the new map.
     * @return A new map.
     * @throws IOException
     */
    public abstract OrderedMap createMap(String mapName, RecordFactory recordFactory)
        throws IOException, InterruptedException;

    /**
     * Provides access to the named map.
     * @param mapName The name of the map to be opened.
     * @return The named map.
     * @throws IOException
     */
    public abstract OrderedMap useMap(String mapName) throws IOException, InterruptedException;

    public abstract void lock(AbstractKey key)
        throws InterruptedException,
               com.geophile.erdo.transaction.DeadlockException,
               TransactionRolledBackException;

    /**
     * Commit the current transaction's updates. After the call, the updates from the transaction are durable
     * and visible.
     * @throws IOException
     * @throws InterruptedException
     */
    public final void commitTransaction()
        throws IOException, InterruptedException
    {
        commitTransactionAsynchronously(null);
    }

    /**
     * Commit the current transaction's updates. After the call, the updates from the transaction are visible.
     * The updates are guaranteed to be durable only after the callback method
     * {@link com.geophile.erdo.TransactionCallback#whenDurable(Object)} is invoked, (null will be passed
     * to the callback).
     * @param transactionCallback {@link com.geophile.erdo.TransactionCallback#whenDurable(Object)} is
     *    called (passing null) once the updates from the current transaction become durable.
     * @throws IOException
     * @throws InterruptedException
     */
    public final void commitTransactionAsynchronously(TransactionCallback transactionCallback)
        throws IOException, InterruptedException
    {
        commitTransactionAsynchronously(transactionCallback, null);
    }

    /**
     * Commit the current transaction's updates. After the call, the updates from the transaction are visible.
     * The updates are guaranteed to be durable only after the callback method
     * {@link com.geophile.erdo.TransactionCallback#whenDurable(Object)} is invoked, (commitInfo will be passed
     * to the callback).
     * @param transactionCallback {@link com.geophile.erdo.TransactionCallback#whenDurable(Object)} is
     *    called once the updates from the current transaction become durable.
     * @param commitInfo Value passed to {@link com.geophile.erdo.TransactionCallback#whenDurable(Object)}. This
     *    value identifies the transaction to the transaction callback.
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract void commitTransactionAsynchronously(TransactionCallback transactionCallback,
                                                         Object commitInfo)
        throws IOException, InterruptedException;

    /**
     * Ends the transaction, discarding all of the transaction's updates.
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract void rollbackTransaction() throws IOException, InterruptedException;

    /**
     * Makes durable all committed, non-durable state.
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract void flush() throws IOException, InterruptedException;

    /**
     * Consolidates all durable state into a single tree.
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract void consolidateAll() throws IOException, InterruptedException;

    /**
     * Closes the database. All further actions on the database will throw exceptions.
     * @throws IOException
     * @throws InterruptedException
     */
    public abstract void close() throws IOException, InterruptedException;

    /**
     * Returns the database's configuration.
     * @return The database's configuration.
     */
    public abstract Configuration configuration();
}
