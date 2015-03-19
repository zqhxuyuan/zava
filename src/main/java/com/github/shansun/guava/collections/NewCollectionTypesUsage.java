package com.github.shansun.guava.collections;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Table;

/**
 * Guava中新引入的一些集合类型
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-2
 */
public class NewCollectionTypesUsage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        useMultiset();

        System.out.println("______________________________________________\r\n");

        useMultimap();

        System.out.println("______________________________________________\r\n");

        useBiMap();

        System.out.println("______________________________________________\r\n");

        useTable();
    }

    /**
     * Multiset允许对一个值添加多次，并可以在上面做统计. 注意Multiset不是Map. <br>
     * 下面是JDK里map对应的Multiset：<br>
     * HashMap ~ HashMultiset <br>
     * TreeMap ~ TreeMultiset <br>
     * LinkedHashMap ~ LinkedHashMultiset <br>
     * CocurrentHashMap ~ ConcurrentHashMultiset <br>
     * ImmutableMap ~ ImmutableMultiset <br>
     */
    static void useMultiset() {
        Multiset<String> wordsMultiset = HashMultiset.<String> create();
        wordsMultiset.add("hello");
        wordsMultiset.add("world");
        wordsMultiset.add("where'r u");
        wordsMultiset.add("hello");
        wordsMultiset.add("guava");

        System.out.println(wordsMultiset);

        System.out.println(wordsMultiset.count("hello"));

        System.out.println(wordsMultiset.size());

        // Remove 2 hello
        wordsMultiset.remove("hello", 2);
        System.out.println(wordsMultiset);
    }

    /**
     * Multimap允许对一个键添加多个值，并可以在上面做统计等。<br>
     */
    static void useMultimap() {
        Multimap<String, String> multimap = HashMultimap.<String, String> create();
        multimap.put("hello", "world");
        multimap.put("hello", "guava");
        multimap.put("hello", "java");
        multimap.put("lanbo", "shansun");

        System.out.println(multimap);

        System.out.println(multimap.get("hello"));

        // 将Multimap转为Map<K, Collection<V>>
        System.out.println(multimap.asMap());

        System.out.println(multimap.values().size());

        // 将指定key的值替换掉, 返回值为旧值
        System.out.println(multimap.replaceValues("lanbo", Lists.newArrayList("xujun")));
    }

    /**
     * BiMap(双向Map)保证值也是唯一的，同时支持key和value反转过来
     */
    static void useBiMap() {
        BiMap<String, String> dict = HashBiMap.<String, String> create();
        dict.put("aubergine", "egglant");
        dict.put("jam", "jelly");
        dict.put("courgette", "zucchini");
        // java.lang.IllegalArgumentException: value already present: jelly
        // dict.put("jam2", "jelly");

        System.out.println(dict.get("jam"));

        // 将BiMap的key和value反转
        dict = dict.inverse();

        System.out.println(dict.get("jelly"));

        // 将BiMap的key和value重置
        dict = dict.inverse();

        // 强制设置value，如果value已经存在，则覆盖原有值
        dict.forcePut("jam3", "jelly");

        System.out.println(dict.get("jam"));

        System.out.println(dict.inverse().get("jelly"));
    }

    /**
     * Table很强大: HashBasedTable、TreeBasedTable、ImmutableTable、ArrayTable
     */
    static void useTable() {
        Table<Character, Integer, String> aTable = HashBasedTable.<Character, Integer, String> create();

        // 创建一个表格
        for (char a = 'A'; a <= 'C'; a++) {
            for (int b = 1; b <= 3; b++) {
                // 第一个参数是rowKey，第二个参数是columnKey，第三个参数是值
                aTable.put(a, b, String.format("%c%d", a, b));
            }
        }

        // 获取列为2的内容， 返回值为Map<Character, String>
        System.out.println(aTable.column(2));

        // 获取行为A的内容，返回值为Map<Integer, String>
        System.out.println(aTable.row('A'));

        // 获取列为2、行为B的内容
        System.out.println(aTable.get('B', 2));

        // 测试是否包含
        System.out.println(aTable.contains('N', 4));

        // 测试是否包含列
        System.out.println(aTable.containsColumn(3));

        // 测试是否包含行
        System.out.println(aTable.containsRow('G'));

        // 将Table转化为Map<K1, Map<K2, V>>
        System.out.println(aTable.columnMap());

        // 将Table转化为Map<K1, Map<K2, V>>
        System.out.println(aTable.rowMap());

        // 移除一个值，返回值为当前值
        System.out.println(aTable.remove('B', 3));
    }
}