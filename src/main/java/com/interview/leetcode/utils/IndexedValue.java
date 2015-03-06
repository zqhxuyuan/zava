package com.interview.leetcode.utils;

/**
 * Created_By: stefanie
 * Date: 14-11-13
 * Time: 下午1:39
 */
public class IndexedValue implements Comparable<IndexedValue>{
    public int value;
    public int offset;

    public IndexedValue(int value, int offset) {
        this.value = value;
        this.offset = offset;
    }

    @Override
    public int compareTo(IndexedValue o) {
        return value - o.value;
    }
}
