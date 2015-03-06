package com.interview.basics.model.collection.heap;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 10/15/14
 * Time: 1:49 PM
 *
 * 操作       二叉堆(最坏)     二项堆(最坏)     斐波那契堆(平摊)
 * __________________________________________________________
 * MAKE-HEAP      Θ(1)            Θ(1)            Θ(1)
 * INSERT         Θ(lg n)         O(lg n)         Θ(1)
 * MINIMUM        Θ(1)            O(lg n)         Θ(1)
 * EXTRACT-MIN    Θ(lg n)         Θ(lg n)         O(lg n)
 * UNION          Θ(n)            O(lg n)         Θ(1)
 * DECREASE-KEY   Θ(lg n)         Θ(lg n)         Θ(1)
 * DELETE         Θ(lg n)         Θ(lg n)         O(lg n)
 */
public class FibonacciHeap<T extends Comparable<T>> implements Heap<T> {


    @Override
    public void add(T element) {

    }

    @Override
    public T getHead() {
        return null;
    }

    @Override
    public T pollHead() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean contains(T k) {
        return false;
    }
}
