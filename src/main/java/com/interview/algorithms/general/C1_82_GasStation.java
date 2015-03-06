package com.interview.algorithms.general;

/**
 * Created_By: stefanie
 * Date: 14-11-10
 * Time: 下午3:12
 */
public class C1_82_GasStation {
    public static int canCompleteCircuit(int[] gas, int[] cost) {
        if(gas == null || cost == null || gas.length == 0 || cost.length == 0) return -1;
        int begin = 0;
        int total = 0;
        int utilNextStation = 0;
        for(int i = 0; i < gas.length; i++){
            total += gas[i] - cost[i];
            utilNextStation += gas[i] - cost[i];
            if(utilNextStation < 0){
                utilNextStation = 0;
                begin = i + 1;
            }
        }
        return total < 0? -1 : begin;
    }
}
