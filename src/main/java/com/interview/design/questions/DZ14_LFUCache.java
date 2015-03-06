package com.interview.design.questions;

import java.util.HashMap;

/**
 * Created_By: stefanie
 * Date: 14-12-18
 * Time: 上午8:58
 */
public class DZ14_LFUCache {
    class Node{
        int key;
        int value;
        Node prev;
        Node next;
        long frequency = 0;

        public Node(int key, int value){
            this.key = key;
            this.value = value;
        }
    }
    private HashMap<Integer, Node> map = new HashMap<>();
    private HashMap<Long, Node> frequencies = new HashMap<>();
    private int count = 0;
    private int capacity = 10;
    private long lowestFreq = 0;

    public DZ14_LFUCache(int capacity) {
        this.capacity = capacity;
    }

    public Integer get(int key){
        Node node = getNode(key);
        if(node == null) return null;
        else return node.value;
    }

    private Node getNode(int key){
        if(!map.containsKey(key)){
            return null;
        }
        Node node = map.get(key);
        if(node.prev == null && node.next == null){ //only one node
            frequencies.remove(node.frequency);
            if(node.frequency == lowestFreq){
                lowestFreq++;
            }
        } else  {
            if(node.prev != null) node.prev.next = node.next;
            else frequencies.put(node.frequency, node.next);
            if(node.next != null) node.next.prev = node.prev;
        }
        node.frequency++;
        if(!frequencies.containsKey(node.frequency)){
            frequencies.put(node.frequency, node);
        } else {
            Node head = frequencies.get(node.frequency);
            node.next = head.next;
            if(head.next != null) head.next.prev = node;
            head.next = node;
            node.prev = head;
        }
        return node;
    }

    public void set(int key, int value){
        Node node = getNode(key);
        if(node != null){
            node.value = value;
            return;
        }
        node = new Node(key, value);
        if(count == capacity){
            //clean lowest frequency node;
            Node head = frequencies.get(lowestFreq);
            if(head != null){
                map.remove(head.key);
                head = head.next;
                count--;
            }
            if(head == null) frequencies.remove(lowestFreq);
            else frequencies.put(lowestFreq, head);
        }
        lowestFreq = 0;
        map.put(key, node);
        frequencies.put(node.frequency, node);
        count++;
    }


    public static void main(String[] args){
        DZ14_LFUCache cache = new DZ14_LFUCache(2);
        cache.set(1, 1);
        System.out.println(cache.get(1));    //1
        System.out.println(cache.get(1));    //1
        cache.set(2, 2);
        cache.set(3, 3);
        System.out.println(cache.get(1));    //1
        System.out.println(cache.get(2));    //null
        System.out.println(cache.get(3));    //3
        cache.set(4, 4);
        System.out.println(cache.get(1));    //1
        System.out.println(cache.get(2));    //null
        System.out.println(cache.get(3));    //null
        System.out.println(cache.get(4));    //4

        cache = new DZ14_LFUCache(2);
        cache.set(1, 1);
        cache.set(2, 2);
        System.out.println(cache.get(1));    //1
        System.out.println(cache.get(2));    //2
        cache.set(3, 3);
        System.out.println(cache.get(1));    //null
        System.out.println(cache.get(2));    //2
        System.out.println(cache.get(3));    //3

    }
}
