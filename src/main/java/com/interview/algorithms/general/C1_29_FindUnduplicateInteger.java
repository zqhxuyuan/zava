package com.interview.algorithms.general;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.BitSet;

/**
 * Created_By: stefanie
 * Date: 14-7-8
 * Time: 下午10:17
 */
public class C1_29_FindUnduplicateInteger {
    public static int unduplicateNumber(String filePath, int base) {
        BitSet first = new BitSet(2*base);
        BitSet multi = new BitSet(2*base);

        try {
            FileInputStream f = new FileInputStream(filePath);
            BufferedReader dr = new BufferedReader(new InputStreamReader(f));
            String line = dr.readLine();
            while (line != null) {
                Integer item = Integer.valueOf(line.trim());
                int index = item+base;
                if (first.get(index)) multi.set(index);
                else first.set(index);
                line = dr.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int count = 0;
        for(int i = 0; i < 2*base; i++){
            if(first.get(i) && !multi.get(i))  count++;
        }
        return count;
    }
}
