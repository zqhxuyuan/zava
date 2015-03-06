package com.interview.books.question300;

/**
 * Created_By: stefanie
 * Date: 14-9-22
 * Time: 下午10:12
 *
 * Given a integer N, find the minimal M to make N * M contains only 0 and 1.
 * such as: N = 2, M = 5, N * M = 10.
 *
 * Solution:
 * When N = 99, M = 1122334455667789L, can't search M by increasing 1 every step.
 * So the N * M only contains 0 and 1, so binarysearch N * M is much easier.
 *
 * mod[] saves different M % N, then increasing M by * 10, add all mod[] to M check if it could get M % N == 0
 */
public class TQ23_ProductOnlyContainsZeroOne {

    public static long find(int N){
        long[] mod = new long[N];
        long M = 1;
        while(true){
            long[] _mod = new long[N]; //mod for current round
            long s = update(M, N, 0, mod, _mod);
            if(s != -1) return s;
            for(int i = 1; i < N; i++){
                if(mod[i] != 0){
                    _mod[i] = mod[i];
                    s = update(M, N, mod[i], mod, _mod);
                    if(s != -1) return s;
                }
            }
            mod = _mod;  //need assign _mod to mod every round to avoid mod[i] created in current round be used to add again.
            M = M * 10;
        }
    }

    private static long update(long M, int N, long add, long[] mod, long[] _mod){
        long t = (M + add) % N;
        if(t == 0) return (M + add) / N;
        else {
            if(mod[(int)t] == 0) _mod[(int)t] = (M + add);
            else _mod[(int)t] = mod[(int)t];
        }
        return -1;
    }

    public static void main(String[] args){
        System.out.println(TQ23_ProductOnlyContainsZeroOne.find(3));
        System.out.println(TQ23_ProductOnlyContainsZeroOne.find(99)); //1122334455667789
    }
}
