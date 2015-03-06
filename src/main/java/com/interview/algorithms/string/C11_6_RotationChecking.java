package com.interview.algorithms.string;

/**
 * Created_By: zouzhile
 * Date: 8/20/13
 * Time: 12:43 PM
 * Assume you have a method isSubstring which checks if one word is a substring of another. Given two strings s1 and s2, write code to check
 if s2 is a rotation of s1 using only one call to isSubstring (e.g. "waterbottle" is a rotation of "erbottlewat".
 */
public class C11_6_RotationChecking {


   private static boolean isSubString(String s1, String s2) {
       if (s1 == null || s2 == null)
           return false;
       return s2.contains(s1);
   }

   public static boolean isRotation(String source, String target) {
       if(source.length() != target.length()) return false;

       for(int i = 1 ; i <= source.length(); i++) {
           String s1 = source.substring(0, i);
           String s2 = source.substring(i, source.length());
           String rotatedValue = s2 + s1;
           if (isSubString(rotatedValue, target))
               return true;
       }
       return false;
    }

    public static void main(String[] args) {
        String source = "waterbottle";
        String target = "erbottlewat";
        System.out.println(String.format("Is %s a rotation of %s : %s", source, target, isRotation(source, target)));
    }
}
