package com.interview.algorithms.array;

import com.interview.basics.sort.QuickSorter;

/**
 * Created_By: stefanie
 * Date: 14-9-4
 * Time: 下午4:00
 *
 * Given an unsorted array of number, find the max difference between the numbers in sorted order.   time O(N), space O(N)
 *
 * Solution: using bucket sort.
 *  1. find the min and max O(N)
 *  2. create N bucket, bucket[i][0] is the min value in this bucket, and bucket[i][1] is the max value.
 *  3. place the n number in bucket:   O(N)
 *      3.1 the n numbers in range of [min,max], the min difference is (max-min)/n-1 (bar), define this as the bar to do the bucket placement.
 *      3.2 for each number the index of bucket is pos = (number[i] - min)/ bar
 *      3.3 in several numbers fall in one bucket only trace the min and max number.
 *  4. the max diff should be the diff between bucket (avoid empty bucket).
 *
 *  The key point of this solution is: the min value of the diff should be (max-min)/n-1, so use this as the range of bucket.
 */
public class C4_51_MaxDifference {
    static QuickSorter<Integer> SORTER = new QuickSorter<Integer>();

    public static int find(Integer[] numbers){
        int n = numbers.length;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < n; i++){
            if(numbers[i] > max) max = numbers[i];
            if(numbers[i] < min) min = numbers[i];
        }

        int bar = (max-min)/(n-1);
        int[][] bucket = new int[n][2];

        for (int i = 0; i < n; i++){
            int pos = (numbers[i] - min) / bar;
            if (bucket[pos][0] == 0){
                bucket[pos][0] = bucket[pos][1] = numbers[i];
            } else {
                if (numbers[i] > bucket[pos][1])    bucket[pos][1] = numbers[i];
                if (numbers[i] < bucket[pos][0])    bucket[pos][0] = numbers[i];
            }
        }

        int maxDiff = 0;
        for (int i = 1; i < n; i++){
            if (bucket[i][0] != 0){
                int j = i - 1;
                while(bucket[j][0] == 0) j--;
                int diff = bucket[i][0] - bucket[j][1];
                if (maxDiff < diff) maxDiff = diff;
            }
        }
        return maxDiff;
    }




    public static int correctAnswer(Integer[] numbers){
        SORTER.sort(numbers);
        int max = 0;
        for(int i = 0; i < numbers.length - 1; i++){
            int diff = numbers[i + 1] - numbers[i];
            if(diff > max) max = diff;
        }
        return max;
    }
}
