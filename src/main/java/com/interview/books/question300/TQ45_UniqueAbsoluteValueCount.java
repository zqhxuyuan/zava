package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 15-1-20
 * Time: 下午3:35
 */
public class TQ45_UniqueAbsoluteValueCount {
    public int count(int[] array){
        int count = 0;
        int begin = 0;
        int end = array.length - 1;

        while(begin <= end){
            int left = Math.abs(array[begin]);
            int right = Math.abs(array[end]);

            count++;

            if(left >= right){
                do {
                    begin++;
                } while(begin <= end && array[begin] == array[begin - 1]);
            }

            if(left <= right) {
                do {
                    end--;
                } while (begin <= end && array[end] == array[end + 1]);
            }
        }
        return count;
    }

    public static void main(String[] args){
        TQ45_UniqueAbsoluteValueCount counter = new TQ45_UniqueAbsoluteValueCount();
        int[] array = new int[]{-4, -4, -2, 0, 0, 0, 1, 2, 3, 4, 5};
        System.out.println(counter.count(array)); //6
    }
}
