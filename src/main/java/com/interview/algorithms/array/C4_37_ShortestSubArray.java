package com.interview.algorithms.array;

/**
 * Created_By: stefanie
 * Date: 14-7-27
 * Time: 下午1:49
 *
 * Given a array of N number which arrange is 1-M, write code to find the shortest sub array contains all 1-M numbers.
 *  Also consider if the array is cycle (connected head and tail).
 *
 * Use a int[] to calculate the occurance of number and tracking if all number appear.
 * When all the number appear, sink from begin, if its occurance more then once.
 * Then once visit a number, check if can sink to get a better solution.
 */

class SubArray{
    int start;
    int end;

    SubArray(int start, int end) {
        this.start = start;
        this.end = end;
    }
}
public class C4_37_ShortestSubArray {
    public static SubArray find(int[] array, int M){
        int N = array.length;
        SubArray result = new SubArray(0,N-1);
        int begin = 0;
        int[] mark = new int[M+1];
        int count = M;
        int i = 0;
        while(begin < N){      //Handle cycle, if no cycle, the condition should be i < N
            if(mark[array[i]] == 0){
                count--;
            }
            mark[array[i]]++;
            if(count == 0){ //start to sink
                while(begin < N){  //Handle cycle, if no cycle, the condition should be true
                    if(mark[array[begin]] > 1)   mark[array[begin++]]--;
                    else {
                        if(length(i, begin, N) < length(result.end, result.start, N)){
                            result.start = begin;
                            result.end = i;
                        }
                        break;
                    }
                }
            }
            if(i == array.length -1) i = 0;   //Handle cycle
            else i++;
        }
        return result;
    }

    public static int length(int end, int start, int N){
        return (end + N - start) % N;
    }
}
