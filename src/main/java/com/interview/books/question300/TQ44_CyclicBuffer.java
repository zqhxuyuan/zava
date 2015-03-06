package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 15-1-20
 * Time: 下午3:10
 */
public class TQ44_CyclicBuffer<T> {
    T[] buffer;
    int size;
    int begin;
    int end;

    public TQ44_CyclicBuffer(int capacity){
        buffer = (T[]) new Object[capacity];
        begin = 0;
        end = 0;
        size = 0;
    }

    private int next(int index){
        return (index + 1) % buffer.length;
    }

    public void push(T element){
        if(size >= buffer.length) throw new RuntimeException("Buffer is Full");
        buffer[end] = element;
        end = next(end);
        size++;
    }

    public T poll(){
        if(size == 0) throw  new RuntimeException("Buffer is Empty");
        T element = buffer[begin];
        begin = next(begin);
        size--;
        return element;
    }

    public static void main(String[] args){
        TQ44_CyclicBuffer<Integer> buffer = new TQ44_CyclicBuffer(3);
        buffer.push(1);
        buffer.push(2);
        buffer.push(3);
        try{
            buffer.push(4);
        } catch (RuntimeException e){
            System.out.println(e.getMessage());  //Buffer is Full
        }
        System.out.println(buffer.poll());    //1
        buffer.push(4);
        System.out.println(buffer.poll());    //2
        System.out.println(buffer.poll());    //3
        System.out.println(buffer.poll());    //4
        try{
            buffer.poll();
        } catch (RuntimeException e){
            System.out.println(e.getMessage()); //Buffer is Empty
        }
    }
}
