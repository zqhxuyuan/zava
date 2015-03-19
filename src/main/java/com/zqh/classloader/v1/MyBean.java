package com.zqh.classloader.v1;

/**
 * Created by zqhxuyuan on 15-2-28.
 *
 * Example about how to use to different version of the same class at the same time.
 */
public class MyBean {

    private static final long serialVersionUID = 1L;

    public static final String s = "v1";

    @Override
    public String toString() {
        return s;
    }

    public String extraMehtod(){
        return "extra_value";
    }

}