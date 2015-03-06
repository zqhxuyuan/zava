package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 上午11:27
 */
public class CC5_ThreeStack {
    int capacity = 100;
    int[] buffer;
    int[] idx = new int[]{-1, -1, -1};

    public CC5_ThreeStack(int capacity){
        this.capacity = capacity;
        buffer = new int[capacity * 3];
    }

    public void push(int stackIdx, int value) throws Exception {
        if(idx[stackIdx] + 1 >= capacity){
            throw new Exception("Out of space.");
        }
        idx[stackIdx]++;
        buffer[convert(stackIdx)] = value;
    }

    public int pop(int stackIdx) throws Exception {
        if(idx[stackIdx] == -1){
            throw new Exception("Empty stack");
        }
        int value = buffer[convert(stackIdx)];
        buffer[convert(stackIdx)] = 0;
        idx[stackIdx]--;
        return value;
    }

    public int peek(int stackIdx) throws Exception {
        if(idx[stackIdx] == -1){
            throw new Exception("Empty stack");
        }
        return buffer[convert(stackIdx)];
    }

    public boolean isEmpty(int stackIdx){
        return idx[stackIdx] == -1;
    }

    //covert the relative idx to global index
    private int convert(int stackIdx){
        return stackIdx * capacity + idx[stackIdx];
    }

    public static void main(String[] args) throws Exception {
        CC5_ThreeStack stack = new CC5_ThreeStack(2);
        stack.push(0, 1);
        stack.push(0, 2);
        try{
            stack.push(0, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stack.push(2, 1);
        System.out.println(stack.isEmpty(0));
        System.out.println(stack.isEmpty(1));
        System.out.println(stack.isEmpty(2));
        System.out.println(stack.pop(2));
        System.out.println(stack.pop(0));
        System.out.println(stack.pop(0));
        try{
            System.out.println(stack.pop(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
