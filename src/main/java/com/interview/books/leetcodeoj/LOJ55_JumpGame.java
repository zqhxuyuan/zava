package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-22
 * Time: 下午6:09
 */
public class LOJ55_JumpGame {
    //scan from A.length - 1 to 0, and tracking the lowest place can jump to the end
    //if A[i] + i >= lowest, position i can jump to end, otherwise can't. lowest should update when find a lower position.
    public boolean canJump(int[] A) {
        int lowest = A.length - 1;
        boolean canJump = true;
        for(int i = A.length - 2; i >= 0; i--){
            if(A[i] + i >= lowest) {
                canJump = true;
                lowest = i;
            }
            else canJump = false;
        }
        return canJump;
    }
}
