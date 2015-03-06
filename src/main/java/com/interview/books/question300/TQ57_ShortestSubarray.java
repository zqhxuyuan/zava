package com.interview.books.question300;

import java.util.Arrays;

/**
 * Created by stefanie on 1/21/15.
 */
public class TQ57_ShortestSubarray {
    public int[] shortest(int[] array, int M) {
        int shortest = Integer.MAX_VALUE;
        int shortestBegin = -1;
        int shortestEnd = -1;

        int begin = 0;
        int end = 0;

        int[] indexes = new int[M + 1];
        Arrays.fill(indexes, -1);

        int count = 0;
        while (begin < array.length) {
            int decodedEnd = end % array.length;
            
            if(count != 0 && decodedEnd == begin) break;
            
            if (indexes[array[decodedEnd]] == -1) count++;
            indexes[array[decodedEnd]] = decodedEnd;
            
            if (count == M) {
                while (indexes[array[begin]] != begin) begin++;
                int length = end - begin + 1;
                if (length < shortest) {
                    shortest = length;
                    shortestBegin = begin;
                    shortestEnd = decodedEnd;
                }
            }
            end++;
        }
        return new int[]{shortestBegin, shortestEnd};
    }

    public static void main(String[] args) {
        TQ57_ShortestSubarray finder = new TQ57_ShortestSubarray();
        int[] array = new int[]{1, 1, 3, 2, 2, 4, 3, 1};
        int[] position = finder.shortest(array, 4);
        System.out.println(position[0] + ", " + position[1]);  //4, 7

        array = new int[]{1, 1, 3, 2, 2, 4, 3};
        position = finder.shortest(array, 4);
        System.out.println(position[0] + ", " + position[1]);  //4, 0
    }
}
