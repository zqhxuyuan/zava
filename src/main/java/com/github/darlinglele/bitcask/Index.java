package com.github.darlinglele.bitcask;

import java.io.Serializable;

/**
 * 内存中对key进行索引
 */
public class Index implements Serializable {
    public final String key;
    public final long offset;
    public final int size;
    public final String fileName;

    public Index(String key, String name, long offset, int size) {
        this.key = key;
        this.fileName = name;
        this.offset = offset;
        this.size = size;
    }
}
