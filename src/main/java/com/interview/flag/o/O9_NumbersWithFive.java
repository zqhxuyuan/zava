package com.interview.flag.o;

import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-14
 * Time: 上午11:54
 */
public class O9_NumbersWithFive {
    public List<Integer> numbers(int upbound){
        List<Integer> numbers = new ArrayList();
        if(upbound < 5) return numbers;

        numbers.add(5);
        int base = 10;

        while(upbound / base > 0){
            List<Integer> current = new ArrayList();
            int largest = upbound/(base * 10) == 0? upbound/base : 10; //if highest digit
            for(int i = 1; i < largest; i++){
                if(i == 5){
                    for(int j = 0; j < base; j++) current.add(i * base + j);
                } else {
                    for(Integer number : numbers) current.add(i * base + number);
                }
            }
            if(largest != 10){
                int lower = upbound % base;
                if(largest == 5){
                    for(int j = 0; j < lower; j++) current.add(largest * base + j);
                } else {
                    for(int j = 0; j < numbers.size() && numbers.get(j) < lower; j++)
                        current.add(largest * base + numbers.get(j));
                }
            }
            numbers.addAll(current);
            base = base * 10;
        }
        return numbers;
    }

    public static void main(String[] args){
        O9_NumbersWithFive finder = new O9_NumbersWithFive();
        List<Integer> numbers = finder.numbers(552);
        ConsoleWriter.printCollection(numbers);

    }
}
