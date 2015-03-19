package com.github.eddyzhou.mcache;


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * Mmap : Memory Map.
 */
public class Mmap {
    private static Logger log = Logger.getLogger(Mmap.class.getName());

    private static final long MAX_FILE_SIZE = 0x7FFFFFFF; // 单个数据文件最大支持2G

    private MmapFile indexFile;         //索引文件
    private MemIndexMap indexMap;       //内存索引
    private MmapFile[] dataFiles;       //数据文件,多个
    private ByteBuffer[] dataBuffers;   //数据内存缓冲区
    private int dataNumOfOneFile;       //每个文件的数据量
    private int dataFileNum;            //数据文件数量, 其中dataFiles数组的大小就是这个值
    private int dataSize;               //数据大小,每条记录的长度

    private String statFile;
    private long statLastRecordTime;
    private long statGetCount;
    private long statPutCount;
    private long statUseMsec;
    private int statMaxDatasize;

    /**
     *
     * @param fileName fileName不要加后缀，会自动增加idx和dat后缀
     * @param dataNum
     * @param dataSize
     */
    public Mmap(String fileName, int dataNum, int dataSize) throws MmapException, IOException {
        statFile = fileName;
        //根据dataNum计算hashNum.
        int hashNum = MmapUtils.getlargerPrime(dataNum * 2);
        int conflictNum = Math.abs(dataNum / 2);
        this.dataSize = dataSize + 16;

        // indexFile
        int indexSize = MemIndexMap.calSize(hashNum, conflictNum, dataNum);
        File indexFile = new File(fileName + ".idx");
        boolean needInit = !indexFile.exists();
        this.indexFile = new MmapFile(indexFile, indexSize);
        ByteBuffer bb = this.indexFile.getBuffer();
        this.indexMap = new MemIndexMap(bb, indexSize, hashNum, conflictNum, dataNum, this.dataSize, needInit);

        // dataFile
        long totalSize = 1L * (dataNum + 1) * this.dataSize;
        long size = 0;
        long lastFileSize = 0;
        //总大小/每条记录的长度=这个文件有多少条记录. 那么dataSize是固定的:即每条记录的长度是一样的
        this.dataNumOfOneFile = (int) (MAX_FILE_SIZE) / this.dataSize;
        this.dataFileNum = 0;
        for (;;) {
            if (totalSize <= size) {
                break;
            }
            long left = totalSize - size;
            if (left > (this.dataNumOfOneFile * this.dataSize)) {
                left = this.dataNumOfOneFile * this.dataSize;
            } else {
                lastFileSize = left;
            }

            size += left;
            this.dataFileNum++;
        }
        log.info("mmap[" + fileName + "] start: " + indexMap.toString());

        //磁盘文件
        dataFiles = new MmapFile[dataFileNum];
        //内存缓冲块
        dataBuffers = new ByteBuffer[dataFileNum];
        for (int i = 0; i < dataFileNum; i++) {
            //最后一个文件
            if (i == dataFileNum - 1) {
                dataFiles[i] = new MmapFile(new File(fileName + ".dat" + i), (int) lastFileSize);
            } else {
                //除了最后一个文件,其他每个文件的大小都是固定的.
                dataFiles[i] = new MmapFile(new File(fileName + ".dat" + i), this.dataNumOfOneFile * this.dataSize);
            }
            dataBuffers[i] = dataFiles[i].getBuffer();
        }
    }

    public boolean isEmpty() {
        return indexMap.isEmpty();
    }

    public boolean isFull() {
        return indexMap.isFull();
    }

    public int getUsedNum() {
        return indexMap.getUsedNum();
    }

    public int getIdleNum() {
        return indexMap.getIdleNum();
    }

    public int size() {
        return indexMap.size();
    }

    public boolean contains(long key) {
        return indexMap.getIndex(key) > 0;
    }

    public byte[] get(long key) {
        ByteBuffer bb = getByteBuffer(key);
        if (bb == null) return null;

        byte[] bytes = new byte[bb.capacity()];
        bb.slice().get(bytes);
        return bytes;
    }

    public ByteBuffer getByteBuffer(long key) {
        ++statGetCount;
        long startTime = System.currentTimeMillis();
        doStat(startTime);
        int pos = indexMap.getIndex(key);
        if (pos == 0) {
            long endTime = System.currentTimeMillis();
            statUseMsec += (endTime - startTime);
            return null;
        }

        ByteBuffer tmpBuffer = dataBuffers[pos / this.dataNumOfOneFile].duplicate();
        tmpBuffer.position((pos % this.dataNumOfOneFile) * dataSize);
        tmpBuffer.limit((pos % this.dataNumOfOneFile) * dataSize + 12);
        long _key = tmpBuffer.slice().asLongBuffer().get(0);
        assert (_key == key);
        int len = tmpBuffer.slice().asIntBuffer().get(2);
        assert (len + 12 <= dataSize);

        tmpBuffer = dataBuffers[pos / this.dataNumOfOneFile].duplicate();
        tmpBuffer.position((pos % this.dataNumOfOneFile) * dataSize + 12);
        tmpBuffer.limit((pos % this.dataNumOfOneFile) * dataSize + 12 + len);

        long endTime = System.currentTimeMillis();
        statUseMsec += (endTime - startTime);
        return tmpBuffer.slice();
    }

