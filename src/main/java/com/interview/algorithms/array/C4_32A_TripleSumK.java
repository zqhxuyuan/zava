package com.interview.algorithms.array;

import com.interview.utils.ArrayUtil;
import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-10-23
 * Time: 下午10:45
 */
public class C4_32A_TripleSumK {
    static class Triple{
        int x;
        int y;
        int z;

        Triple(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public static List<Triple> solveTwoWay(int[] array, int sum){     //O(N^2)
        List<Triple> triples = new ArrayList<>();
        sort(array, 0, array.length - 1);                   //O(NlgN)
        ConsoleWriter.printIntArray(array);
        for(int i = 0; i < array.length - 1; i++){        //O(N)
            triples.addAll(solve2(array, i, sum - array[i]));   //O(N)
        }
        return triples;
    }

    private static List<Triple> solve2(int[] array, int k, int sum){
        List<Triple> triples = new ArrayList<>();
        int i = 0;
        int j = array.length - 1;
        while(i < j){
            if(i == k) {
                i++;
                continue;
            }
            if(j == k) {
                j--;
                continue;
            }
            int tmp = array[i] + array[j];
            if(tmp == sum) {
                triples.add(new Triple(array[i], array[j], array[k]));
                i++;
                j--;
            }
            else if(tmp < sum) i++;
            else j--;
        }
        return triples;
    }

    public static List<Triple> solveOneWay(int[] array, int sum){             //O(N^2lgN)
        List<Triple> triples = new ArrayList<>();
        sort(array, 0, array.length - 1);
        for(int i = 0; i < array.length - 1; i++){                //O(N^2)
            for(int j = i + 1; j < array.length; j++){
                int k = sum - array[i] - array[j];
                int index = find(array, k, 0, array.length - 1);    //O(lgN)
                if(index != -1 && index != i && index != j) triples.add(new Triple(array[i], array[j], k));
            }
        }
        return triples;
    }

    private static void sort(int[] array, int low, int high){
        if(low >= high) return;
        int i = low;
        for(int j = low + 1; j <= high; j++){
            if(array[j] < array[low]) ArrayUtil.swap(array, ++i, j);
        }
        ArrayUtil.swap(array, low, i);
        sort(array, low, i - 1);
        sort(array, i + 1, high);
    }

    private static int find(int[] array, int key, int low, int high){
        if(low > high) return -1;
        int mid = (high + low) / 2;
        if(key == array[mid]) return mid;
        else if(key < array[mid]) return find(array, key, low, mid - 1);
        else return find(array, key, mid + 1, high);
    }
}
