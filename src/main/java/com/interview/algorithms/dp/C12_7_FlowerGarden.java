package com.interview.algorithms.dp;

/**
 * Created_By: zouzhile
 * Date: 3/27/14
 * Time: 6:16 PM
 *
 * You are planting a flower garden with bulbs to give you joyous flowers throughout the year.
 * However, you wish to plant the flowers such that they do not block other flowers while they are visible.
 *
 * You will be given a int[] height, a int[] bloom, and a int[] wilt. Each type of flower is represented by
 * the element at the same index of height, bloom, and wilt. height represents how high each type of flower grows,
 * bloom represents the morning that each type of flower springs from the ground, and wilt represents the evening
 * that each type of flower shrivels up and dies. Each element in bloom and wilt will be a number between 1 and 365
 * inclusive, and wilt[i] will always be greater than bloom[i]. You must plant all of the flowers of the same type
 * in a single row for appearance, and you also want to have the tallest flowers as far forward as possible.
 * However, if a flower type is taller than another type, and both types can be out of the ground at the same time,
 * the shorter flower must be planted in front of the taller flower to prevent blocking. A flower blooms in the morning,
 * and wilts in the evening, so even if one flower is blooming on the same day another flower is wilting, one can
 * block the other.
 *
 * You should return a int[] which contains the elements of height in the order you should plant your flowers to
 * acheive the above goals. The front of the garden is represented by the first element in your return value,
 * and is where you view the garden from. The elements of height will all be unique, so there will always
 * be a well-defined ordering.
 *
 * Sample:
 * {5,4,3,2,1}
 * {1,1,1,1,1}
 * {365,365,365,365,365}
 * Returns: { 1,  2,  3,  4,  5 }
 *
 * {5,4,3,2,1}
 * {1,5,10,15,20}
 * {4,9,14,19,24}
 * Returns: { 5,  4,  3,  2,  1 }
 */
public class C12_7_FlowerGarden {

    public int[] getOrdering(int[] height, int[] bloom, int[] wilt) {
        int[] optimal = new int[height.length];
        int[] optimalBloom = new int[bloom.length];
        int[] optimalWilt = new int[wilt.length];

        // init state
        optimal[0] = height[0];
        optimalBloom[0] = bloom[0];
        optimalWilt[0] = wilt[0];

        // run dynamic programming
        for(int i = 1; i < height.length; i ++) {
            int currHeight = height[i];
            int currBloom = bloom[i];
            int currWilt = wilt[i];

            int offset = 0; // by default, type i is to be put to 1st row
            for(int j = 0; j < i; j ++) {
                if(currWilt >= optimalBloom[j] && currWilt <= optimalWilt[j] ||
                        currBloom >= optimalBloom[j] && currBloom <= optimalWilt[j] ||
                        currWilt >= optimalWilt[j] && currBloom <= optimalBloom[j]) {  // life period overlap
                    if(currHeight < optimal[j]) {  // life overlap, and type i is shorter than type j
                        offset = j;
                        break;
                    } else {
                        offset = j + 1; // type i overlap with type j, and i is taller than j. Put i after j
                    }
                } else {  // not overlap with current
                    if(currHeight < optimal[j]) {
                        offset = j + 1; // type i not overlap with j, i is shorter than j, put i after j
                    }
                    // else keep offset as is considering offset is smaller than j
                }
            }

            // shift the types after offset
            for(int k = i - 1; k >= offset; k -- ) {
                optimal[k+1] = optimal[k];
                optimalBloom[k+1] = optimalBloom[k];
                optimalWilt[k+1] = optimalWilt[k];
            }
            // update optimal
            optimal[offset] = currHeight;
            optimalBloom[offset] = currBloom;
            optimalWilt[offset] = currWilt;
        }
        return optimal;
    }
}
