package com.github.eddyzhou.mcache;


import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * mmap manager.
 * <p>
 * usage:<br>
 * <li>遍历数据(遍历模式取到的ByteBuffer前8个字节为key)
 * <li>建立索引
 * <li>修改数据块数量或大小
 *
 * @author EddyZhou(zhouqian1103@gmail.com)
 *
 */
public class MmapManager {
    private static final int MAX_FILE_SIZE = 0x7FFFFFFF; // 单个数据文件最大支持2G

    private String fileName;
    private int dataNum;
    private int dataSize;

    private MmapFile[] dataFiles;
    private ByteBuffer[] dataBuffers;
    private int dataNumOfOneFile;
    private int dataFileNum;

    public MmapManager(String fileName, int dataNum, int dataSize) throws MmapException, IOException {
        if (dataNum <= 0 || dataSize <= 0)
            throw new IllegalArgumentException("argument err. dataNum:" + dataNum + ", dataSize:" + dataSize);

        this.fileName = fileName;
        this.dataNum = dataNum;
        this.dataSize = dataSize + 12;
        this.dataNumOfOneFile = (int) (MAX_FILE_SIZE / this.dataSize);
        this.dataNum = 0;

        long totalSize = 1L * (dataNum + 1) * this.dataSize;
        long size = 0;
        long lastFileSize = 0;
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
            this.dataNum++;
        }
        System.out.println("dataFileNum:" + dataFileNum + ", dataNumOfOneFile:" + dataNumOfOneFile);

