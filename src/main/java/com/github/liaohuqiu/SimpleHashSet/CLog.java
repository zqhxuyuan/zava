package com.github.liaohuqiu.SimpleHashSet;

public class CLog {

    public static void d(String tag, String msg, Object... args) {
        if (args.length > 0) {
            msg = String.format(msg, args);
        }
        System.out.println(msg);
    }
}
