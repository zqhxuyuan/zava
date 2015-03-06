package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 上午11:14
 */
public class TQ5_ClockHandsAngle {
    static int DEGREE_PRE_HOUR = 360 / 12;
    static int DEGREE_PER_MINUTE = 360 / 60;

    public float angle(int hour, int minute){
        hour += minute / 60;
        hour = hour % 12;
        minute = minute % 60;
        float minuteDegree = minute * DEGREE_PER_MINUTE;
        float hourDegree = (hour + minute/60F) * DEGREE_PRE_HOUR;

        float degree = Math.abs(hourDegree - minuteDegree);
        return degree < 180? degree : 360 - degree;
    }

    public static void main(String[] args){
        TQ5_ClockHandsAngle clock = new TQ5_ClockHandsAngle();
        System.out.println(clock.angle(1, 20));  //80
        System.out.println(clock.angle(4, 70));  //5:10 95
        System.out.println(clock.angle(17, 50));  //5:50 125
    }
}
