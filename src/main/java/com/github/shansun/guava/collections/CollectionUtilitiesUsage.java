package com.github.shansun.guava.collections;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.primitives.Ints;

/**
 * 常用集合工具类
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-2
 */
public class CollectionUtilitiesUsage {

    static List<String>			list;
    static Map<String, Object>	map;
    static Set<String>			set;

    static List<String>			exactly100;

    /**
     * @param args
     */
    public static void main(String[] args) {
        useStaticConstructors();

        useIterables();

        useFluentIterable();

        useLists();

        useSets();

        useMaps();

        useMultimaps();

        useTables();
    }

    static void useStaticConstructors() {
        // 这里使用静态构造方法，可以指定集合对象的容量等
        list = Lists.newArrayList();
        map = Maps.newLinkedHashMap();
        set = Sets.newHashSet();
    }

    static void useIterables() {
        Iterable<Integer> concatenated = Iterables.concat(Ints.asList(1, 2, 3), Ints.asList(4, 5, 6), Ints.asList(7, 8, 9));

        System.out.println(concatenated);

        Integer last = Iterables.getLast(concatenated);

        System.out.println(last);

        Predicate<Integer> greaterThanFive = new Predicate<Integer>() {

            @Override
            public boolean apply(Integer input) {
                return input > 5;
            }
        };

        System.out.println(Iterables.all(concatenated, greaterThanFive));

        System.out.println(Iterables.any(concatenated, greaterThanFive));

        // 过滤
        System.out.println(Iterables.filter(concatenated, greaterThanFive));

        // 计算频率
        System.out.println(Iterables.frequency(concatenated, 5));
    }

    /**
     * 使用流畅的迭代器
     */
    static void useFluentIterable() {
        final Collection<String> filtered = FluentIterable //
                .from(Lists.newArrayList("hello", "world", "lanbo", "shansun", "xujun")) //
                .transform(new Function<String, String>() { //

                    @Override
                    public String apply(String input) {
                        return input == null ? "" : Strings.repeat(input, 3);
                    }
                }) //
                .filter(new Predicate<String>() { //

                    @Override
                    public boolean apply(String input) {
                        return !input.contains("r");
                    }
                }) //
                .limit(3) //
                .skip(1) //
                .toImmutableList();

        System.out.println(filtered);
    }

    static void useLists() {
        List<Integer> countUp = Ints.asList(1, 2, 3, 4, 5);

        List<Integer> countDown = Lists.reverse(countUp);

        System.out.println(countDown);

        List<List<Integer>> parts = Lists.partition(countUp, 2);

        System.out.println(parts);
    }

    static void useSets() {
        Set<String> wordsWithPrimeLength = ImmutableSet.of("one", "two", "three", "six", "seven", "eight");
        Set<String> primes = ImmutableSet.of("two", "three", "five", "seven");

        // 集合交集的计算
        SetView<String> intersection = Sets.intersection(primes, wordsWithPrimeLength);

        ImmutableSet<String> immutableSet = intersection.immutableCopy();

        System.out.println(immutableSet);

        Set<String> animals = ImmutableSet.of("gerbil", "hamster");
        Set<String> fruits = ImmutableSet.of("apple", "orange", "banana");

        // 集合的并集运算 Sets.union

        // 集合的差异运算 Sets.different

        // 集合笛卡尔积运算
        @SuppressWarnings("unchecked")
        Set<List<String>> cartesianProduct = Sets.<String> cartesianProduct(animals, fruits);

        System.out.println(cartesianProduct);

        // 集合子集运算
        Set<Set<String>> powerSet = Sets.powerSet(animals);

        System.out.println(powerSet);
    }

    static void useMaps() {
        List<String> strings = Lists.newArrayList("hello", "world!", "lan", "bo", "shansun");

        // 一个对象集合，我们知道他们有一些独特（可相互区分、唯一性）的属性，并且我们希望通过这些属性的值找到相关的对象时
        // 可以选择使用Maps.uniqueIndex去生成一个key为根据指定属性生成的唯一值
        ImmutableMap<Integer, String> uniqueIndex = Maps.uniqueIndex(strings, new Function<String, Integer>() {

            @Override
            public Integer apply(String input) {
                return input.length();
            }
        });

        System.out.println(uniqueIndex);

        ImmutableMap<String, Integer> left = ImmutableMap.<String, Integer> of("a", 1, "b", 2, "c", 3);
        ImmutableMap<String, Integer> right = ImmutableMap.<String, Integer> of("b", 2, "c", 4, "d", 5);

        // 比较两个Map对象
        MapDifference<String, Integer> difference = Maps.difference(left, right);

        // 两边都有的条目
        System.out.println(difference.entriesInCommon());

        // 两边值不同的条目
        System.out.println(difference.entriesDiffering());

        // 只有左边有的条目
        System.out.println(difference.entriesOnlyOnLeft());

        // 只有右边有的条目
        System.out.println(difference.entriesOnlyOnRight());
    }

    static void useMultisets() {
        // NOTHING
    }

    /**
     * Multimap允许一个key对应多个value，也允许多个key对应一个value
     */
    static void useMultimaps() {
        ImmutableSet<String> immutableSet = ImmutableSet.of("zero", "one", "two", "three", "four");

        // 按字符串长度索引
        Function<String, Integer> lengthFunc = new Function<String, Integer>() {

            @Override
            public Integer apply(String input) {
                return input == null ? -1 : input.length();
            }
        };

        // Multimaps.index与Maps.uniqueIndex不同的是，前者不要求索引项的唯一性
        ImmutableListMultimap<Integer, String> multimap = Multimaps.index(immutableSet, lengthFunc);

        System.out.println(multimap);

        Map<String, Integer> map = ImmutableMap.of("a", 1, "b", 1, "c", 2);

        // 将普通map转为Multimap
        SetMultimap<String, Integer> multimap2 = Multimaps.forMap(map);

        // 将map执行拷贝，并key和value置位
        HashMultimap<Integer, String> inverse = Multimaps.invertFrom(multimap2, HashMultimap.<Integer, String> create());

        System.out.println(inverse);
    }

    static void useTables() {
        // 新建一张Table
        Table<String, Character, Integer> table = Tables.newCustomTable(Maps.<String, Map<Character, Integer>> newLinkedHashMap(), new Supplier<Map<Character, Integer>>() {

            @Override
            public Map<Character, Integer> get() {
                return Maps.newLinkedHashMap();
            }
        });

        table.put("Row#1", 'A', 2);

        System.out.println(table.get("Row#1", 'A'));
    }
}