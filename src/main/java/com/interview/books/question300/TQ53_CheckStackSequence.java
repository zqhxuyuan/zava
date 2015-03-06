package com.interview.books.question300;

import java.util.Stack;

/**
 * Created by stefanie on 1/21/15.
 */
public class TQ53_CheckStackSequence {
    public boolean check(int[] push, int[] pop){
        Stack<Integer> stack = new Stack();
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

    public static void main(String[] args){
        TQ53_CheckStackSequence checker = new TQ53_CheckStackSequence();
        int[] push = new int[]{1,2,3,4};
        int[] pop = new int[]{2,3,4,1};
        System.out.println(checker.check(push, pop)); //true
        pop = new int[]{2,4,3,1};
        System.out.println(checker.check(push, pop)); //true
        pop = new int[]{4,3,2,1};
        System.out.println(checker.check(push, pop)); //true
        pop = new int[]{4,2,3,1};
        System.out.println(checker.check(push, pop)); //false
        pop = new int[]{4,3,1,2};
        System.out.println(checker.check(push, pop)); //false
    }
}
