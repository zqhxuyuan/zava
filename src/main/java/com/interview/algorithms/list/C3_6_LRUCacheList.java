package com.interview.algorithms.list;

import java.util.HashMap;
import java.util.Map;

/**
 * Created_By: stefanie
 * Date: 14-6-29
 * Time: 下午10:43
 */

public class C3_6_LRUCacheList<Key, Value> {
    class Node<Key, Value>{
        Key key;
        Value value;
        Node next;
        Node prev;

        public Node(Key key, Value value){
            this.key = key;
            this.value = value;
        }
    }
    private int N = 10;
    private Map<Key, Node<Key, Value>> map = new HashMap<Key, Node<Key, Value>>();

    private Node<Key, Value> head;
    private Node<Key, Value> tail;

    public C3_6_LRUCacheList(){

    }

    public C3_6_LRUCacheList(int N){
        this.N = N;
    }

    public void set(Key key, Value value){
        Node<Key, Value> node = map.get(key);
        if(node != null){
            node.value = value;
            visit(node);
        } else {
            if(map.size() == N){
                //need delete the tail element
                map.remove(tail.key);
                tail.prev.next = null;
                tail = tail.prev;
            }
            //insert the new element
            Node<Key, Value> newNode = new Node<Key, Value>(key, value);
            newNode.next = head;
            if(head != null)        head.prev = newNode;
            head = newNode;
            if(tail == null)        tail = newNode;
            map.put(key, newNode);
        }
    }

    public Value get(Key key){
        Node<Key, Value> node = map.get(key);
        if(node != null){
            if(node != head) {
               visit(node);
            }
            return node.value;
        } else {
            return null;
        }
    }

    private void visit(Node<Key, Value> node){
        //delete visited node in the list
        node.prev.next = node.next;
        if (node == tail) tail = node.prev;
        else node.next.prev = node.prev;
        //add the visited node as the header
        node.next = head;
        head.prev = node;
        node.prev = null;
        head = node;
    }

    public int size(){
        return map.size();
    }
}
