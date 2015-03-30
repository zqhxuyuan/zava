package com.zqh.base;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;

/**
 * Created by zqhxuyuan on 15-3-19.
 */
public class TestStr {

    public static void main(String[] args)throws Exception{
        String fileName = "/home/hadoop/data/splitByt.txt";
        File file = new File(fileName);
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line.split("\\t").length + "\t"+line);
        }
        reader.close();
    }

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
