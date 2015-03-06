package com.interview.flag.g;

import com.interview.utils.ConsoleWriter;

import java.util.Random;

/**
 * Created_By: stefanie
 * Date: 15-1-7
 * Time: 上午10:16
 */
class Quack{
    static int UNSET = -1;
    static int HEAD = 0;
    static int TAIL = 1;

    Random random = new Random();
    int visit = -1;

    int[] elements;
    int head, tail;
    int size;

    public Quack(int capacity){
        elements = new int[capacity];
        head = 0;
        tail = 0;
        size = 0;
    }
    public void push(int element){
        elements[tail++] = element;
        size++;
    }

    public int peek(){
        visit = random.nextInt(2);
        if(visit == HEAD) return elements[head];
        else return elements[tail-1];
    }

    public int pop(){
        if(visit == UNSET) visit = random.nextInt(2);
        int element;
        if(visit == HEAD) element = elements[head++];
        else element = elements[--tail];
        size--;
        visit = UNSET;
        return element;
    }

    public int size(){
        return size;
    }

}
public class G20_SortedQuack {
    public int[] convert(Quack quack){
        int[] array = new int[quack.size()];
        int front = 0;
        int end = array.length - 1;
        int count = 0;
        int element = 0;
        while(quack.size() > 0){
            element = quack.pop();
            int next = quack.peek();
            if(element == next){
                count++;
            } else if(element > next){
                 array[end--] = element;
                 while(count > 0) {
                     array[end--] = element;
                     count--;
                 }
            } else {
                 array[front++] = element;
                 while(count > 0) {
                     array[front++] = element;
                     count--;
                 }
            }
        }
        while(count-- > 0) array[front++] = element;
        return array;
    }

    public static void main(String[] args){
        G20_SortedQuack converter = new G20_SortedQuack();
        int[] elements = new int[]{1,1,2,2,3,4,4,5,6,6,7,8,9,9};
        Quack quack = new Quack(14);
        for(int i = 0; i < 14; i++) quack.push(elements[i]);

        int[] array = converter.convert(quack);
        ConsoleWriter.printIntArray(array);
    }
}
