package com.github.eddyzhou.mcache;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * 不支持key为0的情况<br>
 * 只维护索引，不维护数据，数据区需自行处理(2G空间问题)
 *
 * @author EddyZhou(zhouqian1103@gmail.com)
 *
 */
public class MemIndexMap {
    public static final int HASH_UNIT_SIZE = 16; // key + pos + next
    public static final int HASH_VERSION = 0x3301;
    public static final int HEADER_SIZE = 24;

    private ByteBuffer buffer;
    private IntBuffer headerBuffer;
    private ByteBuffer hashBuffer;
    private ByteBuffer conflictBucketBuffer;
    private ByteBuffer conflictBuffer;
    private ByteBuffer dataBucketBuffer;
    private MemBucket conflictBucket;
    private MemBucket dataBucket;

    private int totalSize;

    // 以下为header部分
    private int hashVersion;
    private int hashNum; // 建议使用接近(dataNum*2)的一个质数
    private int conflictNum; // 建议使用dataNum
    private int dataNum;
    private int dataSize;
    private int useConflictNum; // 使用中的冲突数量

    /**
     * 创建前先通过calSize方法预先计算索引文件大小
     *
     * @param hashNum
     * @param conflictNum
     * @param dataNum
     * @return
     */
    public static int calSize(int hashNum, int conflictNum, int dataNum) {
        // size = header size + hash size + conflict bucket size + conflict size + data bucket size
        int size = HEADER_SIZE + hashNum * HASH_UNIT_SIZE
                + MemBucket.calSize(conflictNum) + (conflictNum + 1)
                * HASH_UNIT_SIZE + MemBucket.calSize(dataNum);
        return size;
    }

    public MemIndexMap(ByteBuffer buffer, int bufferSize, int hashNum,
                       int conflictNum, int dataNum, int dataSize, boolean isInit)
            throws MmapException {
        if (!(hashNum > 0 && conflictNum > 0 && dataNum > 0 && bufferSize > 0 && dataSize > 12))
            throw new IllegalArgumentException("argument err. hashNum:"
                    + hashNum + ", confilctNum:" + conflictNum + ", dataNum:"
                    + dataNum + ", BufferSize:" + bufferSize);

        this.totalSize = calSize(hashNum, conflictNum, dataNum);
        if (this.totalSize != bufferSize)
            throw new MmapException("bufferSize err. hashNum=" + hashNum
                    + ", conflictNum=" + conflictNum + ", dataNum=" + dataNum
                    + ", bufferSize=" + bufferSize);

        this.buffer = buffer;
        this.hashNum = hashNum;
        this.conflictNum = conflictNum;
        this.dataNum = dataNum;
        this.dataSize = dataSize;
        this.headerBuffer = this.buffer.asIntBuffer();

        ByteBuffer tmpBuffer = this.buffer.duplicate();
        tmpBuffer.position(HEADER_SIZE);
        tmpBuffer.limit(HEADER_SIZE + hashNum * HASH_UNIT_SIZE);
        this.hashBuffer = tmpBuffer.slice();

        tmpBuffer = this.buffer.duplicate();
        tmpBuffer.position(HEADER_SIZE + hashNum * HASH_UNIT_SIZE);
        tmpBuffer.limit(HEADER_SIZE + hashNum * HASH_UNIT_SIZE
                + MemBucket.calSize(conflictNum));
        this.conflictBucketBuffer = tmpBuffer.slice();

        tmpBuffer = this.buffer.duplicate();
        tmpBuffer.position(HEADER_SIZE + hashNum * HASH_UNIT_SIZE
                + MemBucket.calSize(conflictNum));
        tmpBuffer.limit(HEADER_SIZE + hashNum * HASH_UNIT_SIZE
                + MemBucket.calSize(conflictNum) + (conflictNum + 1)
                * HASH_UNIT_SIZE);
        this.conflictBuffer = tmpBuffer.slice();

        tmpBuffer = this.buffer.duplicate();
        tmpBuffer.position(HEADER_SIZE + hashNum * HASH_UNIT_SIZE
                + MemBucket.calSize(conflictNum) + (conflictNum + 1)
                * HASH_UNIT_SIZE);
        tmpBuffer.limit(totalSize);
        this.dataBucketBuffer = tmpBuffer.slice();

        if (isInit)
            initialize();
        else
            check();

    }

    public boolean isEmpty() {
        return dataBucket.isEmpty();
    }

    public boolean isFull() {
        return dataBucket.isFull();
    }

    public int getUsedNum() {
        return dataBucket.getUsedNum();
    }

    public int getIdleNum() {
        return dataBucket.getIdleNum();
    }

    public int size() {
        return dataBucket.size();
    }

