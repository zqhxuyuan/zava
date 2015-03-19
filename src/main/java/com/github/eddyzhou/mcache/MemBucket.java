package com.github.eddyzhou.mcache;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;

public class MemBucket {
    public static final int BUCKET_LINK_SIZE = 4;
    public static final int BUCKET_VERSION = 0x3201;
    public static final int HEADER_SIZE = 20;

    private static final int INDEX_USEDNUM = 2;
    private static final int INDEX_LINK_BEGIN = 3;
    private static final int INDEX_LINK_END = 4;

    private ByteBuffer buffer;
    private IntBuffer headerBuffer;
    private IntBuffer linkBuffer;
    private int totalSize;

    // 以下为header部分
    private int bucketVersion;
    private int bucketNum;
    private int usedNum;
    private int linkBegin;
    private int linkEnd;

    /**
     * 创建前先通过calSize方法预先计算大小.<br>
     * 需要保留一个bucket不用(下标为0的bucket), 每个bucket需要保留4个字节的指针用来标识使用情况
     *
     * @param bucketNum
     * @return
     */
    public static int calSize(int bucketNum) {
        int size = HEADER_SIZE + (bucketNum + 1) * BUCKET_LINK_SIZE;
        return size;
    }

    public MemBucket(ByteBuffer buffer, int bufferSize, int bucketNum,
                     boolean isInit) throws MmapException {
        if (bucketNum <= 0 || bucketNum >= 0x7FFFFFFF) {
            throw new IllegalArgumentException("bucketNum[" + bucketNum
                    + "] not valid.");
        }

        this.totalSize = calSize(bucketNum);
        if (bufferSize != totalSize)
            throw new MmapException("MemBucket size invalid: bucketNum="
                    + bucketNum + ", bufferSize=" + bufferSize);

        this.buffer = buffer;
        this.bucketNum = bucketNum;
        this.headerBuffer = this.buffer.asIntBuffer();
        ByteBuffer tmpBuffer = this.buffer.duplicate();
        tmpBuffer.position(HEADER_SIZE);
        tmpBuffer.limit(HEADER_SIZE + (bucketNum + 1) * BUCKET_LINK_SIZE);
        this.linkBuffer = tmpBuffer.slice().asIntBuffer();

        if (isInit) {
            initialize();
        } else {
            check();
        }
    }

    public boolean isEmpty() {
        return usedNum == 0;
    }

    public boolean isFull() {
        return usedNum == bucketNum;
    }

    public int getUsedNum() {
        return usedNum;
    }

    public int getIdleNum() {
        return bucketNum - usedNum;
    }

    public int size() {
        return bucketNum;
    }

    /**
     * Note:分配的bucket从1开始计算
     *
     * @return
     * @throws MmapException
     */
    public int alloc() throws MmapException {
        if (isFull())
            throw new MmapException("MemBucket alloc err: MemBucket is full.");

        if (linkBegin == 0 || linkEnd == 0)
            throw new MmapException("MemBucket alloc err: linkBegin: "
                    + linkBegin + ", linkEnd: " + linkEnd);

        int pos = linkBegin;
        int link = linkBuffer.get(pos);
        assert ((link & 0x80000000) == 0);
        int next = (link & 0x7FFFFFFF);
        linkBuffer.put(pos, 0x80000000);
        linkBegin = next;
        usedNum = usedNum + 1;
        headerBuffer.put(INDEX_LINK_BEGIN, linkBegin);
        headerBuffer.put(INDEX_LINK_END, linkEnd);
        if (next == 0) {
            assert (linkEnd == pos);
            linkEnd = 0;
            headerBuffer.put(INDEX_LINK_END, linkEnd);
            assert (isFull());
        }
        assert (pos > 0);
        return pos;
    }

    public boolean hasLink(int idx) {
        if (idx <= 0 || idx > bucketNum)
            throw new IllegalArgumentException("idx[" + idx + "] not valid.");

        int link = linkBuffer.get(idx);
        if ((link & 0x80000000) == 0)
            return false;

        return true;
    }

    public boolean free(int idx) {
        if (idx <= 0 || idx > bucketNum)
            throw new IllegalArgumentException("idx[" + idx + "] not valid.");

        int link = linkBuffer.get(idx);
        if ((link & 0x80000000) == 0)
            return false;

        if (linkBegin == 0) {
            assert (linkEnd == 0);
            assert (usedNum == bucketNum);
            linkBuffer.put(idx, 0);
            linkBegin = idx;
            linkEnd = idx;
            usedNum = usedNum - 1;
            headerBuffer.put(INDEX_LINK_BEGIN, linkBegin);
            headerBuffer.put(INDEX_LINK_END, linkEnd);
            headerBuffer.put(INDEX_USEDNUM, usedNum);
        } else {
            linkBuffer.put(idx, linkBegin);
            linkBegin = idx;
            usedNum = usedNum - 1;
            headerBuffer.put(INDEX_LINK_BEGIN, linkBegin);
            headerBuffer.put(INDEX_USEDNUM, usedNum);

        }

        return true;
    }

