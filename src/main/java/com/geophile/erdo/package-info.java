/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/**
 * Erdo is a transactional, ordered key/value store.
 *
 * An Erdo {@link com.geophile.erdo.Database} contains ordered maps.
 * Each {@link com.geophile.erdo.OrderedMap} contains key/value pairs.
 * Keys are unique within a map. Maps are ordered, so when map contents are traversed,
 * records are visited in key order, as defined by {@link com.geophile.erdo.AbstractKey#compareTo(AbstractKey)}.
 *
 * <h2>Usage</h2>
 *
 * <p> Erdo is an embedded database. Your application makes calls to the Erdo API, and all database access takes
 * place in the application's process. A database can only be accessed by one process at a time, but within that
 * process, multi-threaded access is supported.
 *
 * <p>The configuration and state of a {@link com.geophile.erdo.Database} is stored in a directory.
 * The directory is specified when the database
 * is created or opened. The database directory must not exist prior to database creation.
 * Database access is permitted after
 * the database is created or opened, and before the database is closed.
 * Within a database, ordered maps may be created and opened. All created and opened maps remain open until
 * the database is closed.
 *
 * <h2>Keys and Records</h2>
 *
 * <p> An Erdo application provides key and value types by extending
 * {@link com.geophile.erdo.AbstractKey} and {@link com.geophile.erdo.AbstractRecord}. A key class must override
 * methods for hashing the key, comparing keys, serializing, deserializing, and estimating the
 * size of a serialized key. A record class has to define methods for serializing, deserializing, copying, and
 * estimating the size of a serialized record.
 *
 * <p> Keys are unique within a map. If an application wants to associate multiple values with a key, then the logic
 * for doing so must be in the application, either by having the record type provide for multiple values, or by
 * defining a key type with a field for a counter, for example.
 *
 * <h2>Transactions</h2>
 *
 * <p> Access to a database always takes place within the scope of a transaction. Erdo transactions implement
 * <a href="http://en.wikipedia.org/wiki/Snapshot_isolation">snapshot isolation</a>, which means that a transaction
 * operates on the database as it existed at the start of the transaction, modified only by the transaction's own
 * updates.
 *
 * <p> Transactions are not explicitly started. A thread accessing a database is always in a transaction, starting
 * as soon as the database is open, or as soon as the previous transaction has ended. A transaction is ended by calling
 * either {@link com.geophile.erdo.Database#commitTransaction()} or
 * {@link com.geophile.erdo.Database#rollbackTransaction()}. commitTransactionAsynchronously makes the transaction's updates (if any)
 * durable and visible to new transactions. rollbackTransaction discards the transaction's updates.
 *
 * <p> Transaction state may be written to disk synchronously or asynchronously. Synchronous commits are slower, but
 * guarantee durability once commitTransactionAsynchronously returns. Asynchronous commits are faster but don't guarantee durability
 * immediately. An asynchronous commit is performed by calling
 * {@link com.geophile.erdo.Database#commitTransactionAsynchronously(TransactionCallback)}. The callback
 * {@link com.geophile.erdo.TransactionCallback#whenDurable(Object)} is invoked once the updates from the transaction
 * are made durable. This callback can be used to implement application-level management of committed but
 * not-yet-durable state.
 *
 * <h2>Updates</h2>
 *
 * <p> There are four methods for updating an ordered map:
 * <ul>
 *     <li>{@link com.geophile.erdo.OrderedMap#put(AbstractRecord)}: Inserts or updates a record, returning
 *          the previous record associated with the new record's key.
 *     <li>{@link com.geophile.erdo.OrderedMap#delete(AbstractKey)}: Deletes a record, returning the previous
 *          record associated with the given key.
 *     <li>{@link com.geophile.erdo.OrderedMap#ensurePresent(AbstractRecord)}: Inserts or updates a record without
 *          returning any previous record.
 *     <li>{@link com.geophile.erdo.OrderedMap#ensureAbsent(AbstractKey)}: Deletes a record without
 *          returning the record previously associated with the given key.
 * </ul>
 *
 * <p> put and delete return the record being replaced, or null if there is no such record.
 * They therefore have to locate the previous record in the database. ensurePresent and ensureAbsent do not
 * have to locate a previous record, and
 * for this reason, they can often result in improved performance. ensurePresent and ensureAbsent should be used
 * whenever the application doesn't care about any previous state associated with a key.
 *
 * <h2>Retrieval</h2>
 *
 * <p> To retrieve the record associated with a key, the simplest thing to do is to call
 * {@link com.geophile.erdo.OrderedMap#find(AbstractKey)}.
 *
 * <p> {@link com.geophile.erdo.Cursor} objects are used for retrieval of multiple records, starting at a key.
 * {@link com.geophile.erdo.OrderedMap#first()} and {@link com.geophile.erdo.OrderedMap#last()}
 * start at the first and last records of the map, respectively.
 * {@link com.geophile.erdo.OrderedMap#cursor(AbstractKey)} returns a {@link com.geophile.erdo.Cursor}
 * object positioned at a given key, and that can visit neighboring records in either direction.
 *
 * <p> The control of a Cursor is simple: {@link com.geophile.erdo.Cursor#next()} returns the record with the next
 * larger key, or null if there is none. {@link com.geophile.erdo.Cursor#previous()} goes in the other direction,
 * finding the record with the next smaller key. A Cursor is considered to be open until next() or previous() returns
 * null, closed once null has been returned. Once a Cursor is closed, all subsequent calls to next() or previous()
 * will return null.
 * A Cursor can also be closed explicitly using {@link com.geophile.erdo.Cursor#close()}.
 * Any cursors open at the end of a transaction will be closed.
 */

package com.geophile.erdo;
