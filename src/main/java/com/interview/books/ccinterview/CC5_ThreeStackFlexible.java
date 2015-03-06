package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 上午11:45
 */
public class CC5_ThreeStackFlexible {
    static class StackData{
        public int start;
        public int pointer;
        public int size = 0;
        public int capacity;
        public StackData(int _start, int _capacity){
            start = _start;
            pointer = _start - 1;
            capacity = _capacity;
        }

        public boolean isWithinStack(int index, int totalSize){
            if(start <= index && index <= start + capacity) return true;
            else if(start + capacity > totalSize && index < (start + capacity) % totalSize) return true;
            return false;
        }
    }

    int stackCount;
    int defaultSize;
    int totalSize;
    StackData[] stacks;
    int[] buffer;


    public CC5_ThreeStackFlexible(int count, int capacity){
        this.stackCount = count;
        this.defaultSize = capacity;
        this.totalSize = defaultSize * stackCount;
        this.stacks = new StackData[]{
                new StackData(0, defaultSize),
                new StackData(defaultSize, defaultSize),
                new StackData(defaultSize * 2, defaultSize)
        };
        this.buffer = new int[totalSize];
    }

    public void push(int stackIdx, int value) throws Exception {
        StackData stack = stacks[stackIdx];
        if(stack.size >= stack.capacity){
            if(numberOfElements() >= totalSize){
                throw new Exception("Out of space");
            } else {
                expand(stackIdx);
            }
        }
        stack.size++;
        stack.pointer = nextElement(stack.pointer);
        buffer[stack.pointer] = value;
    }

    public int pop(int stackIdx) throws Exception {
        StackData stack = stacks[stackIdx];
        if(stack.size == 0){
            throw new Exception("Empty Stack");
        }
        int value = buffer[stack.pointer];
        buffer[stack.pointer] = 0;
        stack.pointer = previousElement(stack.pointer);
        stack.size--;
        return value;
    }

    public int peek(int stackIdx) throws Exception {
        StackData stack = stacks[stackIdx];
        if(stack.size == 0){
            throw new Exception("Empty Stack");
        }
        return buffer[stack.pointer];
    }

    public boolean isEmpty(int stackIdx){
        StackData stack = stacks[stackIdx];
        return stack.size == 0;
    }

    private int numberOfElements(){
        int total = 0;
        for(int i = 0; i < stacks.length; i++) total += stacks[i].size;
        return total;
    }

    private int nextElement(int index){
        if(index + 1 == totalSize) return 0;
        return index + 1;
    }

    private int previousElement(int index){
        if(index == 0) return totalSize - 1;
        else return index - 1;
    }

    private void shift(int stackIdx){
        StackData stack = this.stacks[stackIdx];
        if(stack.size >= stack.capacity){
            int nextStack = (stackIdx + 1) % stackCount;
            shift(nextStack);
            stack.capacity++;
        }
        for(int i = (stack.start + stack.capacity - 1) % totalSize; stack.isWithinStack(i, totalSize); i = previousElement(i)){
            buffer[i] = buffer[previousElement(i)];
        }
        buffer[stack.start] = 0;
        stack.start = nextElement(stack.start);
        stack.pointer = nextElement(stack.pointer);
        stack.capacity--;
    }

    private void expand(int stackIdx){
        shift((stackIdx + 1) % stackCount);
        stacks[stackIdx].capacity++;
    }

    public static void main(String[] args) throws Exception {
        CC5_ThreeStackFlexible stack = new CC5_ThreeStackFlexible(3, 2);
        stack.push(0, 1);
        stack.push(0, 2);
        stack.push(0, 3);
        stack.push(1, 1);
        stack.push(2, 1);
        stack.push(2, 2);
        try{
            stack.push(2, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(stack.isEmpty(0));
        System.out.println(stack.isEmpty(1));
        System.out.println(stack.isEmpty(2));
        System.out.println(stack.pop(2));
        System.out.println(stack.pop(0));
        System.out.println(stack.pop(0));
        System.out.println(stack.pop(1));
        try{
            System.out.println(stack.pop(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
