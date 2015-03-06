package com.interview.basics.model.collection.list;

import java.util.Iterator;

/**
 * Created_By: stefanie
 * Date: 14-7-1
 * Time: 下午10:14
 */

public class LinkedList<T> implements List<T> {
    Node<T> head;
    int size;

    public LinkedList(){

    }

    public LinkedList(Node head){
        this.head = head;
    }

    public LinkedList(T[] array){
        if(array.length == 0)   return;
        head = new Node(array[0]);
        Node prev = head;
        for(int i = 1; i < array.length; i++){
            Node next = new Node(array[i]);
            prev.next = next;
            prev = next;
        }
    }

    @Override
    public void add(T element) {
        add(size, element);
    }

    @Override
    public void add(int index, T element) {
        Node node = new Node(element);
        if (index >= 0 && index <= size) {
            if (index == 0) {
                node.next = head;
                head = node;
            } else {
                Node<T> current = head;
                for (int i = 0; i < index - 1; i++) current = current.next;
                node.next = current.next;
                current.next = node;
            }
            size++;
        }
    }

    @Override
    public T get(int index) {
        if (!checkIndex(index)) return null;
        Node<T> node = getNode(index);
        if (node != null) return node.item;
        else return null;
    }

    @Override
    public void set(int index, T element) {
        if (checkIndex(index)){
            Node<T> node = getNode(index);
            if (node != null) node.item = element;
        }
    }

    @Override
    public int indexOf(T element) {
        int index = 0;
        for (Node<T> current = head; current != null && !current.item.equals(element); current = current.next) index++;
        if (index >= size) return -1;
        else return index;
    }

    @Override
    public boolean contains(T element) {
        int index = indexOf(element);
        return index >= 0;
    }

    @Override
    public T remove(int index) {
        if (!checkIndex(index)) return null;
        int i = 0;
        Node<T> current = head;
        Node<T> previous = null;
        while (i++ < index) {
            previous = current;
            current = current.next;
        }
        size--;
        return current.item;
    }

    @Override
    public T remove(T element) {
        Node<T> current = head;
        if(element.equals(current.item))    head = head.next;
        else{
            while(current.next != null){
                if(element.equals(current.next.item)){
                    current.next = current.next.next;
                    size--;
                    break;
                }
                current = current.next;
            }
        }
        return element;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size <= 0;
    }

    @Override
    public T[] toArray() {
        T[] arr = (T[]) new Object[size];
        int i = 0;
        for(Node current = head; current!= null; current = current.next){
            arr[i++] = (T)current.item;
        }
        return arr;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node<T> current = null;
            int cursor = -1;
            @Override
            public boolean hasNext() {
                return cursor + 1 < size;
            }

            @Override
            public T next() {
                if(current == null) current = head;
                else current = current.next;
                cursor++;
                return current.item;
            }

            @Override
            public void remove() {
                if(current.next != null){
                    current.item = current.next.item;
                    current.next = current.next.next;
                } else {
                    current = null;
                }
            }
        };
    }

    @Override
    public void addAll(List<T> list) {
        Iterator<T> itr = list.iterator();
        while(itr.hasNext()){
            this.add(itr.next());
        }
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    public Node<T> getNode(int index) {
        int i = 0;
        Node<T> current = head;
        while (i++ < index) current = current.next;
        return current;
    }

    private boolean checkIndex(int index) {
        if (index >= 0 && index < size) return true;
        else return false;
    }

    public Node<T> getHead() {
        return head;
    }

    public void setHead(Node<T> node){
        this.head = node;
    }

    public void resize(){
        int count = 0;
        Node element = head;
        while(element != null){
            element = element.next;
            count++;
        }
        this.size = count;
    }

    public Node<T> getTail(){
        Node<T> element = head;
        while(element.next != null) element = element.next;
        return element;
    }
}
