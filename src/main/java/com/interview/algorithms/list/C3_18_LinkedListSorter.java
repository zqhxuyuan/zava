package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.Node;

/**
 * Created_By: stefanie
 * Date: 14-8-6
 * Time: 下午8:09
 * <p/>
 * Write code to sort a linked list, specify which sort method you choice and why.
 * <p/>
 * For linked list sorting, usually mergesort is the best choice.
 * Pros: O(1) auxilary space, compared to array merge sort. No node creation, just pointer operations.
 */
public class C3_18_LinkedListSorter<T extends Comparable> {

    public void sort(LinkedList<T> list, boolean isAsc) {
        list.setHead(mergeSort(list.getHead(), list.size(), isAsc));
    }

    private Node<T> mergeSort(Node<T> node, int len, boolean isAsc) {
        if (len == 1) {
            node.next = null;
            return node;
        }
        Node mid = node;
        for (int i = 0; i < len / 2; i++)   mid = mid.next;
        Node n1 = mergeSort(node, len / 2, isAsc);
        Node n2 = mergeSort(mid, len - len / 2, isAsc);
        return merge(n1, n2, isAsc);
    }

    private Node<T> merge(Node<T> n1, Node<T> n2, boolean isAsc) {
        Node<T> tail = null;
        Node<T> head = null;
        while (n1 != null && n2 != null) {
            if ((isAsc && n1.item.compareTo(n2.item) > 0) || (!isAsc && n1.item.compareTo(n2.item) < 0)) {
                if (head == null) {
                    head = n2;
                    tail = n2;
                } else {
                    tail.next = n2;
                    tail = tail.next;
                }
                n2 = n2.next;
            } else {
                if (head == null) {
                    head = n1;
                    tail = n1;
                } else {
                    tail.next = n1;
                    tail = tail.next;
                }
                n1 = n1.next;
            }
        }
        tail.next = n1 == null? n2: n1;
        return head;
    }
}
