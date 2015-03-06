package com.interview.algorithms.list;

import java.util.HashMap;

/** 
 * Created_By : zhaoxm (xmpy) 
 * Date : 2014-3-17 
 * Time : 下午4:05:44
 * 
 * LeetCode – LRU Cache (Java)
 * http://oj.leetcode.com/problems/lru-cache/
 * 
 * Design and implement a data structure for Least Recently Used (LRU) cache. It should support the following operations: get and set.
 * 
 * get(key) – Get the value (will always be positive) of the key if the key exists in the cache, otherwise return -1.
 * set(key, value) – Set or insert the value if the key is not already present. When the cache reached its capacity, it should invalidate the least recently used item before inserting a new item.
 * 
 */

class DoubleLinkedListNode {
    public int val;
    public int key;
    public DoubleLinkedListNode pre;
    public DoubleLinkedListNode next;
    
    
    public DoubleLinkedListNode(int key, int val){
        this.key = key;
    	this.val = val;
    }
}

public class C3_6_LRUCache {
    private int capacity;
    private HashMap<Integer, DoubleLinkedListNode> dict = new HashMap<Integer, DoubleLinkedListNode>();
    private DoubleLinkedListNode head;
    private int count;
    private DoubleLinkedListNode last;
    
    public C3_6_LRUCache(int capacity) {
        this.capacity = capacity;
        count = 0;
        head = new DoubleLinkedListNode(0,0);
        last = head;
    }
    
    public int get(int key) {
        if(dict.containsKey(key)){
            DoubleLinkedListNode old = dict.get(key);
            if(old == last) //已经是最后一个了
            	return old.val;
            old.pre.next = old.next;
            if(old.next != null)
            	old.next.pre = old.pre;
            last.next = old;
            old.next = null;
            old.pre = last;
            last = old;
            return old.val;
        }else{
            return -1;
        }
    }
    
    public void set(int key, int value) {
        if(dict.containsKey(key)){
            DoubleLinkedListNode old = dict.get(key);
            if(old == last){
            	last.val = value;
            	return;
            } 
            	
            old.pre.next = old.next;
            if(old.next != null)
            	old.next.pre = old.pre;
            DoubleLinkedListNode temp = new DoubleLinkedListNode(key,value);
            dict.put(key, temp);
            last.next = temp;
            temp.next = null;
            temp.pre = last;
            last = temp;
            
        }else{
            if(count + 1 > capacity){
                //delete the node
            	if(last == head.next){ //only have one element
            		last = head;
            	}
            	if(head.next != null){
            		dict.remove(head.next.key);
            		head.next  = head.next.next;
            		if(head.next != null)
            			head.next.pre = head;
            	}
            	
                count -= 1;
            }
            DoubleLinkedListNode temp = new DoubleLinkedListNode(key,value);
            dict.put(key, temp);
            last.next = temp;
            temp.next = null;
            temp.pre = last;
            last = temp;
            count += 1;
        }
    }
    
    public static void main(String[] args){
    	C3_6_LRUCache l = new C3_6_LRUCache(2);
    	l.set(2, 1);
    	l.set(2, 2);

    	System.out.println(l.get(2));
    	l.set(1, 1);
    	l.set(4, 1);

    	System.out.println(l.get(2));
    	System.out.println(l.get(4));


    }
}
