package com.interview.algorithms.stackqueue;

import com.interview.basics.model.collection.stack.Stack;

/**
 * Created_By: stefanie
 * Date: 14-7-31
 * Time: 下午11:17
 */
public class C7_8_ReverseStack {

    public static void reverse(Stack stack){
        Object tmp = stack.pop();
        if(stack.size() > 0) reverse(stack);
        addAtBottom(stack, tmp);
    }

    public static void addAtBottom(Stack stack, Object obj){
        if(stack.isEmpty())   stack.push(obj);
        else {
            Object obj2 = stack.pop();
            addAtBottom(stack, obj);
            stack.push(obj2);
        }
    }
}
