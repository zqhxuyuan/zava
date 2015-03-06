package com.interview.books.ninechapter;

import java.util.Stack;

/**
 * Created_By: stefanie
 * Date: 14-12-12
 * Time: 下午6:07
 */
public class NC13_MinQueue {
    class MinStack{
        Stack<Integer> data = new Stack();
        Stack<Integer> min = new Stack<>();

        public void push(int element){
            data.push(element);
            if(min.isEmpty() || min.peek() >= element) min.push(element);
        }

        public int peek(){
            return data.peek();
        }

        public int pop(){
            int element = data.pop();
            if(element == min.peek()) min.pop();
            return element;
        }

        public int min(){
            if(min.isEmpty()) return Integer.MAX_VALUE;
            else return min.peek();
        }

        public boolean isEmpty(){
            return data.isEmpty();
        }
    }

    MinStack inStack = new MinStack();
    MinStack outStack = new MinStack();

    public void offer(int element){
        inStack.push(element);
    }

    public int poll(){
        if(outStack.isEmpty()){
            while(!inStack.isEmpty()) outStack.push(inStack.pop());
        }
        return outStack.pop();
    }

    public int peek(){
        if(outStack.isEmpty()){
            while(!inStack.isEmpty()) outStack.push(inStack.pop());
        }
        return outStack.peek();
    }

    public int min(){
        return Math.min(outStack.min(), inStack.min());
    }

    public static void main(String[] args){
        NC13_MinQueue queue = new NC13_MinQueue();
        queue.offer(5);
        queue.offer(6);
        System.out.println(queue.min());  //5
        queue.poll();
        System.out.println(queue.min());  //6
        queue.offer(3);
        System.out.println(queue.min());  //3
    }
}
