package com.zqh.base;

import org.junit.Test;

import java.util.Date;

/**
 * Created by zqhxuyuan on 15-3-19.
 */
public class TestStr {

    @Test
    public void testDate(){
        Date date = new Date();
        System.out.println(date);
    }

    @Test
    public void testEqual(){
        String s0="test";
        String s1="test";
        String s2="te"+"st";

        String s3=new String("test");

        System.out.println(s0==s1);
        System.out.println(s0==s2);
        System.out.println(s3==s0);
    }
}
