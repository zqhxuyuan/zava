package com.interview.books.leetcodeoj;

import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 上午10:37
 */
public class LOJ46_Permutation {
    //try to put every element in List and use a boolean[] to avoid duplication
    //to de dup: check if previous element with same value all used, if not have duplication
    List<List<Integer>> sols;
    public List<List<Integer>> permute(int[] num) {
        sols = new ArrayList();
        List<Integer> cur = new ArrayList();
        Arrays.sort(num);
        boolean[] used = new boolean[num.length];
        permute(num, cur, used);
        return sols;
    }

    public void permute(int[] num, List<Integer> current, boolean[] used){
        if(current.size() == num.length) {
            sols.add(new ArrayList(current));
            return;
        }
        for(int i = 0; i < num.length; i++){
            if(!used[i]){
                current.add(num[i]);
                used[i] = true;
                boolean noDup = true;
                for(int j = i; j > 0 && num[j - 1] == num[j]; j--){
                    if(used[j - 1] == false) {
                        noDup = false;
                        break;
                    }
                }
                if(noDup) permute(num, current, used);
                current.remove(current.size() - 1);
                used[i] = false;
            }
        }
    }

    public static void main(String[] args){
        LOJ46_Permutation permutater = new LOJ46_Permutation();
        int[] nums = new int[]{1,1,2};
        List<List<Integer>> sols = permutater.permute(nums);
        ConsoleWriter.print(sols);
    }
}
