package com.interview.leetcode.binarysearch;

/**
 * Created_By: stefanie
 * Date: 14-11-14
 * Time: 下午4:27
 *
 * Searching in array is a very typical algorithm, return its index if exist, return -1 if not found.
 * The condition of binarysearch can be divided as following cases:
 *  each case will have implementations **L as Loop version and **R as Recursion version.
 *
 * Sorted or Partial Sorted
 * 1. binarysearch given number in sorted array,   {@link SearchingSortedArray}
 * 2. binarysearch first/last/range appearance of a given number in a sorted array with duplication. {@link SearchingSortedArrayWithDuplication}
 * 3. binarysearch min/max/target in a given sorted rotated array. {@link SearchingRotatedArray}
 *      min: array[mid] < array[mid - 1] indicate mid is min
 *           else array[mid] >= array[high]  min is in the right part, otherwise in left part
 *      max: array[mid] > array[mid + 1] indicate mid is max
 *           else array[mid] >= array[low]   max is in the right part, otherwise in left part
 *      find: get mid, if array[mid] == target return mid;
 *           if(target < array[mid]) should search in left, but if array[mid] >= array[low] && target < array[low] (low ~ mid is increasing and target is smaller than low) binarysearch in right
 *           if(target > array[mid]) should search in right, but if array[mid] <= array[high] && target > array[high] (mid ~ high is increasing and target is larger than low), binarysearch in left.
 *
 * 4. binarysearch min/max/target in a given sorted rotated array with duplication.  {@link com.interview.leetcode.binarysearch.SearchingRotatedArrayWithDuplication}
 *    basically have the same methodology as 3, but when have duplication, need check mid/low/high
 *      if array[mid] == array[low] == array[high], can decide left or right, just move as usual:
 *          min/max, low++
 *          find: if(target < array[mid]) low++, if(target > array[mid]) high--;
 * 5. binarysearch min/target in an array first decrease than increase {@link SearchingVArray}
 *      min: array[mid-1] > array[mid] && array[mid+1] > array[mid]
 *          if array[mid-1] < array[mid] -> mid in increasing part, and search min in low ~ mid-1.
 *          if array[mid+1] < array[mid] -> mid in decreasing part, and search min in high ~ mid + 1;
 *          if array[mid+1] == array[mid] == array[mid-1], can decide which part to search, low++;
 *      find: get mid,
 *      if array[mid] == target return mid;
 *          if target < array[mid]
 *            if array[mid-1] > array[mid] && array[mid+1] > array[mid], mid is min, so return -1;
 *            if array[mid-1] < array[mid] -> mid in increasing part, and search min in low ~ mid-1.
 *            if array[mid+1] < array[mid] -> mid in decreasing part, and search min in high ~ mid + 1;
 *            if array[mid+1] == array[mid] == array[mid-1], can decide which part to search, low++;
 *          if target > array[mid], need search left part if array[low] > target and right part if array[high] > target
 * 6. binarysearch merged k-th element in two sorted array.  {@link SearchTwoSortedArray}
 *      tracking the low element in a and b, if no element in a or b, return low + k - 1
 *      if k = 1, return min(a_low, b_low)
 *          get half-k-th elements in A and B, if out of range, assign as Integer.MAX_VALUE
 *          if half-k-th elements in A < B's, elements before half-k-th element in A belongs to topK, otherwise half-k-th element in B belongs topK.
 *              so every time, it reduce K to a half size ( find half-k element belongs to topK)
 *          continue binarysearch for k - half.
 *
 * Unsorted Array
 * 1. binarysearch given number in unsorted array.  {@link SearchingUnsortedArray}
 *      quick select, find a pivot, if array[pivot] == target, return true, else partition the array using the pivot.
 *                    if target < array[pivot] binarysearch in low ~ pivot - 1
 *                    else binarysearch in pivot + 1 ~ high
 * 2. binarysearch K-th min/max element
 *      quick select, find a pivot, partition use this pivot, if right place of pivot is k, return array[pivot]
 *                    if(index > k), search k in low, pivot - 1;
 *                    if(index < k), search k in pivot + 1, high;
 *            notice index is the offset in whole array, so when index < k, still search k not k - index !important
 *
 * Tricks:
 *  1. in sorted array, use sorted feature to search half part of array
 *          based on the relation of low, high, mid, target for rotated array
 *          based on the relation of mid, mid - 1, mid + 1 for V array
 *  2. when the criteria is broken since array have duplication, array[mid] = array[low] = array[high] or array[mid] = array[mid-1] = array[mid+1]
 *          just do low++ or high-- to find a new mid
 *  3. In unsorted array, using pivot to partition and search in one half. O(lgn)
 *
 */
public class Searching {
}
