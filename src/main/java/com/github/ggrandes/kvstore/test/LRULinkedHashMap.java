package com.github.ggrandes.kvstore.test;

import java.util.*;

/**
 * Created by zqhxuyuan on 15-3-31.
 * <p>
 * LRU是Least Recently Used 近期最少使用算法。
 * 通过HashLiekedMap实现LRU的算法的关键是，如果map里面的元素个数大于了缓存最大容量，则删除链表头元素
 * <p>
 * public LinkedHashMap(int initialCapacity,float loadFactor,boolean accessOrder)
 * LRU参数参数：
 * initialCapacity - 初始容量。
 * loadFactor - 加载因子（需要是按该因子扩充容量）。
 * accessOrder - 排序模式( true) - 对于访问顺序（get一个元素后，这个元素被加到最后，使用了LRU  最近最少被使用的调度算法），对于插入顺序，则为 false,可以不断加入元素。
 * <p>
 * 相关思路介绍：
 * 当有一个新的元素加入到链表里面时，程序会调用LinkedHahMap类中Entry的addEntry方法，
 * 而该方法又会 会调用removeEldestEntry方法，这里就是实现LRU元素过期机制的地方，
 * 默认的情况下removeEldestEntry方法只返回false，表示可以一直表链表里面增加元素，在这个里  *修改一下就好了。
 * <p>
 * <p>
 * 测试数据：
 * 11
 * 7 0 7 1 0 1 2 1 2 6
 */
public class LRULinkedHashMap<K,V> extends LinkedHashMap<K,V>{
    private int capacity;                     //初始内存容量

    LRULinkedHashMap(int capacity){          //构造方法，传入一个参数
        super(16,0.75f,true);               //调用LinkedHashMap，传入参数
        this.capacity=capacity;             //传递指定的最大内存容量
    }

    @Override
    public boolean removeEldestEntry(Map.Entry<K, V> eldest){
        //，每加入一个元素，就判断是size是否超过了已定的容量
        System.out.println("此时的size大小="+size());
        if((size()>capacity)) {
            System.out.println("超出已定的内存容量，把链表顶端元素移除："+eldest.getValue());
        }
        return size()>capacity;
    }

    public static void main(String[] args) throws Exception{//方便实例，直接将异常抛出
        Scanner cin = new Scanner(System.in);

        System.out.println("请输入总共内存页面数： ");
        int n = cin.nextInt();
        Map<Integer,Integer> map=new LRULinkedHashMap<Integer, Integer>(n);

        System.out.println("请输入按顺序输入要访问内存的总共页面数： ");
        int y = cin.nextInt();

        System.out.println("请输入按顺序输入访问内存的页面序列： ");
        for(int i=1;i<=y;i++) {
            int x = cin.nextInt();
            map.put(x,  x);
        }
        System.out.println("此时内存中包含的页面数是有:");
        //遍历此时内存中的页面并输出
        for(java.util.Map.Entry<Integer, Integer> entry: map.entrySet()){
            System.out.println(entry.getValue());
        }
    }
}
