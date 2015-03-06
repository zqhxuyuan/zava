package com.interview.books.question300;

import com.interview.utils.models.Point;

/**
 * Created_By: stefanie
 * Date: 14-9-29
 * Time: 上午10:55
 *
 * Solution
 * 1. based on Area, D can divide ABC into ABD, BCD, and CAD three triangle, if D is inside ABC
 *      area(ABD) + area(BCD) + area(CAD) = area(ABC)
 *    and we could get triangle area by AB, BC, CA -> a, b, c
 *      area(ABC) = Math.sqrt(p * p-a * p-b * p-c), which p = (a+b+c)/2  Heron's formula
 *    but many Math.sqrt will lose accuracy, so should add this error handling in code
 * 2. based on relative position of line and point
 *      if D is inside of ABC, it always in the left side of AB, BC, and CA
 *      means, the vector product of AB and AD, BC and BD, CA and CD is positive.
 *
 *    More detail of Vector: http://baike.baidu.com/view/973423.htm
 *
 */
public class TQ26_InsideTriangle {

    public static boolean isInside(Point A, Point B, Point C, Point D){
        if(Point.product(A,B,D) >= 0 && Point.product(B,C,D) >= 0 && Point.product(C,A,D) >= 0)
            return true;
        else return false;
    }

    public static boolean isInsideByArea(Point A, Point B, Point C, Point D){
        double abd = area(A, B, D);
        double bcd = area(B, C, D);
        double cad = area(C, A, D);
        double abc = area(A, B, C);
        if((abd + bcd + cad) - abc < 1E-10) return true;     //for double actuary losing
        else return false;
    }

    private static double area(Point A, Point B, Point C){
        double ab = Point.length(A, B);
        double bc = Point.length(B, C);
        double ca = Point.length(C, A);
        double pabc = (ab + bc + ca) / 2;
        return Math.sqrt(pabc * (pabc - ab) * (pabc - bc) * (pabc - ca));
    }

}
