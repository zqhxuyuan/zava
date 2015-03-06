package com.interview.design.questions;

/**
 * Created_By: stefanie
 * Date: 14-12-13
 * Time: 下午3:13
 */
public class DZ12_BitMap {
    public static int BIT_UNIT = 32;
    int[] buffer;
    long size;

    public DZ12_BitMap(int size){
        this.size = size;
        int count = size / BIT_UNIT;
        if(size % BIT_UNIT != 0) count++;
        this.buffer = new int[count];
    }

    private int[] convert(int idx) throws Exception {
        int[] offset = new int[2];
        offset[1] = idx % BIT_UNIT;
        offset[0] = idx / BIT_UNIT;
        if(offset[0] >= size) {
            throw new Exception("index " + idx + " out of range");
        }
        return offset;
    }

    public boolean get(int idx) throws Exception {
        int[] offset = convert(idx);
        int mask = 1 << offset[1];
        if((buffer[offset[0]] & mask) == 0) return false;
        else return true;
    }

    public void set(int idx) throws Exception {
        int[] offset = convert(idx);
        int mask = 1 << offset[1];
        buffer[offset[0]] |= mask;
    }

    public void clear(int idx) throws Exception {
        int[] offset = convert(idx);
        int mask = ~(1 << offset[1]);
        buffer[offset[0]] &= mask;
    }

    public static void main(String[] args) throws Exception {
        int[] array = new int[]{1,2,3,56,4,5,6,56,32,3,4,5,1,8,9,4,45,32,29};
        DZ12_BitMap map = new DZ12_BitMap(57);
        System.out.println("Duplicated Elements: ");
        for(int i = 0; i < array.length; i++){
            if(map.get(array[i])) System.out.print(array[i] + " ");
            else map.set(array[i]);
        }
        System.out.println();
        System.out.println("Elements: ");
        for(int i = 0; i < map.size; i++){
            if(map.get(i)) System.out.print(i + " ");
        }
    }
}
