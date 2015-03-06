package com.interview.design.questions;


import com.interview.basics.model.tree.RedBlackTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-8
 * Time: 下午6:04
 *
 * Design a data structure that can implements the following 2 search method in O(lgN)
 *   1. public V get(K key), return the value which key is key
 *   2. public List<V> getRange(K key1, K key2) return all the values which key is between key1 and key2
 */
public class DZ16_MagicMap<K extends Comparable<K>, V> {
    private HashMap<K, V> map = new HashMap<>();
    private RedBlackTree<K> tree = new RedBlackTree<>();

    public void add(K key, V value){
        if(!map.containsKey(key)){
            tree.insert(key);
        }
        map.put(key, value);
    }

    public V get(K key){
        return map.get(key);
    }

    public List<V> getRange(K key1, K key2){
        List<K> keys = tree.searchRange(key1, key2);
        List<V> values = new ArrayList<V>();
        for(K key : keys){
            values.add(map.get(key));
        }
        return values;
    }

    public void remove(K key){
        tree.delete(key);
        map.remove(key);
    }
}
