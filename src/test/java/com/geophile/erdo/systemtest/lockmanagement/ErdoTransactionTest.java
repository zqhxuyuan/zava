/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.systemtest.lockmanagement;

import com.geophile.erdo.*;
import com.geophile.erdo.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ErdoTransactionTest
{
    public static void main(String[] args)
        throws InterruptedException,
               IOException,
               DeadlockException,
               TransactionRolledBackException
    {
        new ErdoTransactionTest(args).run();
    }

    private ErdoTransactionTest(String[] args) 
        throws IOException, InterruptedException
    {
        // Database setup
        final File DB_DIRECTORY = new File(FileUtil.tempDirectory(), DB_NAME);
        FileUtil.deleteDirectory(DB_DIRECTORY);
        db = Database.createDatabase(DB_DIRECTORY, configuration());
        accounts = db.createMap(MAP_NAME, RecordFactory.simpleRecordFactory(AccountId.class, Account.class));
        int a = 0;
        this.nAccounts = Integer.parseInt(args[a++]);
        this.threads = new ErdoTestThread[Integer.parseInt(args[a++])];
        this.transactionsPerThread = Integer.parseInt(args[a++]);
        LOG.log(Level.INFO, "accounts: {0}", nAccounts);
        LOG.log(Level.INFO, "threads: {0}", threads.length);
        LOG.log(Level.INFO, "transactions per thread: {0}", transactionsPerThread);
    }

    private void run()
        throws InterruptedException,
               IOException,
               DeadlockException,
               TransactionRolledBackException
    {
        createAccounts();
        createThreads();
        startThreads();
        waitForCompletion();
        flush();
        verify();
        shutdown();
    }
    
    private void createAccounts()
        throws IOException,
               DeadlockException,
               InterruptedException,
               TransactionRolledBackException
    {
        for (int a = 0; a < nAccounts; a++) {
            Account account = new Account(new AccountId(a));
            account.balance(0);
            accounts.ensurePresent(account);
        }
        db.commitTransaction();
    }

    private void createThreads()
    {
        for (int t = 0; t < threads.length; t++) {
            threads[t] = new ErdoTestThread(t, db, accounts, transactionsPerThread, nAccounts);
        }
    }

    private void startThreads()
    {
        for (ErdoTestThread thread : threads) {
            thread.start();
        }
    }

    private void waitForCompletion() throws InterruptedException
    {
        for (ErdoTestThread thread : threads) {
            thread.join();
        }
        LOG.log(Level.INFO, "Test threads have all exited.");
    }
    
    private void flush() throws IOException, InterruptedException
    {
        db.flush();
    }

    private void verify() throws IOException, InterruptedException
    {
        long sum = 0;
        Cursor cursor = accounts.first();
        Account account;
        while ((account = (Account) cursor.next()) != null) {
            LOG.log(Level.INFO, "{0}", account);
            sum += account.balance();
        }
        db.commitTransaction();
        if (sum == 0) {
            LOG.log(Level.INFO, "OK!");
        } else {
            LOG.log(Level.WARNING, "Test failed, sum = {0}", sum);
        }
    }

    private void shutdown() throws IOException, InterruptedException
    {
        LOG.log(Level.INFO, "Shutting down");
        db.close();
    }

    private Configuration configuration()
    {
        Configuration configuration = Configuration.defaultConfiguration();
        configuration.consolidationThreads(5);
        configuration.consolidationMinSizeBytes(4 * (1 << 20)); // 4M
        configuration.diskSegmentSizeBytes(4 * (1 << 20)); // 4M
        configuration.diskPageSizeBytes(32 * 1024); // 32K
        configuration.consolidationMaxPendingCommittedSizeBytes(20 * (1 << 20)); // 20 meg
        return configuration;
    }

    private static final Logger LOG = Logger.getLogger(ErdoTransactionTest.class.getName());
    private static final String DB_NAME = "erdo";
    private static final String MAP_NAME = "account";

    private final Database db;
    private final int nAccounts;
    private final OrderedMap accounts;
    private final ErdoTestThread[] threads;
    private final int transactionsPerThread;
}
