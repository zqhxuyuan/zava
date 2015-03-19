package com.github.shansun.guava.collections;

import com.google.common.collect.ImmutableSet;

/**
 * Immutable Collections浏览
 *
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-2
 */
public class ImmutableCollectionsTour {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // 不可修改集合
        ImmutableSet<String> COLOR_NAMES = ImmutableSet.of("red", "orange", "yellow", "green", "blue", "purple");
        System.out.println(COLOR_NAMES);

        // 不可修改集合的其他用法
        ImmutableSet<String> GOOGLE_COLORS =
                ImmutableSet.<String>builder() //
                        .addAll(COLOR_NAMES) //
                        .add("not-exist") //
                        .build();

        System.out.println(GOOGLE_COLORS);
    }

}