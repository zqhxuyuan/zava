package com.github.shansun.guava.basicutilities;

import com.google.common.base.Optional;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-2
 */
public class OptionalUsage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Optional<Integer> possible = Optional.of(5);

        // 判断一个值是否存在
        System.out.println(possible.isPresent());

        // 获取值
        System.out.println(possible.get());

        // 设定值为不存在
        possible = Optional.absent();

        // 如果值不存在，则返回or中指定的值
        System.out.println(possible.or(0));

        // 如果值不存在，则返回null
        System.out.println(possible.orNull());
    }

}