package com.interview.books.topcoder.math;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 15-1-12
 * Time: 下午8:49
 */
public class TC_M1_PrimNumbers {

    public boolean[] sieve(int n){
        boolean[] prime=new boolean[n+1];
        Arrays.fill(prime, true);
        prime[0]=false;
        prime[1]=false;
        int m = (int) Math.sqrt(n);
        for(int i = 2; i <= m; i++)
            if (prime[i])
                for(int k = i*i; k <= n; k += i)
                    prime[k]=false;
        return prime;
    }
}
