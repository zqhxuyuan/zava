package com.interview.algorithms.array;

import com.interview.basics.model.collection.stack.ArrayStack;
import com.interview.basics.model.collection.stack.Stack;

/**
 * Created_By: stefanie
 * Date: 14-7-23
 * Time: 下午9:47
 */
public class C4_33_PushPopSequenceChecker {

    public static boolean check(int[] push, int[] pop){
        Stack<Integer> stack = new ArrayStack<>();
        int j = 0;
        for(int i = 0; i < push.length; i++){
            if(push[i] != pop[j]) stack.push(push[i]);
            else j++;
        }
        while(!stack.isEmpty()) {
            if(stack.pop() != pop[j++]) return false;
        }
        return true;
    }
}
