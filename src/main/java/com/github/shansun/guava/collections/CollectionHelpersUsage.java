package com.github.shansun.guava.collections;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.AbstractSequentialIterator;
import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-3
 */
public class CollectionHelpersUsage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        useForwardingDecorators();

        usePeekingIterator();

        useAbstractIterator();

        useAbstractSequentialIterator();
    }

    static void useForwardingDecorators() {
        List<String> list = Lists.newArrayList("123", "abc", "helloworld!", null);

        // Forwarding Decorators
        ListWithDefault<String> listWithDefault = new ListWithDefault<String>("default-value", list);

        Iterator<String> iterator = listWithDefault.iterator();

        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        for (String val : listWithDefault) {
            System.out.println(val);
        }
    }

    static void usePeekingIterator() {
        List<String> result = Lists.newArrayList();

        List<String> source = Lists.newArrayList("lanbo", "lanbo", "xujun", "shansun", "chris");

        PeekingIterator<String> peekingIterator = Iterators.peekingIterator(source.iterator());

        while (peekingIterator.hasNext()) {
            String current = peekingIterator.next();

            // 这里过滤掉重复的值(这里只处理连续的)
            while (peekingIterator.hasNext() && peekingIterator.peek().equals(current)) {
                peekingIterator.next();
            }

            result.add(current);
        }

        System.out.println(result);
    }

    static void useAbstractIterator() {
        List<String> source = Lists.newArrayList("lanbo", "xujun", null, "chris", null);

        Iterator<String> skipNulls = skipNulls(source.iterator());

        while (skipNulls.hasNext()) {
            System.out.println(skipNulls.next());
        }
    }

    static Iterator<String> skipNulls(final Iterator<String> in) {
        return new AbstractIterator<String>() {

            @Override
            protected String computeNext() {
                while (in.hasNext()) {
                    String s = in.next();
                    if (s != null) {
                        return s;
                    }
                }

                // 在computeNext方法中，必须要在迭代器完结后调用endOfData()方法
                return endOfData();
            }
        };
    }

    /**
     * AbstractSequentialIterator以前是AbstractLinkedIterator
     */
    static void useAbstractSequentialIterator() {
        Iterator<Integer> iterator = new AbstractSequentialIterator<Integer>(1) {

            @Override
            protected Integer computeNext(Integer previous) {
                return previous == 1 << 30 ? null : previous * 2;
            }
        };

        while (iterator.hasNext()) {
            System.out.print(iterator.next() + ",");
        }

        System.out.println();
    }

    /**
     * 可以编写自己的Collections类型
     *
     * @author: lanbo <br>
     * @version: 1.0 <br>
     * @date: 2012-7-3
     */
    static class ListWithDefault<E> extends ForwardingList<E> {

        final E			defaultValue;
        final List<E>	delegate;		// backing list

        public ListWithDefault(E defaultValue, List<E> delegate) {
            super();
            this.defaultValue = defaultValue;
            this.delegate = delegate;
        }

        @Override
        protected List<E> delegate() {
            return delegate;
        }

        @Override
        public E get(int index) {
            E v = super.get(index);
            return v == null ? defaultValue : v;
        }

        @Override
        public Iterator<E> iterator() {
            final Iterator<E> iterator = super.iterator();

            /**
             * 可以编写自己的迭代器
             */
            return new ForwardingIterator<E>() {

                @Override
                protected Iterator<E> delegate() {
                    return iterator;
                }

                @Override
                public E next() {
                    E next = super.next();
                    return next == null ? defaultValue : next;
                }
            };
        }
    }
}