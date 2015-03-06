package com.interview.books.leetcode;

import com.interview.utils.ConsoleWriter;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Created_By: stefanie
 * Date: 14-12-11
 * Time: 上午10:58
 */
public class LCS6_MaxInSlidingWindow {

    public int[] maxElements(int[] A, int window){
        int[] B = new int[A.length - window + 1];
        Deque<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < window; i++) {
            while (!queue.isEmpty() && A[i] >= A[queue.peekLast()])
                queue.pollLast();
            queue.add(i);
        }

        for(int i = window; i < A.length; i++){
            B[i-window] = A[queue.peekFirst()];
            while (!queue.isEmpty() && A[i] >= A[queue.peekLast()])
                queue.pollLast();
            if(!queue.isEmpty() && queue.peekFirst() <= i - window)
                queue.pollFirst();
            queue.add(i);
        }
        B[A.length - window] = A[queue.peekFirst()];
        return B;
    }

    public static void main(String[] args){
        LCS6_MaxInSlidingWindow maxFinder = new LCS6_MaxInSlidingWindow();
        int[] A = new int[]{1, 3, -1, -3, 5, 3, 6, 7};
        int[] B = maxFinder.maxElements(A, 3);
        //3,3,5,5,6,7
        ConsoleWriter.printIntArray(B);
    }
}
