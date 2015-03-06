package com.interview.algorithms.list;

import com.interview.basics.model.collection.list.LinkedList;
import com.interview.basics.model.collection.list.Node;
import com.interview.basics.model.collection.stack.LinkedStack;
import com.interview.basics.model.collection.stack.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/11/14
 * Time: 1:10 PM
 */
public class C3_12_ListPalindrome {

    public static boolean isParlindromeByStack(LinkedList<String> list){
        Node<String> p = list.getHead();
        Node<String> p2 = list.getHead();
        Stack<String> stack = new LinkedStack<String>();

        while(p2 != null && p2.next != null){
            stack.push(p.item);
            p = p.next;
            p2 = p2.next.next;
        }

        if(p2 != null && p2.next == null)   p = p.next; //for odd number

        while(p != null){
            if(p.item.equals(stack.pop()))  p = p.next;
            else return false;
        }

        return true;
    }

    public static boolean isParlindromeByRecursive(LinkedList<String> list){
        int size = list.size();
        Node<String> head = list.getHead();
        if(head == null || head.next == null) return true;
        else {
            Node<String> pair = pair(head.next, 1, (size - 1) / 2);
            if(pair == null || pair.next != null || !head.item.equals(pair.item)) return false;
            else return true;
        }

    }

    private static Node<String> pair(Node<String> node, int index, int size){
        if(index < size) {
            Node<String> pair = pair(node.next, ++index, size);
            if(pair == null || !node.item.equals(pair.item))    return null;
            return pair.next;
        } else if(index == size){
            if(node.next != null && node.item.equals(node.next.item)) return node.next.next;
            else return node.next;
        } else {
            return null;
        }
    }
}
