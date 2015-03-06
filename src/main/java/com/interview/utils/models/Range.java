package com.interview.utils.models;

/**
 * Created with IntelliJ IDEA.
 * User: stefanie
 * Date: 9/24/14
 * Time: 3:24 PM
 */
public class Range {
    public int start;
    public int end;

    public Range(int start, int end){
        this.start = start;
        this.end = end;
    }

    public static boolean cover(Range s, Range t){
        return (t.start <= s.start && t.end >= s.end);
    }
}
