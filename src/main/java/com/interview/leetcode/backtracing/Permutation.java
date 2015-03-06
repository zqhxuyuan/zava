package com.interview.leetcode.backtracing;

import com.interview.utils.ConsoleWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 14-11-24
 * Time: 下午3:20
 */
public class Permutation {

    static class KPermuation{
        List<List<Integer>> sols;
        public List<List<Integer>> permute(int[] num, int K) {
            sols = new ArrayList<>();
            List<Integer> current = new ArrayList<Integer>();
            boolean[] used = new boolean[num.length];
            Arrays.sort(num);
            permute(num, current, K, used);
            return sols;
        }

        public void permute(int[] num, List<Integer> current, int K, boolean[] used){
            if(current.size() == K){
                sols.add(new ArrayList<Integer>(current));
                return;
            }
            for(int i = 0; i < num.length; i++){
                if(used[i] == true) continue;
                used[i] = true;
                current.add(num[i]);
                //de-dup
                boolean hasDup = false;
                for(int j = i; j > 0 && num[j - 1] == num[j]; j--){
                    if(used[j - 1] == false) {
                        hasDup = true;
                        break;
                    }
                }
                if(!hasDup) permute(num, current, K, used);
                current.remove(current.size() - 1);
                used[i] = false;
            }
        }
    }

    static class PermuationSequence{

        /**
         * scan from right to left, find the min number in the right that larger than current element,
         * if exist, switch it with current, and sort the element in the right.
         * @param num
         */
        public void nextPermutation(int[] num) {
            for(int i = num.length - 2; i >= 0; i--){
                int min = -1;
                for(int j = num.length - 1; j > i; j--){
                    if(num[j] > num[i] && (min == -1 || num[j] < num[min])) min = j;
                }
                if(min != -1){
                    int temp = num[i];
                    num[i] = num[min];
                    num[min] = temp;
                    Arrays.sort(num, i + 1, num.length);
                    return;
                }
            }
            Arrays.sort(num);
        }

        public String getPermutation(int n, int k) {
            int[] factors = factors(n);
            k--;
            k = k % factors[n];
            StringBuilder options = new StringBuilder("123456789");
            StringBuilder result = new StringBuilder();
            while(n > 0){
                int index = k / factors[n - 1];
                char ch = options.charAt(index);
                options.deleteCharAt(index);
                result.append(ch);
                k = k - factors[n - 1] * index;
                n--;
            }
            return result.toString();
        }

        public int[] factors(int n){
            int[] factors = new int[n + 1];
            factors[0] = 1;
            for(int i = 1; i <= n; i++) factors[i] = factors[i - 1] * i;
            return factors;
        }

        public static void main(String[] args){
            PermuationSequence sequence = new PermuationSequence();
//            System.out.println(sequence.getPermutation(3, 1));
//            System.out.println(sequence.getPermutation(3, 2));
//            System.out.println(sequence.getPermutation(3, 3));
            int[] num = new int[]{1,3,2};
            sequence.nextPermutation(num);
            ConsoleWriter.printIntArray(num);
        }
    }
}
