package com.github.shansun.concurrent.queue;

import java.util.PriorityQueue;

/**
 * http://hubingforever.blog.163.com/blog/static/1710405792010740234900
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-30
 */
public class PriorityQueueDemo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        PriorityQueue<String> pq = new PriorityQueue<String>();
        pq.add("dog");
        pq.add("apple");
        pq.add("fox");
        pq.add("easy");
        pq.add("boy");

        while (!pq.isEmpty()) {
            System.out.print("left: ");

            for (String s : pq) {
                System.out.print(s + " ");
            }

            System.out.println();

            System.out.println("poll(): " + pq.poll());
        }
    }

}