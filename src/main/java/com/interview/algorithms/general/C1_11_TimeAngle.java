package com.interview.algorithms.general;

import java.util.Date;


/**
 * Created_By: stefanie
 * Date: 14-6-30
 * Time: 下午9:10
 */
public class C1_11_TimeAngle {
    public static float angle(Date time){

        float hourDegree = ((time.getHours() % 12) / 12F) * 360 + (time.getMinutes() / 60F) * 1/12F * 360;
        float minsDegree = (time.getMinutes() / 60F) * 360;

        float angle = hourDegree - minsDegree;

        return  angle >= 0 ? angle : angle + 360;
    }
}
