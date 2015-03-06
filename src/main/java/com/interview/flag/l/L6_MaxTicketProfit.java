package com.interview.flag.l;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created_By: stefanie
 * Date: 15-1-8
 * Time: 下午6:06
 */
public class L6_MaxTicketProfit {
    public int maxProfit(int[] tickets, int M){
        Comparator<Integer> comparator = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        };
        PriorityQueue<Integer> prices = new PriorityQueue<>(tickets.length, comparator);
        for(int i = 0; i < tickets.length; i++) prices.add(tickets[i]);
        int profit = 0;
        for(int i = 0; i < M && !prices.isEmpty(); i++){
            int maxPrice = prices.poll();
            profit += maxPrice;
            if(maxPrice - 1 > 0) prices.add(maxPrice - 1);
        }
        return profit;
    }

    public static void main(String[] args){
        L6_MaxTicketProfit seller = new L6_MaxTicketProfit();
        int[] tickets = new int[]{2,5};
        System.out.println(seller.maxProfit(tickets, 4));  //14
        System.out.println(seller.maxProfit(tickets, 9));  //18
    }
}
