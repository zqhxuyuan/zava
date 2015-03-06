package com.interview.books.ccinterview;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午12:26
 */
public class CC8_SortStack {
    public void sort(Stack<Comparable> stack){
        Stack<Comparable> backup = new Stack<>();
        while(!stack.isEmpty()){
            Comparable element = stack.pop();
            while(!backup.isEmpty() && element.compareTo(backup.peek()) < 0) stack.push(backup.pop());
            backup.push(element);
        }
        while(!backup.isEmpty()) stack.push(backup.pop());
    }

    public static void main(String[] args){
        Stack<Comparable> stack = new Stack<>();
        stack.push(3);
        stack.push(1);
        stack.push(2);
        stack.push(4);

        CC8_SortStack sorter = new CC8_SortStack();
        sorter.sort(stack);

        while(!stack.isEmpty()) System.out.println(stack.pop());
    }
}
