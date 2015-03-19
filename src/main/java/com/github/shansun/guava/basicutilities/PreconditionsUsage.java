package com.github.shansun.guava.basicutilities;

import com.google.common.base.Preconditions;

/**
 * 断言的用法
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-2
 */
public class PreconditionsUsage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Preconditions.checkArgument(args.length == 0, "Args is not empty");

        Preconditions.checkNotNull(args, "Args is null");

        Preconditions.checkState(args.length == 0, "Args is not in state");

        Preconditions.checkElementIndex(0, args.length + 1, "Args is out of bound");

        Preconditions.checkPositionIndex(0, args.length + 1, "Args is out of bound");
    }

}