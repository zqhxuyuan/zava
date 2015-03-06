package com.interview.algorithms.general;

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
public class C1_61_ProductOnlyContainsZeroOne {

    public static long find(int N){
        long[] mod = new long[N];
        long M = 1;
        while(true){
            long[] modC = new long[N];
            long s = update(M, N, 0, mod, modC);
            if(s != -1) return s;
            for(int i = 1; i < N; i++){
                if(mod[i] != 0){
                    modC[i] = mod[i];
                    s = update(M, N, mod[i], mod, modC);
                    if(s != -1) return s;
                }
            }
            mod = modC;
            M = M * 10;
        }
    }

    private static long update(long M, int N, long add, long[] mod, long[] modC){
        long t = (M + add) % N;
        if(t == 0) return (M + add) / N;
        else {
            if(mod[(int)t] == 0) modC[(int)t] = (M + add);
            else modC[(int)t] = mod[(int)t];
        }
        return -1;
    }

    //when M is very very large, it's hard to loop on M by increasing 1
    public static long correct(int N){
        long M = 1;
        while(!onlyZeroOne(M * N)) M++;
        return M;
    }

    private static boolean onlyZeroOne(long m){
        String ms = String.valueOf(m);
        for(int i = 0; i < ms.length(); i++){
            if(!(ms.charAt(i) == '1' || ms.charAt(i) == '0')) return false;
        }
        return true;
    }
}
