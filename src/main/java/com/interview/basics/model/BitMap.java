package com.interview.basics.model;

/**
 * Created_By: stefanie
 * Date: 14-10-10
 * Time: 下午5:10
 */
public class BitMap {
    public static int DEFAULT_SIZE = 1024;
    public static int UNIT_SIZE = 32;

    private int size = DEFAULT_SIZE;
    private int[] map;

    public BitMap(int size) {
        this.size = size;
        int count = (int) Math.ceil(1.0 * size / UNIT_SIZE);
        map = new int[count];
    }

    private void checkIndex(int i) throws Exception{
        if(i >= size) throw new Exception("DZ12_BitMap Index Overflow");
    }

    public boolean getBit(int i) throws Exception{
        checkIndex(i);
        int offset = i / UNIT_SIZE;
        int index = i % UNIT_SIZE;
        return ((map[offset] & ( 1 << index)) != 0);
    }

    public void setBit(int i) throws Exception{
        checkIndex(i);
        int offset = i / UNIT_SIZE;
        int index = i % UNIT_SIZE;
        map[offset] |= (1 << index);
    }

    public void flipBit(int i) throws Exception{
        checkIndex(i);
        int offset = i / UNIT_SIZE;
        int index = i % UNIT_SIZE;
        if(((map[offset] & ( 1 << index)) == 0)){
            map[offset] |= (1 << index);
        } else {
            map[offset] &= (~(1 << index));
        }
    }

    public void clearBit(int i) throws Exception{
        checkIndex(i);
        int offset = i / UNIT_SIZE;
        int index = i % UNIT_SIZE;
        int mask = ~(1 << index);
        map[offset] &= mask;
    }

    public void clearRightBit(int i) throws Exception{
        checkIndex(i);
        int offset = i / UNIT_SIZE;
        int index = i % UNIT_SIZE;
        int mask = ~((1 << (index + 1)) - 1);
        map[offset] &= mask;
        for(int j = 0; j < offset; j++) map[j] &= 0;
    }

    public void clearLeftBit(int i) throws Exception{
        checkIndex(i);
        int offset = i / UNIT_SIZE;
        int index = i % UNIT_SIZE;
        int mask = (1 << (index + 1)) - 1;
        map[offset] &= mask;
        for(int j = offset + 1; j < map.length; j++) map[j] &= 0;
    }

    public void updateBit(int i, boolean flag) throws Exception{
        checkIndex(i);
        int offset = i / UNIT_SIZE;
        int index = i % UNIT_SIZE;
        int mask = ~(1 << index);
        map[offset] &= mask;
        if(flag) map[offset] |= (1 << index);
    }

    public void clean(int i, int j) throws Exception{
        checkIndex(i);
        checkIndex(j);
        int offsetI = i / UNIT_SIZE;
        int indexI = i % UNIT_SIZE;
        int offsetJ = j / UNIT_SIZE;
        int indexJ = j % UNIT_SIZE;
        if(offsetI == offsetJ){
            int mask = -1 ^ (((1 << (indexJ - indexI + 1)) - 1) << indexI);
            map[offsetI] &= mask;
        } else {
            int mask = (1 << (indexI + 1)) - 1;
            map[offsetI] &= mask;
            mask = ~((1 << (indexJ + 1)) - 1);
            map[offsetJ] &= mask;
        }
    }

    public void copy(int M, int i, int j) throws Exception{
        checkIndex(i);
        checkIndex(j);
        int offsetI = i / UNIT_SIZE;
        int indexI = i % UNIT_SIZE;

        if(indexI + j - i < UNIT_SIZE){
            int mask = M << indexI;
            map[offsetI] |= mask;
        } else {
            int index = indexI + j - i - UNIT_SIZE;
            int mask = M << UNIT_SIZE - index;
            map[offsetI] |= mask;
            mask = M >> index;
            map[offsetI + 1] |= mask;
        }
    }

    public void print(){
        for(int i = map.length - 1; i >= 0; i--){
            System.out.print(toBinary(map[i]));
            System.out.print(" ");
        }
        System.out.println();
    }

    private String toBinary(int value) {
        char[] chars = new char[UNIT_SIZE];
        for(int j = UNIT_SIZE - 1; j >= 0; j--) {
            chars[j] = (value & 1) == 0? '0' : '1';
            value = value >>> 1;
        }
        return String.valueOf(chars);
    }
}
