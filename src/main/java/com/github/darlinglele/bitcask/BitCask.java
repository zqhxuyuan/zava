package com.github.darlinglele.bitcask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

public class BitCask<T> {
    private final Indexer indexer;
    private final String name;
    private static HashMap<String, BitCask> bitCasks = new HashMap<>();
    private long offset;

    /**
     * 操作对应索引名称的工具类,比如可以put,get
     * @param name 索引名称
     *             用索引名称来标记不同的索引集合. 比如Student表示学生索引,里面存放的是所有学生的索引
     *             可以再加上Teacher的索引,存放的是Teacher的索引
     * @param <T> 泛型类
     * @return
     */
    public static <T> BitCask<T> of(String name) {
        return of(name, defaultIndexOf(name));
    }

    /**
     * @param name 索引名称
     * @param indexFile 索引文件名, 内存中的索引信息需要持久化到磁盘.注意不是数据内容的磁盘文件
     * @param <T>
     * @return
     */
    public static <T> BitCask<T> of(String name, String indexFile) {
        // bitCasks维护了索引名称和对应的BitCask, 如果索引名称已经存在, 直接从map中获取
        if (bitCasks.containsKey(name)) {
            return (BitCask<T>) bitCasks.get(name);
        } else {
            BitCask<T> newBitCask = new BitCask<>(name, indexFile);
            bitCasks.put(name, newBitCask);
            return newBitCask;
        }
    }

    private static String defaultIndexOf(String name) {
        return name + ".index";
    }

    /**
     * 根据索引名称和索引文件构造BitCask
     * @param name 索引名称
     * @param indexFile 索引文件
     */
    private BitCask(String name, String indexFile) {
        this.name = name;
        this.indexer = loadIndexFrom(indexFile);
    }

    // read index-file to memory
    private Indexer loadIndexFrom(String indexFile) {
        RandomAccessFile file = getFileAccesser(indexFile);
        try {
            // 如果文件内容为空,创建一个Indexer: 实际上是创建一个Map
            if (file.length() == 0) {
                return new Indexer();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = null;
        try {
            // 文件内容不为空,读取文件内容,转化为字节数组
            bytes = readBytesFromFile(0, (int) file.length(), indexFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (bytes == null) {
            return new Indexer();
        }
        // 将字节数组反序列化为对象
        Object object = convertBytesToObject(bytes);
        // 转化为Indexer. Indexer是什么东东? 见updateIndex-->
        if (!(object instanceof Indexer))
            return new Indexer();
        else
            return (Indexer) object;
    }

    // write memory to index-file
    public void dumpIndexTo(String indexFile) {
        RandomAccessFile file = getFileAccesser(indexFile);
        if (file != null) {
            try {
                file.write(convertObjectToBytes(this.indexer));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 写数据
     * 1. 序列化value
     * 2. 追加value到磁盘文件中
     * 3. 更新内存中的索引
     * @param key
     * @param value
     */
    public void put(String key, T value) {
        // 保存数据时, 先序列化数据
        byte[] bytes = convertObjectToBytes(value);
        // 写数据, 直接追加
        if (appendValue(key, bytes)) {
            // 更新内存中的索引信息
            updateIndex(key, bytes, this.offset);
        }
    }

    private boolean appendValue(String key, byte[] bytes) {
        // 数据会写到磁盘文件中, 文件名称为索引名称. 比如索引名称为Student的磁盘文件为Student
        return appendBytesToFile(bytes, this.name);
    }

    private boolean appendBytesToFile(byte[] bytes, String name) {
        RandomAccessFile file = getFileAccesser(name);
        try {
            // 写数据时,首先定位到文件末尾
            long offset = file.length();
            // 跳转到文件末尾
            file.seek(offset);
            // 写入二进制数据
            file.write(bytes);
            // 保存offset变量, 用于更新内存中的索引信息
            this.offset = offset;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 更新索引: 索引的内容是Index对象. 包含了key的名称, 文件名, 在文件中的偏移量, 写入的数据大小
     *
     * indexer是个内存中的Map对象. key是写入的key, value是Index对象.
     * Index对象维护了内存中的key索引:value在磁盘文件中的位置和大小
     * @param key 数据的key
     * @param bytes 数据内容
     * @param offset 数据写到磁盘文件中的偏移量/开始位置
     */
    private void updateIndex(String key, byte[] bytes, long offset) {
        this.indexer.put(key, new Index(key, this.name, offset, bytes.length));
    }

    // 读取key的内容. 根据key构造Index
    public T get(String key) {
        return readFromFile(this.indexer.get(key));
    }

    /**
     * 根据Index对象, 读取key的内容. Index包含了key以及在文件中的起始位置和数据大小.
     * @param index
     * @return
     */
    private T readFromFile(Index index) {
        byte[] bytes = readBytesFromFile(index.offset, index.size, index.fileName);
        Object object = convertBytesToObject(bytes);
        return (T) object;
    }

    /**
     * @param offset 在文件中的偏移量, 从这里开始读
     * @param size 要读多少
     * @param fileName 文件名
     * @return 数据
     */
    private byte[] readBytesFromFile(long offset, int size, String fileName) {
        byte[] bytes = new byte[size];
        RandomAccessFile file = getFileAccesser(fileName);
        try {
            file.seek(offset);
            file.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            bytes = null;
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }

    // 序列化
    private byte[] convertObjectToBytes(Object value) {
        try {
            return Serialization.serialize(value);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 反序列化
    private Object convertBytesToObject(byte[] bytes) {
        try {
            return Serialization.deserialize(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 如果文件名不存在, 则新建文件, 如果文件已经存在,则直接获取文件
    private RandomAccessFile getFileAccesser(String name) {
        try {
            return new RandomAccessFile(name, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