    public void setLink(int idx, int next) {
        if (next == 0) {
            linkEnd = idx;
            headerBuffer.put(INDEX_LINK_END, linkEnd);
        }
        linkBuffer.put(idx, next);
    }

    public void setLinkUsed(int idx) {
        linkBuffer.put(idx, 0x80000000);
    }

    public void setUsedAndLink(int usedNum, int linkBegin, int linkEnd) {
        this.usedNum = usedNum;
        this.linkBegin = linkBegin;
        this.linkEnd = linkEnd;

        headerBuffer.put(INDEX_LINK_BEGIN, linkBegin);
        headerBuffer.put(INDEX_LINK_END, linkEnd);
        headerBuffer.put(INDEX_USEDNUM, usedNum);
    }

    @Override
    public String toString() {
        StringBuilder strBu = new StringBuilder();
        strBu.append("MemBucket [").append("version=").append(bucketVersion)
                .append(" , bucketNum=").append(bucketNum)
                .append(" , usedNum=").append(usedNum).append(" , linkBegin=")
                .append(linkBegin).append(" , linkEnd=").append(linkEnd);
        return strBu.toString();
    }

    private void initialize() throws MmapException {
        if (headerBuffer.get(0) != 0)
            throw new MmapException(
                    "MemBucket Initialize failed: bucketVersion["
                            + headerBuffer.get(0) + "] is not 0");

        bucketVersion = BUCKET_VERSION;
        usedNum = 0;
        linkBegin = 1; // 0-保留
        linkEnd = bucketNum;
        headerBuffer.put(0, bucketVersion);
        headerBuffer.put(1, bucketNum);
        headerBuffer.put(2, usedNum);
        headerBuffer.put(3, linkBegin);
        headerBuffer.put(4, linkEnd);

        for (int i = 1; i <= bucketNum; i++) {
            if (i == bucketNum) {
                linkBuffer.put(i, 0);
            } else {
                linkBuffer.put(i, i + 1);
            }
        }
    }

    private void check() throws MmapException {
        bucketVersion = headerBuffer.get(0);
        int bucketNum = headerBuffer.get(1);
        usedNum = headerBuffer.get(2);
        linkBegin = headerBuffer.get(3);
        linkEnd = headerBuffer.get(4);

        if (bucketVersion != BUCKET_VERSION) {
            throw new MmapException("MemBucket Check fail: bucketVersion="
                    + bucketVersion);
        }
        if (this.bucketNum != bucketNum) {
            throw new MmapException("MemBucket Check fail: bucketNum="
                    + bucketNum + "!=" + this.bucketNum);
        }
        if (usedNum > bucketNum) {
            throw new MmapException("MemBucket Check fail: usedNum=" + usedNum
                    + ">" + bucketNum);
        }
        if (linkBegin > bucketNum) {
            throw new MmapException("MemBucket Check fail: linkBegin="
                    + linkBegin + ">" + bucketNum);
        }
        if (linkEnd > bucketNum) {
            throw new MmapException("MemBucket Check fail: linkEnd=" + linkEnd
                    + ">" + bucketNum);
        }

        fullCheck();
    }

    private void fullCheck() throws MmapException {
        int realUsed = 0;
        int idle = 0;

        for (int i = 1; i <= bucketNum; i++) {
            int link = linkBuffer.get(i);
            if ((link & 0x80000000) == 0) {
                idle++;
            } else {
                realUsed++;
            }
        }

        System.out.println("realUsed: " + realUsed + ", idle: " + idle);
        System.out.println("usedNum=" + usedNum + ", bucketNum=" + bucketNum
                + ", linkBegin=" + linkBegin + ", linkEnd=" + linkEnd);
    }

    public static void main(String[] args) throws MmapException {
        int bucketNum = 10000000;
        int bufferSize = MemBucket.calSize(bucketNum);
        System.out.println("bucketNum=" + bucketNum + ", bufferSize="
                + bufferSize);
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        MemBucket bucket = new MemBucket(buffer, bufferSize, bucketNum, true);
        System.out.println("bucket=" + bucket.toString());
        Random random = new Random();
        for (int i = 0; i < 10000000; i++) {
            int idx = random.nextInt(bucketNum);
            bucket.hasLink(idx);
            if (random.nextInt(10) == 0) {
                bucket.free(idx);
            } else {
                idx = bucket.alloc();
                if (idx == 0) {
                    System.out.println("alloc failed.");
                }
            }
        }

        System.out.println("insert over");
        MemBucket _bucket = new MemBucket(buffer, bufferSize, bucketNum, false);
        System.out.println("_bucket=" + _bucket.toString());
    }
}