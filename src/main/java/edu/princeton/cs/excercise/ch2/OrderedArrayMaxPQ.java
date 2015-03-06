package edu.princeton.cs.excercise.ch2;

import edu.princeton.cs.introcs.StdOut;

/*************************************************************************
 *  Compilation:  javac OrderedArrayMaxPQ.java
 *  Execution:    java OrderedArrayMaxPQ
 *
 *  Priority queue implementation with an ordered array.
 *
 *  Limitations
 *  -----------
 *   - no array resizing
 *   - does not check for overflow or underflow.
 *
 *
 *************************************************************************/

public class OrderedArrayMaxPQ<Key extends Comparable<Key>> {
    private Key[] pq;          // elements
    private int N;             // number of elements

    // set inititial size of heap to hold size elements
    public OrderedArrayMaxPQ(int capacity) {
        pq = (Key[]) (new Comparable[capacity]);
        N = 0;
    }


    public boolean isEmpty() { return N == 0;  }
    public int size()        { return N;       }
    public Key delMax()      { return pq[--N]; }

    public void insert(Key key) {
        int i = N-1;
        while (i >= 0 && less(key, pq[i])) {
            pq[i+1] = pq[i];
            i--;
        }
        pq[i+1] = key;
        N++;
    }



    /***********************************************************************
     * Helper functions.
     **********************************************************************/
    private boolean less(Key v, Key w) {
        return (v.compareTo(w) < 0);
    }

    private void exch(int i, int j) {
        Key swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
    }


    /***********************************************************************
     * Test routine.
     **********************************************************************/
    public static void main(String[] args) {
        OrderedArrayMaxPQ<String> pq = new OrderedArrayMaxPQ<String>(10);
        pq.insert("this");
        pq.insert("is");
        pq.insert("a");
        pq.insert("test");
        while (!pq.isEmpty())
            StdOut.println(pq.delMax());
    }

}