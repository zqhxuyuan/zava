package com.interview.flag.o;

import java.util.*;

/**
 * Created_By: stefanie
 * Date: 15-2-8
 * Time: 下午5:04
 */
public class O25_CountUnDividableNumbers {
    public int count(int N, List<Integer> numbers){
        numbers = clearNonPrim(numbers);
        if(numbers.contains(1)) return 0;

        int count = N;
        Map<Integer, List<Integer>> combination = new HashMap();
        combination.put(1, new ArrayList());
        for(int i = 0; i < numbers.size(); i++){
            count -= N / numbers.get(i);
            for(int j = i + 1; j > 1; j--){
                int flag = j % 2 == 0? 1 : -1;
                List<Integer> pre = combination.get(j-1);
                List<Integer> cur = new ArrayList();
                for(Integer num : pre){
                    int share = num * numbers.get(i);
                    count += flag * (N / share);
                    cur.add(share);
                }
                combination.put(j, cur);
            }
            combination.get(1).add(numbers.get(i));
        }
        return count;
    }

    public List<Integer> clearNonPrim(List<Integer> numbers){
        List<Integer> primes = new ArrayList();
        for(int i = 0; i < numbers.size(); i++){
            boolean prime = true;
            for(int j = 0; j < i; j++){
                if(numbers.get(i) % numbers.get(j) == 0){
                    prime = false;
                    break;
                }
            }
            if(prime) primes.add(numbers.get(i));
        }
        return primes;
    }

    public static void main(String[] args){
        O25_CountUnDividableNumbers counter = new O25_CountUnDividableNumbers();
        System.out.println(counter.count(10, Arrays.asList(new Integer[]{2,4,5}))); //4
    }
}
