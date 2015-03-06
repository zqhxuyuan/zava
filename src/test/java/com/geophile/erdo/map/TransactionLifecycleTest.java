/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.map;

import com.geophile.erdo.Database;
import com.geophile.erdo.TestFactory;
import com.geophile.erdo.TestKey;
import com.geophile.erdo.apiimpl.DisklessTestDatabase;
import com.geophile.erdo.map.privatemap.PrivateMap;
import com.geophile.erdo.transaction.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class TransactionLifecycleTest
{
    @BeforeClass
    public static void beforeClass()
    {
        FACTORY = new TestFactory();
        Transaction.initialize(FACTORY);
    }

    @Before
    public void before() throws IOException, InterruptedException
    {
        db = new DisklessTestDatabase(FACTORY);
        TestKey.testErdoId(ERDO_ID);
    }
    
    @After
    public void after() throws IOException, InterruptedException
    {
        db.close();
        FACTORY.reset();
    }
    
    @Test
    public void testNoopCommit() throws IOException, InterruptedException
    {
        db.commitTransaction();
    }

    @Test
    public void testNoopRollback() throws IOException, InterruptedException
    {
        db.rollbackTransaction();
    }

    @Test
    public void testCommitWithMap() throws IOException, InterruptedException
    {
        new PrivateMap(FACTORY);
        db.commitTransaction();
    }

    private static final int ERDO_ID = 1;
    private static TestFactory FACTORY;

    private Database db;
}