        this.dataFiles = new MmapFile[dataFileNum];
        dataBuffers = new ByteBuffer[dataFileNum];
        for (int i = 0; i < dataFileNum; i++) {
            File f = new File(fileName + ".dat" + i);
            if (!f.exists())
                throw new MmapException(fileName + ".dat" + i + " not exists.");
            if (i == dataFileNum - 1) {
                dataFiles[i] = new MmapFile(f, (int) lastFileSize);
            } else {
                dataFiles[i] = new MmapFile(f, this.dataSize * this.dataNumOfOneFile);
            }
            dataBuffers[i] = dataFiles[i].getBuffer();
        }
    }

    /**
     * 用于按以下遍历模式访问数据区，返回null不代表下一个索引位置也没有数据，可继续往下访问
     *
     * <pre>
     * for (int i = 0; i < dataNum; i++) {
     * 	ByteBuffer data = accessDataBuffer(i);
     * 	if (data == null)
     * 		continue;
     * 	// do it,可直接修改数据,注意不要修改key部分
     * }
     * </pre>
     *
     * @param idx
     * @return ByteBuffer 前8个字节为key，接着是4个字节的长度，再接着是data内容，整个dataSize的最后4个字节是时间戳
     */
    public ByteBuffer getDataBuffer(int idx) {
        if (idx > dataNum) {
            throw new IllegalArgumentException("idx[" + idx + "] > dataNum[" + dataNum + "]");
        }
        int pos = idx + 1;
        ByteBuffer tmpBuffer = dataBuffers[pos / this.dataNumOfOneFile].duplicate();
        tmpBuffer.position((pos % this.dataNumOfOneFile) * dataSize);
        tmpBuffer.limit((pos % this.dataNumOfOneFile + 1) * dataSize);
        return tmpBuffer.slice();
    }

    public byte[] getNotExpiredData(int pos, int expireTime) {
        ByteBuffer tmpBuffer = dataBuffers[pos / this.dataNumOfOneFile].duplicate();
        tmpBuffer.position((pos % this.dataNumOfOneFile) * dataSize);
        tmpBuffer.limit((pos % this.dataNumOfOneFile) + 12);
        long id = tmpBuffer.slice().asLongBuffer().get(0);
        int len = tmpBuffer.slice().asIntBuffer().get(2);

        if (id == 0) return null;

        ByteBuffer _tmpBuffer = dataBuffers[pos / this.dataNumOfOneFile].duplicate();
        _tmpBuffer.position((pos % this.dataNumOfOneFile + 1) * dataSize - 4);
        _tmpBuffer.position((pos % this.dataNumOfOneFile + 1) * dataSize);
        int time = _tmpBuffer.slice().asIntBuffer().get(0);

        if (time < expireTime) return null;

        tmpBuffer = dataBuffers[pos / this.dataNumOfOneFile].duplicate();
        tmpBuffer.position((pos % this.dataNumOfOneFile) * dataSize + 12);
        tmpBuffer.limit((pos % this.dataNumOfOneFile) * dataSize + 12 + len);
        ByteBuffer bb = tmpBuffer.slice();

        if (bb == null) return null;

        byte[] bytes = new byte[bb.capacity()];
        bb.slice().get(bytes);
        return bytes;
    }

    public void rebuildIndex() throws MmapException, IOException {
        int hashNum = MmapUtils.getlargerPrime(dataNum * 2);
        int conflictNum = Math.abs(dataNum / 2);

        int indexSize = MemIndexMap.calSize(hashNum, conflictNum, dataNum);
        File f = new File(fileName + ".idx");
        if (f.exists())
            throw new MmapException("MmapManager rebuild index failed: index file[" + fileName + ".idx] exists.");
        MmapFile indexFile = new MmapFile(f, indexSize);
        ByteBuffer buffer = indexFile.getBuffer();
        MemIndexMap indexMap = new MemIndexMap(buffer, indexSize, hashNum, conflictNum, dataNum, dataSize, true);

        long startTime = System.currentTimeMillis();
        System.out.println("rebuild index start at: " + startTime);
        int buildNum = 0;
        int nextLink = 0;
        int beginLink = 0, endLink = 0;
        for (int i = dataNum; i > 0; i--) {
            ByteBuffer data = this.getDataBuffer(i - 1);
            if (data != null) {
                long key = data.asLongBuffer().get(0);
                if (key > 0) {
                    ++buildNum;
                    indexMap.insertIndex(key, i);
                    indexMap.setDataLinkUsed(i);
                } else {
                    System.out.println("warning: data invalid at " + i);
                    if (nextLink == 0)
                        endLink = i;
                    indexMap.setDataLink(i, nextLink);
                    nextLink = i;
                }
            } else {
                if (nextLink == 0)
                    endLink = i;
                indexMap.setDataLink(i, nextLink);
                nextLink = i;
            }
        }

        if (buildNum == 0) {
            beginLink = 0;
            endLink = 0;
        } else {
            beginLink = nextLink;
        }
        indexMap.setDataUsedAndLink(buildNum, beginLink, endLink);
        long endTime = System.currentTimeMillis();
        System.out.println("rebuild index succ. use " + (endTime - startTime)
                + " ms, buildNum = " + buildNum);
        System.out.println(indexMap.toString());
    }

    public void modifyDataFile(String newFileName, int newDataNum, int newDataSize) throws MmapException, IOException {
        if (newDataNum <= 0 && newDataSize <= 0)
            throw new IllegalArgumentException("argument err. newDataNum:" + newDataNum + ", newDataSize:" + newDataSize);

        long startTime = System.currentTimeMillis();
        System.out.println("rebuild index start at: " + startTime);

        newDataSize = newDataSize + 16;
        int newDataNumOfOneFile = (int) (MAX_FILE_SIZE / newDataSize);

        ByteBuffer[] newDataBuffers = createNewFiles(newFileName, newDataNum, newDataSize);

        int _num = dataNum > newDataNum ? newDataNum : dataNum;
        int _size = dataSize > newDataSize ? newDataSize : dataSize;
        int usedNum = 0;
        int rebuildNum = 0;
        ByteBuffer _buffer = null, buffer_ = null;
        int idx = 1;
        for (int i = 1; i <= dataNum; i++) {
            if (idx > _num) {
                if (i < dataNum)
                    System.out.println("newDataNum < dataNum, maybe not deal over.");
                break;
            }
            _buffer = dataBuffers[i / dataNumOfOneFile].duplicate();
            _buffer.position((i % dataNumOfOneFile) * dataSize);
            _buffer.limit((i % dataNumOfOneFile) * dataSize + 8);
            long _key = _buffer.slice().get(0);

            if (_key > 0) {
                _buffer = dataBuffers[i / dataNumOfOneFile].duplicate();
                _buffer.position((i % dataNumOfOneFile) * dataSize);
                _buffer.limit((i % dataNumOfOneFile) * dataSize + _size);

                buffer_ = newDataBuffers[idx / newDataNumOfOneFile].duplicate();
                buffer_.position((idx % newDataNumOfOneFile) * newDataSize);
                buffer_.limit((idx % dataNumOfOneFile) * newDataSize + _size);
                buffer_.slice().put(_buffer.slice());
                idx++;
                usedNum++;
                rebuildNum++;
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("modifyDataFile succ. use " + (endTime - startTime) + " ms");
        System.out.println("oldNum=" + dataNum + ", oldSize=" + dataSize
                + ", newNum=" + newDataNum + ", newSize=" + newDataSize
                + " ,usedNum=" + usedNum + " ,rebuildNum=" + rebuildNum);
    }

    private ByteBuffer[] createNewFiles(String newFileName, int newDataNum, int newDataSize) throws MmapException, IOException {
        MmapFile[] newDataFiles;
        ByteBuffer[] newDataBuffers;
        long newTotalSize = 1L * (newDataNum + 1) * newDataSize;
        long newSize = 0;
        long newLastFileSize = 0;
        int newDataNumOfOneFile = (int) (MAX_FILE_SIZE / newDataSize);
        int newDataFileNum = 0;
        for (;;) {
            if (newTotalSize <= newSize) {
                break;
            }
            long left = newTotalSize - newSize;
            if (left > (newDataNumOfOneFile * newDataSize)) {
                left = newDataNumOfOneFile * newDataSize;
            } else {
                newLastFileSize = left;
            }
            newSize += left;
            newDataFileNum++;
        }
        System.out.println("NewFileNum: " + newDataFileNum + ", newDataNumOfOneFile: " + newDataNumOfOneFile);

        newDataFiles = new MmapFile[newDataFileNum];
        newDataBuffers = new ByteBuffer[newDataFileNum];
        for (int i = 0; i < dataFileNum; i++) {
            File f = new File(newFileName + ".dat" + i);
            if (f.exists())
                throw new MmapException(newFileName + ".dat" + i + " is aready exists.");
            if (i == newDataFileNum - 1) {
                newDataFiles[i] = new MmapFile(f, (int) newLastFileSize);
            } else {
                newDataFiles[i] = new MmapFile(f, newDataNumOfOneFile * newDataSize);
            }
            newDataBuffers[i] = newDataFiles[i].getBuffer();
        }

        return newDataBuffers;
    }
}