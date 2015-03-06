package com.interview.leetcode.application;

/**
 * Created_By: stefanie
 * Date: 14-11-26
 * Time: 下午10:43
 *
 * There are N gas stations along a circular route, where the amount of gas at station i is gas[i].
 * You have a car with an unlimited gas tank and it costs cost[i] of gas to travel from station i to its next station (i+1).
 * You begin the journey with an empty tank at one of the gas stations.
 *
 * Return the starting gas station's index if you can travel around the circuit once, otherwise return -1.
 */
public class GasStation {

    public int canCompleteCircuit(int[] gas, int[] cost) {
        if(gas.length == 0 || cost.length == 0) return -1;
        int total = 0;
        int current = 0;
        int start = 0;
        for(int i = 0; i < gas.length; i++){
            total += gas[i] - cost[i];
            current += gas[i] - cost[i];
            if(current < 0) {
                current = 0;
                start = i + 1;
            }
        }
        return total >= 0? start : -1;
    }
}
