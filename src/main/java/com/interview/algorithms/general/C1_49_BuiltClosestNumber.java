package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-8-21
 * Time: 下午9:55
 */
public class C1_49_BuiltClosestNumber {

    public static int build(int[] numbers, int K){
        int[] p = getByBit(K);
        int[] q = new int[p.length];
        int i = p.length - 1;
        for(; i >= 0; i--){
            Integer j = find(numbers, p[i]);
            if(j == null){
                break;
            }
            q[i] = numbers[j];
            if(numbers[j] > p[i]){
                for(int k = i-1; k >= 0; k--) q[k] = numbers[0];
                return getNumber(q);
            }
        }

        for(i = i+1; i < q.length; i++){
            int index = find(numbers, q[i]);
            if(index + 1 < numbers.length){
                q[i] = numbers[index + 1];
                for(int k = i-1; k >= 0; k--) q[k] = numbers[0];
                return getNumber(q);
            }
        }

        q = new int[p.length + 1];
        if(numbers[0] == 0) q[q.length - 1] = numbers[1];
        for(int j = q.length - 2; j >= 0; j--) q[j] = numbers[0];
        return getNumber(q);
    }

    public static int[] getByBit(int n){
        int length = String.valueOf(n).length();
        int[] number = new int[length];
        for(int i = 0; i < length; i++){
            number[i] = n % 10;
            n = n / 10;
        }
        return number;
    }

    public static int getNumber(int[] num){
        int n = num[num.length - 1];
        for(int i = num.length - 2; i >= 0; i--){
            n = n * 10;
            n += num[i];
        }
        return n;
    }

    public static Integer find(int[] numbers, int k){
        return find(numbers, k, 0, numbers.length - 1);
    }

    public static Integer find(int[] numbers, int k, int low, int high){
        if(low > high) return null;
        int mid = low + (high - low) / 2;
        if(k == numbers[mid])   return mid;
        else if(k > numbers[mid])  return find(numbers, k, mid + 1, high);
        else {
            Integer larger = find(numbers, k, low, mid-1);
            if(larger == null) return mid;
            else return larger;
        }
    }
}
