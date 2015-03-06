package com.interview.flag.f;

import com.interview.utils.ConsoleWriter;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created_By: stefanie
 * Date: 15-1-9
 * Time: 上午8:29
 */
public class F9_SalaryBalancer {

    public int[] balance(final int[] current, int[] base){
        int available = 0;
        final Integer[] idxs = new Integer[current.length];

        for(int i = 0; i < current.length; i++){
            available += current[i] - base[i];
            current[i] = base[i];
            idxs[i] = i;
        }

        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return current[o1] - current[o2];
            }
        };

        Arrays.sort(idxs, comparator);

        int everyone = current[0];
        for(int i = 1; i < idxs.length; i++){
            if(current[idxs[i]] == current[idxs[i - 1]]) continue;
            int count = i;
            int diff = current[idxs[i]] - current[idxs[i - 1]];
            if(available >= count * diff){
                everyone = current[idxs[i]];
                available -= count * diff;
            } else {
                everyone += available / count;
                int rest = count - available % count;
                for(int j = 0; j < count; j++){
                    current[idxs[j]] = everyone + (j >= rest? 1 : 0);
                }
                break;
            }
        }
        return current;
    }

    public static void main(String[] args){
        F9_SalaryBalancer balancer = new F9_SalaryBalancer();
        int[] current = new int[]{20, 40, 33, 130, 90, 50, 110};     //110
        int[] base = new int[]{10, 30, 10, 100, 50, 50, 100};
        int[] salary = balancer.balance(current, base);
        ConsoleWriter.printIntArray(salary);   //54, 55, 54, 100, 55, 55, 100,
    }
}
