package com.interview.flag.g;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-12-31
 * Time: 下午7:27
 */
public class G4_OperationCountToSorted {
    private int count(int[] num) {
        int[] aux = new int[num.length];
        for (int i = 0; i < num.length; i++) aux[i] = num[i];
        Arrays.sort(aux);
        int index = 0;
        for (int i = 0; i < num.length; i++) {
            if (num[i] == aux[index]) index++;
        }
        return aux.length - index;
    }

    public static void main(String[] args){
        G4_OperationCountToSorted counter = new G4_OperationCountToSorted();
        int[] num = new int[]{4,1,3,5,2};
        System.out.println(counter.count(num)); //3
    }
}
