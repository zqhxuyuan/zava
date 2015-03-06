/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.geophile.erdo.consolidate;

import com.geophile.erdo.map.SealedMap;
import com.geophile.erdo.map.emptymap.EmptyMap;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.geophile.erdo.consolidate.Consolidation.Element;
import static com.geophile.erdo.util.Math.log2;

// Tracks a set of Consolidation.Elements as they move from being available for consolidation,
// to being consolidated, and then to obsolete. All Elements managed by one tracker are alike
// in their durability status

abstract class ConsolidationElementTracker
{
    // ConsolidationElementTracker interface

    public abstract String type();

    public List<Element> elements()
    {
        assert Thread.holdsLock(container);
        List<Element> elements = new ArrayList<>(nonEmptyAvailableForConsolidation);
        elements.addAll(beingConsolidated);
        return elements;
    }

    public abstract void add(Element element);

    public abstract void removeElementBeingConsolidated(Element element);

    public abstract void beingConsolidated(List<Element> elements);

    public abstract List<Element> availableForConsolidation();

    public long totalBytes()
    {
        assert Thread.holdsLock(container);
        long size = 0;
        for (Element element : nonEmptyAvailableForConsolidation) {
            size += element.sizeBytes();
        }
        for (Element element : beingConsolidated) {
            size += element.sizeBytes();
        }
        return size;
    }

    public int totalRecords()
    {
        assert Thread.holdsLock(container);
        int count = 0;
        for (Element element : nonEmptyAvailableForConsolidation) {
            count += element.count();
        }
        for (Element element : beingConsolidated) {
            count += element.count();
        }
        return count;
    }

    public double complexity()
    {
        assert Thread.holdsLock(container);
        double sumLog = 0;
        for (Element element : nonEmptyAvailableForConsolidation) {
            long n = element.count();
            if (n > 0) {
                sumLog += log2(n);
            }
        }
        for (Element element : beingConsolidated) {
            long n = element.count();
            if (n > 0) {
                sumLog += log2(n);
            }
        }
        return sumLog;
    }

    public void consolidationFailed(List<Element> elements)
    {
        assert Thread.holdsLock(container);
        for (Element element : elements) {
            if (beingConsolidated.remove(element)) {
                LOG.log(Level.WARNING, "Moving {0} back to durable due to failed consolidation", element);
                add(element);
            }
        }
    }

    public void describe(Logger log, Level level, String label)
    {
        describe(log, level, label, nonEmptyAvailableForConsolidation, "non-empty available");
        describe(log, level, label, beingConsolidated, "beingConsolidated");
    }

    public static ConsolidationElementTracker durable(Consolidation.Container container)
    {
        return new DurableConsolidationElementTracker(container);
    }

    public static ConsolidationElementTracker nonDurable(Consolidation.Container container)
    {
        return new NonDurableConsolidationElementTracker(container);
    }

    // For use by subclasses

    protected ConsolidationElementTracker(Consolidation.Container container)
    {
        this.container = container;
    }

    protected void describe(Logger log,
                            Level level,
                            String label,
                            Set<Element> elements,
                            String subLabel)
    {
        long recordCount = 0;
        for (Element element : elements) {
            recordCount += element.count();
        }
        log.log(level,
                "{0} {1} {2}: {3}/{4}",
                new Object[]{
                    label,
                    type(),
                    subLabel,
                    elements.size(),
                    recordCount});
    }

    // For use by this class

    private String describeDistribution(Collection<Element> elements)
    {
        StringBuilder buffer = new StringBuilder();
        Map<Long, Integer> counts = new TreeMap<>();
        for (Element element : elements) {
            Integer frequency = counts.get(element.count());
            if (frequency == null) {
                frequency = 0;
            }
            frequency = frequency + 1;
            counts.put(element.count(), frequency);
        }
        for (Map.Entry<Long, Integer> entry : counts.entrySet()) {
            if (buffer.length() > 0) {
                buffer.append(", ");
            }
            buffer.append(entry.getValue());
            buffer.append(" x ");
            buffer.append(entry.getKey());
        }
        return buffer.toString();
    }

    // Class state