    /**
     * 获取key对应的索引位置
     *
     * @param key
     * @return 存在返回position,否则返回0
     */
    public int getIndex(long key) {
        if (key <= 0)
            throw new IllegalArgumentException("key must > 0. key: " + key);

        int idx = Math.abs((int) key % hashNum);
        ByteBuffer tmpBuffer = hashBuffer.duplicate();
        tmpBuffer.position(idx * HASH_UNIT_SIZE);
        tmpBuffer.limit(idx * HASH_UNIT_SIZE + HASH_UNIT_SIZE);
        ByteBuffer bb = tmpBuffer.slice();
        long _key = bb.asLongBuffer().get(0);
        int _pos = bb.asIntBuffer().get(2);
        int _next = bb.asIntBuffer().get(3);

        if (_key == 0) {
            assert (_pos == 0 && _next == 0);
            return 0;
        }

        if (key == _key) {
            assert (_pos > 0);
            return _pos;
        }

        if (_next == 0) {
            return 0;
        }

        // 到冲突区找
        for (int i = 0; i < conflictNum; i++) {
            assert (_pos > 0);
            assert (conflictBucket.hasLink(_next));
            idx = _next;
            tmpBuffer = conflictBuffer.duplicate();
            tmpBuffer.position(idx * HASH_UNIT_SIZE);
            tmpBuffer.limit(idx * HASH_UNIT_SIZE + HASH_UNIT_SIZE);
            bb = tmpBuffer.slice();
            _key = bb.asLongBuffer().get(0);
            _pos = bb.asIntBuffer().get(2);
            _next = bb.asIntBuffer().get(3);
            assert (_key > 0 && _pos > 0);

            if (key == _key) {
                assert (_pos > 0);
                return _pos;
            }

            if (_next == 0) {
                return 0;
            }
        }

        return 0;
    }

    /**
     * 须先getIndex，当不存在时调用insertData申请一个data空间，然后写数据，然后调用insertIndex写索引，如果写索引失败，
     * 需要回收data空间
     *
     * @return position
     * @throws MmapException
     */
    public int insertData() throws MmapException {
        return dataBucket.alloc();
    }

    /**
     * 先释放索引再释放data
     *
     * @param pos
     * @return
     */
    public boolean freeData(int pos) {
        return dataBucket.free(pos);
    }

    public void insertIndex(long key, int pos) throws MmapException {
        if (key <= 0 || pos <= 0) {
            throw new IllegalArgumentException("arguemnt err. key:" + key
                    + ", pos:" + pos);
        }

        int idx = Math.abs((int) (key % hashNum));
        ByteBuffer tmpBuffer = hashBuffer.duplicate();
        tmpBuffer.position(idx * HASH_UNIT_SIZE);
        tmpBuffer.limit(idx * HASH_UNIT_SIZE + HASH_UNIT_SIZE);
        ByteBuffer bb = tmpBuffer.slice();
        long _key = bb.asLongBuffer().get(0);
        int _pos = bb.asIntBuffer().get(2);
        int _next = bb.asIntBuffer().get(3);
        assert (key != _key);

        int newIdx = 0;

        // 如果有冲突，要在冲突区新建一个索引
        if (_key > 0) {
            assert (_pos > 0);
            newIdx = conflictBucket.alloc();
            assert (newIdx > 0);
            ByteBuffer _tmpBuffer = conflictBuffer.duplicate();
            _tmpBuffer.position(newIdx * HASH_UNIT_SIZE);
            _tmpBuffer.limit(newIdx * HASH_UNIT_SIZE + HASH_UNIT_SIZE);
            ByteBuffer _bb = _tmpBuffer.slice();
            _bb.asLongBuffer().put(0, _key);
            _bb.asIntBuffer().put(2, _pos);
            _bb.asIntBuffer().put(3, _next);
            useConflictNum++;
            headerBuffer.put(5, useConflictNum);
        }

        // 写hash区
        bb.asLongBuffer().put(0, key);
        bb.asIntBuffer().put(2, pos);
        bb.asIntBuffer().put(3, newIdx);
    }

