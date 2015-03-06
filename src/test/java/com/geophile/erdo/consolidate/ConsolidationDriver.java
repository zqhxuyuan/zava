/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import com.geophile.erdo.Configuration;
import com.geophile.erdo.map.Factory;
import com.geophile.erdo.transaction.TimestampSet;
import com.geophile.erdo.transaction.Transaction;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static com.geophile.erdo.consolidate.Consolidation.Container;
import static com.geophile.erdo.consolidate.Consolidation.Element;
import static org.junit.Assert.fail;

public class ConsolidationDriver
{
    @Test
    @Ignore
    public void test() throws IOException, InterruptedException
    {
        final int N = 100;
        TestContainer container = new TestContainer();
        Configuration configuration = Configuration.defaultConfiguration();
        configuration.consolidationThreads(0);
        ConsolidationSet consolidationSet =
            ConsolidationSet.newConsolidationSet(container,
                                                 Collections.<Element>emptyList());
        for (int i = 0; i < N; i++) {
            TestElement element = new TestElement(idCounter++, 1000);
            ConsolidationPlanner planner =
                ImmediateConsolidationPlanner.newPlanner(consolidationSet);
            consolidationSet.add(element, true);
            ConsolidationTask task = new ConsolidationTask(null, element);
            task.consolidateIfNecessary();
            dump(i, consolidationSet);
        }
    }

    private void dump(int iteration, ConsolidationSet consolidationSet)
    {
        SortedMap<Long, Integer> distribution = new TreeMap<Long, Integer>(); // size -> count
        for (Element element : consolidationSet.snapshot().elements()) {
            Long size = element.count();
            Integer count = distribution.get(size);
            if (count == null) {
                distribution.put(size, 1);
            } else {
                distribution.put(size, count + 1);
            }
        }
        StringBuilder buffer = new StringBuilder();
        buffer.append(iteration);
        buffer.append(": ");
        boolean first = true;
        for (Map.Entry<Long, Integer> entry : distribution.entrySet()) {
            if (first) {
                first = false;
            } else {
                buffer.append(", ");
            }
            buffer.append(String.format("(%s: %s)", entry.getKey(), entry.getValue()));
        }
        System.out.println(buffer);
    }

    private static int idCounter = 0;

    private static class TestContainer implements Container
    {
        public Element consolidate(List<Element> elements,
                                   boolean inputDurable,
                                   boolean outputDurable)
        {
            int totalSize = 0;
            for (Element element : elements) {
                totalSize += element.count();
            }
            return new TestElement(idCounter++, totalSize);
        }

        public void replaceObsolete(List<Element> obsolete, Element replacement)
        {
        }

        public Configuration configuration()
        {
            fail();
            return null;
        }

        public Factory factory()
        {
            assert false;
            return null;
        }

        @Override
        public void reportCrash(Throwable crash)
        {
            fail();
        }
    }

    private static class TestElement implements Element
    {
        public long id()
        {
            return id;
        }

        public long count()
        {
            return size;
        }

        public long sizeBytes()
        {
            return 0; // Don't inhibit consolidation
        }

        public void destroyPersistentState()
        {
        }

        public void markDurable()
        {
            this.durable = true;
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

        public TestElement(int id, int size)
        {
            this.id = id;
            this.size = size;
        }

        private final int id;
        private final int size;
        private boolean durable = false;
        private List<Transaction> transactions = new ArrayList<Transaction>();
    }
}
