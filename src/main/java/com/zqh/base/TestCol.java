package com.zqh.base;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertNull;

/**
 * Created by zqhxuyuan on 15-3-17.
 */
public class TestCol {

    public static void main(String[] args) {
        //testLinkedList();

        //testRange();
        testBinarySearch();
    }

    public static void testBinarySearch(){
        int[] arr = new int[]{0,1,3,4,5,6,7,8};
        int index = Arrays.binarySearch(arr, 0, arr.length, 2);
        System.out.println(index);

        System.out.println(-index - 1);
    }

    public static void testRange(){
        //Byte,1个字节,8位=1000 0000=128
        System.out.println("Byte:");
        System.out.println(Byte.MAX_VALUE); //127: 0-127一共128
        System.out.println(Byte.MIN_VALUE); //-128
        System.out.println(1 << 7); //128
        System.out.println((1 << 7 ) - 1);  //127

        System.out.println("Short:");
        //short,2个字节,16位=1000 0000 0000 0000
        System.out.println(Short.MAX_VALUE);
        System.out.println((1 << 15) - 1);

        System.out.println("Int:");
        //int,4个字节,32位
        System.out.println(Integer.MAX_VALUE); //2147483647 = 1
        System.out.println(Integer.MIN_VALUE); //-2147483648
        System.out.println(1 << 31);
        System.out.println((1 << 31)-1); //2147483647

        //MAX_VALUE=(1<<(bit-1))-1
        //MIN_VALUE=-(1<<(bit-1))
        String[] type = new String[]{"BYTE","SHORT","INT"};
        int[] bits = new int[]{8,16,32};
        for(int i=0;i<type.length;i++){
            String t = type[i];
            int b = bits[i];
            int maxValue = (1 << (b-1)) - 1;
            int minValue = -(1 << (b-1));

            if("BYTE".equals(t)){
                System.out.println("Byte:"+(Byte.MAX_VALUE == maxValue) + (Byte.MIN_VALUE == minValue));
            }else if ("SHORT".equals(t)){
                System.out.println("Short:" + (Short.MAX_VALUE == maxValue) + (Short.MIN_VALUE == minValue));
            }else if ("INT".equals(t)){
                System.out.println("Int:" + (Integer.MAX_VALUE == maxValue) + (Integer.MIN_VALUE == minValue));
            }
        }
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