    // 存在则覆盖；不存在则新增
    public void put(long key, byte[] bytes) throws MmapException {
        ++statPutCount;
        if (bytes.length > statMaxDatasize)
            statMaxDatasize = bytes.length;
        long startTime = System.currentTimeMillis();
        doStat(startTime);

        if (bytes.length + 16 > dataSize) {
            throw new MmapException("Mmap put failed: data too big");
        }

        int pos = indexMap.getIndex(key);
        //存在:覆盖. 能根据key找到索引位置,说明存在
        if (pos > 0) {
            ByteBuffer tmpBuffer = dataBuffers[(pos / this.dataNumOfOneFile)].duplicate();
            tmpBuffer.position((pos % this.dataNumOfOneFile) * dataSize);
            tmpBuffer.limit((pos % this.dataNumOfOneFile) * dataSize + 12);
            long _key = tmpBuffer.slice().asLongBuffer().get(0);
            assert (_key == key);

            // write len
            // key-长度-data-时间戳，(key-长度-时间戳)部分共占16个字节
            int writeSize = ((dataSize - 16) > bytes.length ? bytes.length : (dataSize - 16));
            tmpBuffer.asIntBuffer().put(2, writeSize);

            // write data
            tmpBuffer = dataBuffers[(pos / this.dataNumOfOneFile)].duplicate();
            tmpBuffer.position((pos % this.dataNumOfOneFile) * dataSize + 12);
            tmpBuffer.limit(((pos % this.dataNumOfOneFile) + 1) * dataSize);
            tmpBuffer.slice().put(bytes, 0, writeSize);

            // 时间戳
            tmpBuffer.position(((pos % this.dataNumOfOneFile) + 1) * dataSize - 4);
            tmpBuffer.slice().asIntBuffer().put(0, (int) (startTime / 1000));
        } else {
            //不存在:新增.
            int _pos = indexMap.insertData();
            ByteBuffer tmpBuffer = dataBuffers[_pos / this.dataNumOfOneFile].duplicate();
            tmpBuffer.position((_pos % this.dataNumOfOneFile) * dataSize);
            tmpBuffer.limit((_pos % this.dataNumOfOneFile) * dataSize + 12);
            tmpBuffer.slice().asLongBuffer().put(0, key);

            int writeSize = ((dataSize - 16) > bytes.length ? bytes.length : (dataSize - 16));

            // write len
            tmpBuffer.slice().asIntBuffer().put(2, writeSize);

            // write data
            tmpBuffer = dataBuffers[_pos / this.dataNumOfOneFile].duplicate();
            tmpBuffer.position((_pos % this.dataNumOfOneFile) * dataSize + 12);
            tmpBuffer.limit(((_pos % this.dataNumOfOneFile) + 1) * dataSize);
            tmpBuffer.slice().put(bytes, 0, writeSize);

            tmpBuffer.limit(((_pos % this.dataNumOfOneFile) + 1) * dataSize - 4);
            tmpBuffer.slice().asIntBuffer().put(0, (int) startTime / 1000);

            try {
                indexMap.insertIndex(key, _pos);
            } catch (MmapException e) {
                // for reuse
                indexMap.freeData(_pos);
                tmpBuffer = dataBuffers[_pos / this.dataNumOfOneFile].duplicate();
                tmpBuffer.position((_pos % this.dataNumOfOneFile) * dataSize);
                tmpBuffer.limit((_pos % this.dataNumOfOneFile) * dataSize + 12);
                tmpBuffer.slice().asLongBuffer().put(0, 0);
                tmpBuffer.slice().asIntBuffer().put(2, 0);
            }
        }

        long endTime = System.currentTimeMillis();
        statUseMsec += (endTime - startTime);
    }

    public void free(long key) throws MmapException {
        ++statPutCount;
        long startTime = System.currentTimeMillis();
        doStat(startTime);

        int pos = indexMap.getIndex(key);
        if (pos > 0) {
            ByteBuffer tmpBuffer = dataBuffers[pos / this.dataNumOfOneFile].duplicate();
            tmpBuffer.position((pos % this.dataNumOfOneFile) * dataSize);
            tmpBuffer.limit((pos % this.dataNumOfOneFile) * dataSize + 12);
            tmpBuffer.slice().asLongBuffer().put(0, 0);
            tmpBuffer.slice().asIntBuffer().put(2, 0);

            indexMap.freeData(pos);
            indexMap.freeIndex(key);
        }

        long endTime = System.currentTimeMillis();
        statUseMsec += (endTime - startTime);
    }

    @Override
    public String toString() {
        return indexMap.toString();
    }

    private void doStat(long time) {
        if (time - statLastRecordTime > 1000 * 60 * 60) {
            statLastRecordTime = time;
            long avg = statUseMsec == 0 ? 0 : ((statGetCount + statPutCount) * 1000 / statUseMsec);
            long useNumRate = (getUsedNum() * 100) / size();
            long dataSizeRate = (statMaxDatasize * 100) / dataSize;
            log.info("[" + statFile + "] stat:" + indexMap.toString()
                    + " , [getCount=" + statGetCount + ", putCount="
                    + statPutCount + ", usedMsec=" + statUseMsec + ", avg="
                    + avg + ", maxDatasize=" + statMaxDatasize
                    + ", useNumRate=" + useNumRate + ", dataSizeRate=" + dataSizeRate + "]");

            if (useNumRate > 90) {
                log.warning("[" + statFile + "] warning: useNumRate=" + useNumRate);
            }
            if (dataSizeRate > 90) {
                log.warning("[" + statFile + "] warning: dataSizeRate=" + dataSizeRate);
            }
        }
    }
}