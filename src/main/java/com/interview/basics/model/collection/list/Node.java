package com.interview.basics.model.collection.list;

/**
 * Created_By: stefanie
 * Date: 14-7-9
 * Time: 下午10:57
 */
public class Node<T> {
    public T item;
    public Node<T> next;

    public Node(T element) {
        item = element;
    }
}
