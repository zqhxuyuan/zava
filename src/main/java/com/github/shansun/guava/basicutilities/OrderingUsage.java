package com.github.shansun.guava.basicutilities;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

/**
 * 排序的用法
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-2
 */
public class OrderingUsage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // 生成排序规则
        Ordering<String> byLengthOrdering = new Ordering<String>() {

            @Override
            public int compare(String left, String right) {
                return Ints.compare(left.length(), right.length());
            }
        }.reverse().nullsFirst();

        List<String> list = Lists.newArrayList("hello", "world", "lan", "bo", "shansun");

        // 用指定规则排序
        System.out.println(byLengthOrdering.isOrdered(list));

        ImmutableList<String> immutableList = byLengthOrdering.immutableSortedCopy(list);

        System.out.println(immutableList);

        // 用默认规则排序
        ImmutableList<String> immutableList2 = Ordering.natural().immutableSortedCopy(list);

        // ImmutableList不允许修改: java.lang.UnsupportedOperationException
        // immutableList2.add("sfg");

        System.out.println(immutableList2);

        List<OrderingObject> list2 = Lists.newArrayList(new OrderingObject("hello"), new OrderingObject("world"), new OrderingObject("shansun"));

        // 使用toString后用默认规则排序
        @SuppressWarnings("static-access")
        ImmutableList<OrderingObject> immutableList3 = Ordering.natural().usingToString().immutableSortedCopy(list2);

        System.out.println(immutableList3);

        // 使用onResultOf和Function配合使用
        Ordering<Foo> ordering = Ordering.natural().nullsFirst().onResultOf(new Function<Foo, String>() {

            @Override
            public String apply(Foo input) {
                return input.sortedBy;
            }
        });

        List<Foo> list3 = Lists.newArrayList(new Foo("abc"), new Foo("defg"), new Foo("hijkl"));

        System.out.println(ordering.immutableSortedCopy(list3));

        // 获取最大的前k个数值
        System.out.println(ordering.greatestOf(list3, 2));

        // 获取最大的数值
        System.out.println(ordering.max(list3));

        // 获取可修改List
        List<Foo> mutableList = ordering.sortedCopy(list3);

        mutableList.add(new Foo("12345"));

        System.out.println(mutableList);
    }

    static class Foo {
        @Nullable
        String	sortedBy;
        int		notSortedBy;

        public Foo(String sortedBy, int notSortedBy) {
            super();
            this.sortedBy = sortedBy;
            this.notSortedBy = notSortedBy;
        }

        public Foo(String sortedBy) {
            super();
            this.sortedBy = sortedBy;
        }

        @Override
        public String toString() {
            return "Foo [sortedBy=" + sortedBy + "]";
        }
    }

    static class OrderingObject {
        private String	value;

        public OrderingObject(String val) {
            this.value = val;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}