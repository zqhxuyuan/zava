/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.TestFactory;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.transaction.Transaction;
import com.geophile.erdo.transaction.TransactionManager;
import junit.framework.Assert;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.geophile.erdo.consolidate.Consolidation.Container;
import static com.geophile.erdo.consolidate.Consolidation.Element;
import static org.junit.Assert.fail;

// Runs by itself but hangs when run with all tests?!?!
@Ignore
public class ConsolidationSetTest
{
    @BeforeClass
    public static void beforeClass()
    {
        FACTORY = new TestFactory();
        TRANSACTION_MANAGER = FACTORY.transactionManager();
    }

    @After
    public void after()
    {
        FACTORY.reset();
    }

    @Test
    public void testConsolidation() throws InterruptedException, IOException
    {
        // TODO: async commit
        for (int threads = 1; threads <= 3; threads++) {
/*
            System.out.println(String.format("consolidation threads: %s", threads));
*/
            Configuration configuration = Configuration.defaultConfiguration();
            configuration.consolidationThreads(threads);
            test(configuration, false);
            test(configuration, true);
        }
    }

    private void test(Configuration configuration, boolean suppressSmallConsolidations)
        throws InterruptedException, IOException
    {
/*
        System.out.println("suppressSmallConsolidations: " + suppressSmallConsolidations);
*/
        this.suppressSmallConsolidations = suppressSmallConsolidations;
        if (suppressSmallConsolidations) {
            configuration.consolidationMinSizeBytes();
            configuration.consolidationMaxPendingCommittedSizeBytes(0);
        } else {

        }
        reset();
        TestContainer owner = new TestContainer(configuration);
        for (int i = 0; i < N; i++) {
/*
            if (i % 1000 == 0) {
                System.out.println("add " + i);
            }
*/
            owner.addElement(new TestElement(list(i)));
            TRANSACTION_MANAGER.commitTransaction(null, null);
        }
        boolean[] check = new boolean[N];
        SortedMap<Integer, Integer> sizeCounts = new TreeMap<Integer, Integer>();
        for (Element e : owner.consolidationSet.snapshot().elements()) {
            TestElement element = (TestElement) e;
            for (Integer x : element.list) {
                Assert.assertTrue(!check[x]);
                check[x] = true;
            }
            Integer size = element.list.size();
            Integer count = sizeCounts.get(size);
            if (count == null) {
                count = 1;
            } else {
                count += 1;
            }
            sizeCounts.put(size, count);
        }
        System.out.println(String.format("N: %s, suppressSmallConsolidations: %s - %s",
                                         suppressSmallConsolidations,
                                         configuration.consolidationThreads(),
                                         sizeCounts));
        for (boolean b : check) {
            Assert.assertTrue(b);
        }
        int total = 0;
        for (Map.Entry<Integer, Integer> entry : sizeCounts.entrySet()) {
            Integer size = entry.getKey();
            Integer count = entry.getValue();
            total += size * count;
        }
        Assert.assertEquals(N, total);
        owner.stop();
    }

    private void reset()
    {
        Transaction.initialize(FACTORY);
        FACTORY.reset();
    }

    private void print(String template, Object... args)
    {
        System.out.println(String.format(template, args));
    }

    private List<Integer> list(Integer... values)
    {
        return Arrays.asList(values);
    }

    private static final int N = 10000;
    private static TestFactory FACTORY;
    private static TransactionManager TRANSACTION_MANAGER;

    private AtomicInteger idCounter = new AtomicInteger(0);
    private boolean suppressSmallConsolidations;

    private class TestElement implements Element
    {
        public long id()
        {
            return id;
        }

        public long count()
        {
            return list.size();
        }

        public long sizeBytes()
        {
            return suppressSmallConsolidations ? list.size() : 0;
        }

        public void destroyPersistentState()
        {
        }

        public void markDurable()
        {
            this.durable = true;
            if (initialTransaction != null) {
                initialTransaction.markDurable();
            }
        }

        public boolean durable()
        {
            return durable;
        }

        public TimestampSet timestamps()
        {
            return null;
        }

        public void registerTransactions(List<Transaction> transactions)
        {
            this.transactions.addAll(transactions);
        }

        public List<Transaction> transactions()
        {
            return transactions;
        }

        public TestElement(List<Integer> list)
        {
            this.id = idCounter.getAndIncrement();
            this.list = list;
            this.initialTransaction = TRANSACTION_MANAGER.currentTransaction();
        }

        private final int id;
        private final List<Integer> list;
        private final Transaction initialTransaction;
        private final List<Transaction> transactions = new ArrayList<Transaction>();
        private boolean durable = false;
    }

    private class TestContainer implements Container
    {
        public Configuration configuration()
        {
            return consolidationSet.configuration();
        }

        public Element consolidate(List<Element> elements,
                                   boolean inputDurable,
                                   boolean outputDurable)
        {
            TestElement consolidated = new TestElement(new ArrayList<Integer>());
            for (Element element : elements) {
                TestElement r = (TestElement) element;
                consolidated.list.addAll(r.list);
            }
            return consolidated;
        }

        public void replaceObsolete(List<Element> obsolete,
                                    Element replacement)
        {
        }

        public Factory factory()
        {
            return FACTORY;
        }

        public void stop() throws InterruptedException, IOException
        {
            consolidationSet.shutdown();
        }

        public TestContainer(Configuration configuration)
        {
            if (suppressSmallConsolidations) {
                configuration.consolidationMinSizeBytes((int)(N * 1.1));
            }
            this.consolidationSet = ConsolidationSet.newConsolidationSet(this,
                                                                         Collections.<Element>emptyList());
        }

        public synchronized void addElement(TestElement element) throws InterruptedException, IOException
        {
            consolidationSet.add(element, true);
        }

        @Override
        public void reportCrash(Throwable crash)
        {
            fail();
        }

        // Object state

        final ConsolidationSet consolidationSet;
    }
}
