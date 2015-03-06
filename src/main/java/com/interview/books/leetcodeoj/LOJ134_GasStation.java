package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-27
 * Time: 下午12:19
 */
public class LOJ134_GasStation {
    //tracking the current retain gas, if current < 0, mark next station as start.
    //also tracking the total gas and cost, if total < 0 after scan, no start point is OK, so return -1.
    public int canCompleteCircuit(int[] gas, int[] cost) {
        if(gas.length == 0 || cost.length == 0) return -1;
        int begin = 0;
        int current = 0;
        int total = 0;
        for(int i = 0; i < gas.length; i++){
            current += gas[i] - cost[i];
            total += gas[i] - cost[i];
            if(current < 0){
                begin = i + 1;
                current = 0;
            }
        }
        return total < 0? -1 : begin;
    }
}
