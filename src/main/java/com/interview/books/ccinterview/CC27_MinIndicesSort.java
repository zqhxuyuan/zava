package com.interview.books.ccinterview;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午11:18
 */
public class CC27_MinIndicesSort {
    public int[] findIndices(int[] array){
        int leftEnd = findEndOfLeftSubsequence(array);
        int rightBegin = findBeginOfRightSubsequence(array);

        int minIdx = leftEnd + 1;
        if(minIdx >= array.length) return new int[]{-1, -1};
        int maxIdx = rightBegin - 1;

        for(int i = leftEnd; i <= rightBegin; i++){
            if(array[i] < array[minIdx]) minIdx = i;
            else if(array[i] > array[maxIdx]) maxIdx = i;
        }

        leftEnd = shrinkLeft(array, minIdx, leftEnd);
        rightBegin = shrinkRight(array, maxIdx, rightBegin);
        return new int[]{leftEnd, rightBegin};
    }

    private int findEndOfLeftSubsequence(int[] array) {
        for(int i = 1; i < array.length; i++){
            if(array[i] < array[i - 1]) return i - 1;
        }
        return array.length - 1;
    }

    private int findBeginOfRightSubsequence(int[] array) {
        for(int i = array.length - 2; i >= 0; i--){
            if(array[i] > array[i + 1]) return i + 1;
        }
        return 0;
    }


    private int shrinkLeft(int[] array, int minIdx, int leftEnd) {
        while(leftEnd >= 0 && array[leftEnd] > array[minIdx]){
            leftEnd--;
        }
        return leftEnd + 1;
    }

    private int shrinkRight(int[] array, int maxIdx, int rightBegin) {
        while(rightBegin < array.length && array[rightBegin] < array[maxIdx]){
            rightBegin++;
        }
        return rightBegin - 1;
    }

    public static void main(String[] args){
        CC27_MinIndicesSort finder = new CC27_MinIndicesSort();
        int[] array = new int[]{1,2,4,7,10,11,7,12,6,7,16,18,19};
        int[] indices = finder.findIndices(array);
        System.out.println(indices[0] + ", " + indices[1]);    //3,9
    }
}
