package com.ctriposs.tsdb.test.util;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class TestUtil {

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random rd = new Random();

    public static final String TEST_BASE_DIR = "d:/tsdb_test/";
    private static final NumberFormat MEM_FMT = new DecimalFormat("##,###.##");

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rd.nextInt(AB.length())));
        }

        return sb.toString();
    }

    public static String kbString(long memBytes) {
        return MEM_FMT.format(memBytes/1024) + " kb";
    }

    public static String getMemoryFootprint() {
        Runtime runtime = Runtime.getRuntime();
        String memoryInfo = "Memory - free:" + kbString(runtime.freeMemory()) + " - max:" + kbString(runtime.maxMemory()) + " - total:" + kbString(runtime.totalMemory());
        return memoryInfo;
    }

    public static byte[] getBytes(Object o) {
        if (o instanceof String) {
            return ((String) o).getBytes();
        } else if (o instanceof byte[]) {
            return ((byte[]) o);
        } else if (o instanceof Serializable) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(o);
                return bos.toByteArray();
            } catch (Exception e) {
                return null;
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {/**/}

                try {
                    bos.close();
                } catch (IOException e) {/**/}
            }
        }

        throw new RuntimeException("Fail to convert object to bytes");
    }

    public static String convertToString(byte[] bytes) {
        return new String(bytes);
    }
}