    public boolean freeIndex(long key) {
        if (key <= 0)
            throw new IllegalArgumentException("agument err. key:" + key);

        int idx = Math.abs((int) (key % hashNum));
        ByteBuffer tmpBuffer = hashBuffer.duplicate();
        tmpBuffer.position(idx * HASH_UNIT_SIZE);
        tmpBuffer.limit(idx * HASH_UNIT_SIZE + HASH_UNIT_SIZE);
        ByteBuffer bb = tmpBuffer.slice();
        long _key = bb.asLongBuffer().get(0);
        int _pos = bb.asIntBuffer().get(2);
        int _next = bb.asIntBuffer().get(3);

        ByteBuffer _bb = bb;
        ByteBuffer preBb = null, nextBb = null;
        long preKey = 0, nextKey = 0;
        int nextIdx = 0;
        int nextPos = 0;
        int nextNext = 0;
        boolean found = false;
        int i = 0;

        for (; i < conflictNum; i++) {
            // not found
            if (_key == 0) {
                assert (_pos == 0 && _next == 0);
                return false;
            }

            // found
            if (key == _key) {
                assert (_pos > 0);
                found = true;
                if (_next > 0) {
                    assert (conflictBucket.hasLink(_next));
                    nextIdx = _next;
                    tmpBuffer = conflictBuffer.duplicate();
                    tmpBuffer.position(nextIdx * HASH_UNIT_SIZE);
                    tmpBuffer.limit(nextIdx * HASH_UNIT_SIZE
                            + HASH_UNIT_SIZE);
                    nextBb = tmpBuffer.slice();
                    nextKey = nextBb.asLongBuffer().get(0);
                    nextPos = nextBb.asIntBuffer().get(2);
                    nextNext = nextBb.asIntBuffer().get(3);
                    assert (nextKey > 0 && nextPos > 0);
                }
                break;
            }

            // not found
            if (_next == 0) {
                return false;
            }

            assert (conflictBucket.hasLink(_next));
            idx = _next;
            preBb = bb;
            preKey = _key;
            tmpBuffer = conflictBuffer.duplicate();
            tmpBuffer.position(idx * HASH_UNIT_SIZE);
            tmpBuffer.limit(idx * HASH_UNIT_SIZE + HASH_UNIT_SIZE);
            bb = tmpBuffer.slice();
            _key = bb.asLongBuffer().get(0);
            _pos = bb.asIntBuffer().get(2);
            _next = bb.asIntBuffer().get(3);
            assert (_key > 0 && _pos > 0);
        }

        assert (i < conflictNum && found);
        if (preKey > 0) {
            preBb.asIntBuffer().put(3, _next);
            conflictBucket.free(idx);
            useConflictNum--;
            if (useConflictNum < 0)
                useConflictNum = 0;
            headerBuffer.put(5, useConflictNum);
        } else {
            _bb.asLongBuffer().put(0, nextKey);
            _bb.asIntBuffer().put(2, nextPos);
            _bb.asIntBuffer().put(3, nextNext);
            if (nextKey > 0) {
                conflictBucket.free(nextIdx);
                useConflictNum--;
                if (useConflictNum < 0)
                    useConflictNum = 0;
                headerBuffer.put(5, useConflictNum);
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "MemIndexMap [version=" + hashVersion + " ,datanum=" + dataNum
                + " ,datasize=" + dataSize + " ,hashNum=" + hashNum
                + " ,conflict=" + conflictNum + " . " + "used=" + getUsedNum()
                + " ,idle=" + getIdleNum() + " ,conflict=" + useConflictNum
                + "]";
    }

    public void setDataLink(int idx, int next) {
        dataBucket.setLink(idx, next);
    }

    public void setDataLinkUsed(int idx) {
        dataBucket.setLinkUsed(idx);
    }

    public void setDataUsedAndLink(int usedNum, int linkBegin, int linkEnd) {
        dataBucket.setUsedAndLink(usedNum, linkBegin, linkEnd);
    }

    private void initialize() throws MmapException {
        if (headerBuffer.get(0) != 0)
            throw new MmapException(
                    "MemIndexMap initialize failed: hashVersion must be 0");

        hashVersion = HASH_VERSION;
        headerBuffer.put(0, hashVersion);
        headerBuffer.put(1, hashNum);
        headerBuffer.put(2, conflictNum);
        headerBuffer.put(3, dataNum);
        headerBuffer.put(4, dataSize);
        useConflictNum = 0;
        headerBuffer.put(5, useConflictNum);

        conflictBucket = new MemBucket(conflictBucketBuffer,
                MemBucket.calSize(conflictNum), conflictNum, true);
        dataBucket = new MemBucket(dataBucketBuffer,
                MemBucket.calSize(dataNum), dataNum, true);
    }

    private void check() throws MmapException {
        hashVersion = headerBuffer.get(0);
        int hashNum = headerBuffer.get(1);
        int conflictNum = headerBuffer.get(2);
        int dataNum = headerBuffer.get(3);
        int dataSize = headerBuffer.get(4);
        useConflictNum = headerBuffer.get(5);

        if (hashVersion != HASH_VERSION) {
            throw new MmapException("MemIndexMap Check fail: hashVersion="
                    + hashVersion);
        }
        if (this.hashNum != hashNum) {
            throw new MmapException("MemIndexMap Check fail: hashNum="
                    + hashNum + "!=" + this.hashNum);
        }
        if (this.conflictNum != conflictNum) {
            throw new MmapException("MemIndexMap Check fail: conflictNum="
                    + conflictNum + "!=" + this.conflictNum);
        }
        if (this.dataNum != dataNum) {
            throw new MmapException("MemIndexMap Check fail: dataNum="
                    + dataNum + "!=" + this.dataNum);
        }
        if (this.dataSize != dataSize) {
            throw new MmapException("MemIndexMap Check fail: dataSize="
                    + dataSize + "!=" + this.dataSize);
        }
        conflictBucket = new MemBucket(conflictBucketBuffer,
                MemBucket.calSize(conflictNum), conflictNum, false);
        dataBucket = new MemBucket(dataBucketBuffer,
                MemBucket.calSize(dataNum), dataNum, false);
    }

    public static void main(String[] args) {
        int num = 36000000;
        System.out.println("size="
                + MemIndexMap.calSize(MmapUtils.getlargerPrime(num * 2),
                num / 2, num));
    }
}