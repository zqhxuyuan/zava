package com.interview.design.questions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 14-12-4
 * Time: 下午8:37
 */
public class DZ14_LRUCache {
    class Node{
        int key;
        int value;
        Node prev;
        Node next;
        public Node(int key, int value){
            this.key = key;
            this.value = value;
        }
    }
    Node head = null;
    Node tail = null;
    int capacity = 0;
    Map<Integer, Node> map;

    public DZ14_LRUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<Integer, Node>();
    }

    public int get(int key) {
        Node node = getNode(key);
        return node == null? -1 : node.value;
    }

    private Node getNode(int key){
        Node node = map.get(key);
        if(node == null) return null;
        if(head == node) return node;

        //remove node from the list
        node.prev.next = node.next;
        if(node.next != null) node.next.prev = node.prev;
        else tail = node.prev;

        //add node after head
        head.prev = node;
        node.next = head;
        head = node;
        node.prev = null;
        return node;
    }

    public void set(int key, int value) {
        Node node = getNode(key);
        if(node != null){
            node.value = value;
        } else {
            if(map.size() == capacity){
                map.remove(tail.key);
                tail = tail.prev;
                if(tail != null) tail.next = null;
                else head = null;
            }
            node = new Node(key, value);
            map.put(key, node);

            if(head != null)    head.prev = node;
            else tail = node;
            node.next = head;
            head = node;
        }
    }
}
