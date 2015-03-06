package com.geophile.erdo.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ThrowableUtil
{
    public static String toString(Throwable throwable)
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
