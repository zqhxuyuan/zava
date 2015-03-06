package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-30
 * Time: 下午12:34
 */
public class LOJ164_MaximumGap {
    //Bucket placement: range = max - min, bucketSize = (max - min)/n-1, bucketIdx = (num[i] - min)/bucketSize;
    //max gap = bucket[i].max - bucket[j].min, i and j are continuous non-empty bucket
    class Bucket{
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        public void place(int value){
            if(value > max) max = value;
            if(value < min) min = value;
        }
    }
    public int maximumGap(int[] num) {
        if(num.length <= 1) return 0;
        int min = num[0];
        int max = num[0];
        for(int i = 1; i < num.length; i++){
            if(num[i] > max) max = num[i];
            else if(num[i] < min) min = num[i];
        }

        int bucketSize = (int) Math.ceil((max-min+0.0)/(num.length - 1));
        Bucket[] buckets = new Bucket[num.length];

        for(int i = 0; i < num.length; i++){
            int bucketIdx = (num[i] - min)/bucketSize;
            if(buckets[bucketIdx] == null) buckets[bucketIdx] = new Bucket();
            buckets[bucketIdx].place(num[i]);
        }

        int maxGap = bucketSize - 1;
        int prevBucketIdx = 0;
        for(int i = 1; i < buckets.length; i++){
            if(buckets[i] == null) continue;
            maxGap = Math.max(maxGap, buckets[i].min - buckets[prevBucketIdx].max);
            prevBucketIdx = i;
        }
        return maxGap;
    }

    public static void main(String[] args){
        LOJ164_MaximumGap gapFinder = new LOJ164_MaximumGap();
        int[] num = new int[]{4,2,5,1};
        System.out.println(gapFinder.maximumGap(num));
    }
}