    private static final Logger LOG = Logger.getLogger(ConsolidationElementTracker.class.getName());

    // Object state

    protected final Consolidation.Container container;
    protected final Set<Element> nonEmptyAvailableForConsolidation = new HashSet<>();
    protected final Set<Element> beingConsolidated = new HashSet<>();

    // Inner classes

    public static class DurableConsolidationElementTracker extends ConsolidationElementTracker
    {
        @Override
        public String type()
        {
            return "durable";
        }

        @Override
        public void add(Element element)
        {
            // Empty element should not go to emptyAvailableForConsolidation, due to bug 4.
            assert element.durable();
            assert Thread.holdsLock(container);
            nonEmptyAvailableForConsolidation.add(element);
        }

        @Override
        public void removeElementBeingConsolidated(Element element)
        {
            assert Thread.holdsLock(container);
            boolean removed = beingConsolidated.remove(element);
            assert removed : element;
        }

        @Override
        public void beingConsolidated(List<Element> elements)
        {
            assert Thread.holdsLock(container);
            for (Element element : elements) {
                boolean removed = nonEmptyAvailableForConsolidation.remove(element);
                assert removed;
                boolean added = beingConsolidated.add(element);
                assert added;
            }
        }

        @Override
        public List<Element> availableForConsolidation()
        {
            assert Thread.holdsLock(container);
            // Copy so that the caller has a static view
            ArrayList<Element> available = new ArrayList<>(nonEmptyAvailableForConsolidation);
            if (LOG.isLoggable(Level.FINER) && !available.isEmpty()) {
                LOG.log(Level.FINER, "available for consolidation: {0}", available);
            }
            return available;
        }

        public DurableConsolidationElementTracker(Consolidation.Container container)
        {
            super(container);
        }
    }

    public static class NonDurableConsolidationElementTracker extends ConsolidationElementTracker
    {
        @Override
        public String type()
        {
            return "nonDurable";
        }

        @Override
        public List<Element> elements()
        {
            List<Element> elements = super.elements();
            if (!emptyAvailableForConsolidation.timestamps().empty()) {
                // Copy so that the caller has a static view
                elements.add(emptyAvailableForConsolidation.copy());
            }
            return elements;
        }

        @Override
        public void add(Element element)
        {
            assert !element.durable();
            assert Thread.holdsLock(container);
            if (element.count() == 0) {
                emptyAvailableForConsolidation.addEmpty((SealedMap) element);
            } else {
                nonEmptyAvailableForConsolidation.add(element);
            }
        }

        @Override
        public void removeElementBeingConsolidated(Element element)
        {
            assert Thread.holdsLock(container);
            boolean removed = beingConsolidated.remove(element);
            assert removed : element;
        }

        @Override
        public void beingConsolidated(List<Element> elements)
        {
            assert Thread.holdsLock(container);
            for (Element element : elements) {
                if (element.count() == 0) {
                    emptyAvailableForConsolidation.removeEmpty((SealedMap) element);
                } else {
                    boolean removed = nonEmptyAvailableForConsolidation.remove(element);
                    assert removed;
                }
                boolean added = beingConsolidated.add(element);
                assert added;
            }
        }

        @Override
        public List<Element> availableForConsolidation()
        {
            assert Thread.holdsLock(container);
            // Copy so that the caller has a static view
            ArrayList<Element> available = new ArrayList<>(nonEmptyAvailableForConsolidation);
            if (!emptyAvailableForConsolidation.timestamps().empty()) {
                available.add(emptyAvailableForConsolidation.copy());
            }
            if (LOG.isLoggable(Level.FINER) && !available.isEmpty()) {
                LOG.log(Level.FINER, "available for consolidation: {0}", available);
            }
            return available;
        }

        @Override
        public void describe(Logger log, Level level, String label)
        {
            super.describe(log, level, label);
            describe(log, level, label, Collections.<Element>singleton(emptyAvailableForConsolidation), "empty available");
        }

        public NonDurableConsolidationElementTracker(Consolidation.Container container)
        {
            super(container);
            this.emptyAvailableForConsolidation = new EmptyMap(container.factory());
        }

        private  final EmptyMap emptyAvailableForConsolidation;
    }
}
