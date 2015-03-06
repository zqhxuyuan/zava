package com.interview.algorithms.general;

/**
 * Created_By: zouzhile
 * Date: 10/18/14
 * Time: 3:34 PM
 */
public class C1_34_BitSet {

    private byte[] bits;
    private byte[] masks = new byte [] {1, 2, 4, 8, 16, 32, 64, -128};

    public C1_34_BitSet(int N){
        bits = new byte[N/8 + 1];
    }

    public void set(int pos) {
        int offset = pos / 8;
        int bit = pos % 8;
        bits[offset] |= masks[bit];
    }

    public void unset(int pos) {
        int offset = pos / 8;
        int bit = pos % 8;
        bits[offset] &= (~masks[bit]);
    }

    public void flip(int pos) {
        int offset = pos / 8;
        int bit = pos % 8;
        bits[offset] ^= masks[bit];
    }

    public int get(int pos) {
        int offset = pos / 8;
        int bit = pos % 8;
        int result = (bits[offset] & masks[bit]) == masks[bit] ? 1 : 0;
        return result;
    }

    public String toBinary() {
        String binary = "";
        for(int i = 0; i < bits.length; i ++ ){
            int val = bits[i];
            for(int j = 0; j < 8; j ++) {
                binary += val & 1;
                val >>>= 1;
            }
            binary += " ";
        }
        return binary;
    }

    public static void main(String[] args) {
        C1_34_BitSet bitset = new C1_34_BitSet(20);
        bitset.set(10);
        System.out.println("set = " + bitset.toBinary());
        bitset.unset(10);
        System.out.println("unset = " + bitset.toBinary());
        bitset.flip(10);
        System.out.println("flip = " + bitset.toBinary());
    }
}
