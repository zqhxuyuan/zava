package com.interview.algorithms.array;

import com.interview.basics.sort.RadixSorter;
import com.interview.utils.ArrayUtil;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/11/14
 * Time: 6:05 PM
 * Given an int array, write code to remove duplicate numbers. Time O(N), Space O(1)
 *
 * Remove duplicate can be simplify with sort, so need a O(N) sort: Counter/Radix/Bucket.
 *      Counter/Bucket need large space, so use Radix sort.
 * Refer: http://blog.csdn.net/hawksoft/article/details/6867493
 *
 * Radix Sort could also use O(10) space for numbers [0-9], Refer to RadixSorter
 */
public class C4_55_DuplicateRemover {

    public static Integer[] remove(Integer[] numbers){
        sort(numbers);
        int i = 0;
        int j = 1;
        while(j < numbers.length){
            if(numbers[i] != numbers[j]){
                numbers[++i] = numbers[j];
            }
            j++;
        }
        while(++i < numbers.length) numbers[i] = 0;
        return numbers;
    }

    private static void sort(Integer[] numbers){
        RadixSorter<Integer> sorter = new RadixSorter<>();
        sorter.sort(numbers);
    }

    //radix sort code
    private static void sortInPlace(Integer[] numbers) {
        int theN = numbers.length;
        //从高位到低位开始排序，这里从31位开始，32位是符号位不考虑，或者单独考虑。
        for (int i = 31; i >= 1; i--) {
            //当前排序之前的值，只有该值相同才进行快排分组，如果不相同，则重新开始另外一次快排
            //这很关键，否则快排的不稳定就会影响最后结果.
            int thePrvCB = numbers[0] >> (i);
            //快排开始位置,会变化
            int theS = 0;
            //快排插入点
            int theI = theS - 1;
            //2进制基数，用于测试某一位是否为0
            int theBase = 1 << (i - 1);
            //位基元始终为0，
            int theAxBit = 0;

            //分段快排，但总体上时间复杂度与快排分组一样.
            for (int j = 0; j < theN; j++) {
                //获取当前数组值的前面已拍过序的位数值。
                int theTmpPrvCB = numbers[j] >> (i);
                //如果前面已排过的位不相同，则重新开始一次快排.
                if (theTmpPrvCB != thePrvCB) {
                    theS = j;
                    theI = theS - 1;
                    theAxBit = 0;
                    thePrvCB = theTmpPrvCB;
                    j--;//重新开始排，回朔一位.
                    continue;
                }
                //如果前面的数相同，则寻找第1个1,thI指向其
                //如果相同，则按快排处理
                int theAJ = (numbers[j] & (theBase)) > 0 ? 1 : 0;
                ;//(A[j] & (theBase)) > 0 ? 1 : 0;(A[j] >> (i - 1)) & 1
                //如果是重新开始排，则寻找第1个1，并人theI指向其.这可以减少交换，加快速度.
                if (theI < theS) {
                    if (theAJ == 0) {
                        continue;
                    }
                    theI = j;//Continue保证J从theI+1开始.
                    continue;
                }
                //交换.
                if (theAJ <= theAxBit) {
                    ArrayUtil.swap(numbers, j, theI);
                    theI++;
                }
            }
        }
    }
}
