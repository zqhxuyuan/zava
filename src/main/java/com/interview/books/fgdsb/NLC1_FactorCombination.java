package com.interview.books.fgdsb;

import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-30
 * Time: 下午11:17
 */
public class NLC1_FactorCombination {
    List<List<Integer>> combinations;
    public List<List<Integer>> factors(int number){
        combinations = new ArrayList();
        List<Integer> current = new ArrayList();
        factors(number, 2, current, number/2);
        return combinations;
    }

    private void factors(int number, int factor, List<Integer> current, int threshold){
        if(number == 1) {
            combinations.add(new ArrayList(current));
            return;
        }
        for(int i = factor; i <= number && i <= threshold; i++){
            if(number % i == 0) {
                current.add(i);
                factors(number / i, i, current, threshold);
                current.remove(current.size() - 1);
            }
        }
    }


    public static void main(String[] args){
        NLC1_FactorCombination finder = new NLC1_FactorCombination();
        List<List<Integer>> combinations = finder.factors(12);
        for(List<Integer> combination : combinations) ConsoleWriter.printCollection(combination);
        //[[2, 2, 3], [2, 6], [3, 4]]

        combinations = finder.factors(15);
        for(List<Integer> combination : combinations) ConsoleWriter.printCollection(combination);
        //[[3, 5]]
    }
}
