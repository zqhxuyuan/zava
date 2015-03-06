package com.interview.algorithms.stackqueue;

import com.interview.basics.model.collection.stack.LinkedStack;
import com.interview.basics.model.collection.stack.Stack;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 7/15/14
 * Time: 1:49 PM
 */
public class C7_7_StackSorter<T extends Comparable> {

    public void sort(Stack<T> stack){
        Stack<T> ordered = new LinkedStack<>();
        while(!stack.isEmpty()){
            T tmp = stack.pop();
            while(!ordered.isEmpty() && ordered.peek().compareTo(tmp) > 0)
                stack.push(ordered.pop());
            ordered.push(tmp);
        }

        while(!ordered.isEmpty()) stack.push(ordered.pop());
    }
}
