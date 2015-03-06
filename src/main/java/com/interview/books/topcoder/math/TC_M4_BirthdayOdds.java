package com.interview.books.topcoder.math;

/**
 * Created_By: stefanie
 * Date: 15-1-18
 * Time: 下午1:21
 *
 * Here is an interesting factoid: "On the planet Earth, if there are at least 23 people in a room, the chance
 * that two of them have the same birthday is greater than 50%." You would like to come up with more factoids
 * of this form. Given two integers (minOdds and daysInYear), your method should return the fewest number of
 * people (from a planet where there are daysInYear days in each year) needed such that you can be at least
 * minOdds% sure that two of the people have the same birthday. See example 0 for further information.
 */
public class TC_M4_BirthdayOdds {
    public int minPeople(int minOdds, int daysInYear){
        double target = 1 - (minOdds/100.0);
        double probability = 1;
        int count = 1;
        while(probability > target){
            probability *= 1 - ((double) count / daysInYear);
            count++;
        }
        return count;
    }

    public static void main(String[] args){
        TC_M4_BirthdayOdds game = new TC_M4_BirthdayOdds();
        System.out.println(game.minPeople(75, 5)); //4
        System.out.println(game.minPeople(50, 365)); //23
    }
}
