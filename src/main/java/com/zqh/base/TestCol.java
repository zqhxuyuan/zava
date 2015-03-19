package com.zqh.base;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertNull;

/**
 * Created by zqhxuyuan on 15-3-17.
 */
public class TestCol {

    public static void main(String[] args) {
        testLinkedList();
    }

    public static void testPutMap(){
        HashMap<String, byte[]> maps = new HashMap<>();
        String key = "key";
        byte[] value = "value".getBytes();
        assertNull(maps.put(key, value));
    }

    public static void testLinkedList(){
        LinkedList<Map> queue = new LinkedList<Map>();

        Map<Integer,Integer> m1 = new HashMap();
        m1.put(1,1);
        m1.put(2,2);
        m1.put(3,3);

        Map<Integer,Integer> m2 = new HashMap();
        m2.put(11,11);
        m2.put(22,22);
        m2.put(33,33);

        queue.add(m1);
        queue.add(m2);

        Map first = queue.poll();

        System.out.println(queue.size());
    }
}
