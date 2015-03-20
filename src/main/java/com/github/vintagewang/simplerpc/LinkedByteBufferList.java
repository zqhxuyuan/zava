package com.github.vintagewang.simplerpc;


import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 针对写优化的ByteBuffer序列
 *
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class LinkedByteBufferList {

    //内存节点/内存缓冲区, 实际上是Connection中的一个byteBufferRead
    //LinkedByteBufferList管理所有的内存节点. 因为要保存所有的请求信息.
    //只用一个内存缓冲区显然不够,而多个内存缓冲区就需要LinkedByteBufferList进行管理
    class ByteBufferNode {
        //和Connection.ReadMaxBufferSize大小一样. 将Connection的byteBufferRead抽象成内存中的一个节点
        public static final int NODE_SIZE = 1024 * 1024 * 4;
        private final AtomicInteger writeOffset = new AtomicInteger(0);
        private ByteBuffer byteBufferWrite;
        private ByteBuffer byteBufferRead;
        private volatile ByteBufferNode nextByteBufferNode;

        public ByteBufferNode clearAndReturnNew() {
            this.writeOffset.set(0);
            this.byteBufferWrite.position(0);
            this.byteBufferWrite.limit(NODE_SIZE);
            this.byteBufferRead.position(0);
            this.byteBufferRead.limit(NODE_SIZE);
            this.nextByteBufferNode = null;
            return this;
        }

        //如果byteBufferRead >= writeOffset.说明读指针已经超过了内存块中写入的数据: 在writeOffset后的位置还没有数据, 则不能读!
        //写数据的时候writeOffset指针不断往后移动.读指针在初始位置. 当开始读时,读指针也往后移动,但是不能超过写指针的位置!
        public boolean isReadable() {
            return this.byteBufferRead.position() < this.writeOffset.get();
        }

        //当前内存块是否读取完毕. 读指针的位置到达内存块的末尾
        public boolean isReadover() {
            return this.byteBufferRead.position() == NODE_SIZE;
        }

        private ByteBuffer allocateNewByteBuffer(final int size) {
            return ByteBuffer.allocate(size);
        }

        public ByteBufferNode() {
            LinkedByteBufferList.this.nodeTotal++;
            this.nextByteBufferNode = null;
            this.byteBufferWrite = allocateNewByteBuffer(NODE_SIZE);
            //read和write公用内存缓冲区. 因为写入缓冲区的数据要被读取出来,用于解析每条消息的内容
            //只不过针对不同角色而已.比如byteBufferWrite用来存放客户端写入的数据,则服务端要从byteBufferRead读取出消息内容
            //或者服务端将响应结果写到byteBufferWrite,客户端会从byteBufferRead读取出消息的响应结果.
            this.byteBufferRead = this.byteBufferWrite.slice();
        }

        public ByteBuffer getByteBufferWrite() {
            return byteBufferWrite;
        }

        public void setByteBufferWrite(ByteBuffer byteBufferWrite) {
            this.byteBufferWrite = byteBufferWrite;
        }

        public ByteBuffer getByteBufferRead() {
            return byteBufferRead;
        }

        public void setByteBufferRead(ByteBuffer byteBufferRead) {
            this.byteBufferRead = byteBufferRead;
        }

        public ByteBufferNode getNextByteBufferNode() {
            return nextByteBufferNode;
        }

        public void setNextByteBufferNode(ByteBufferNode nextByteBufferNode) {
            this.nextByteBufferNode = nextByteBufferNode;
        }

        //writeOffset并没有提供set方法,而是在write data时,通过Atomic的addAndGet原子操作来更新缓冲区中当前写的位置
        public AtomicInteger getWriteOffset() {
            return writeOffset;
        }
    }

    private volatile int nodeTotal = 0;
    //为什么要使用2个内存节点? 分别表示当前的读和写.
    private ByteBufferNode currentWriteNode;
    private ByteBufferNode currentReadNode;
    private final LinkedBlockingDeque<ByteBufferNode> bbnIdleList = new LinkedBlockingDeque<LinkedByteBufferList.ByteBufferNode>();

    // 是否已经被Notify过
    protected volatile boolean hasNotified = false;


    public LinkedByteBufferList() {
        this.currentWriteNode = new ByteBufferNode();
        //初始时,读节点和写节点是同一个内存缓冲区
        this.currentReadNode = this.currentWriteNode;
    }


    // TODO 可能需要流控

    /**
     * 什么时候会调用该方法:
     * 1. 客户端请求调用服务端的方法. -->客户端写数据.
     * 2. 服务端响应结果. -->服务端写数据.
     *
     * 写数据的时候都是使用currentWriteNode.
     * @param reqId
     * @param data
     */
    public void putData(final int reqId, final byte[] data) {
        //消息头部
        final int HEADER_SIZE = 8;
        ByteBuffer header = ByteBuffer.allocate(HEADER_SIZE);
        header.putInt(data.length);
        header.putInt(reqId);
        header.flip();
        synchronized (this) {
            //当前写节点(内存缓冲区)是不是够写一条请求消息? 可能的情况是
            //(a)最后一条消息只写了头,数据写在另一个块里.  刚好写了头
            //(b)写了消息头后,还剩余一点空间不够写数据     写了头后还剩余点空间
            //(c)内存块的最后剩余的空间都不够写消息头.     一条完整的消息后还有空间
            //(d)刚好写满一条消息                       一条完整的消息后没有空间
            int minHeader = Math.min(HEADER_SIZE, this.currentWriteNode.getByteBufferWrite().remaining());
            int minData = 0;
            // 尝试写入头
            if (minHeader > 0) {
                this.currentWriteNode.getByteBufferWrite().put(header.array(), 0, minHeader);
                this.currentWriteNode.getWriteOffset().addAndGet(minHeader);
            }
            // 尝试写入体
            if (minHeader == HEADER_SIZE) {
                minData = Math.min(data.length, this.currentWriteNode.getByteBufferWrite().remaining());
                if (minData > 0) {
                    this.currentWriteNode.getByteBufferWrite().put(data, 0, minData);
                    this.currentWriteNode.getWriteOffset().addAndGet(minData);
                }
            }

            // 需要创建新的Buffer
            if (!this.currentWriteNode.getByteBufferWrite().hasRemaining()) {
                ByteBufferNode newNode = null;
                // 尝试从空闲处取
                newNode = this.bbnIdleList.poll();
                if (null == newNode) {
                    newNode = new ByteBufferNode();
                }

                //不同内存块之间通过next指针引用,才能知道请求到达的顺序.
                //当前内存块的下一个内存块是新增的内存块
                this.currentWriteNode.setNextByteBufferNode(newNode.clearAndReturnNew());
                //把新增的内存块设置为当前内存块
                this.currentWriteNode = newNode;

                //针对前面出现的几种写数据的情况,要处理一条消息不完整写入一个内存块的情景
                // 补偿Header
                int remainHeaderPut = HEADER_SIZE - minHeader;
                int remainDataPut = data.length - minData;
                if (remainHeaderPut > 0) {
                    this.currentWriteNode.getByteBufferWrite().put(header.array(), minHeader, remainHeaderPut);
                    this.currentWriteNode.getWriteOffset().addAndGet(remainHeaderPut);
                }

                // 补偿Data
                if (remainDataPut > 0) {
                    this.currentWriteNode.getByteBufferWrite().put(data, minData, remainDataPut);
                    this.currentWriteNode.getWriteOffset().addAndGet(remainDataPut);
                }
            }

            if (!this.hasNotified) {
                this.hasNotified = true;
                this.notify();
            }
        }
    }


    public ByteBufferNode findReadableNode() {
        //当前内存节点可读,直接返回当前内存块
        if (this.getCurrentReadNode().isReadable()) {
            return this.getCurrentReadNode();
        }
        //当前内存节点不可读,读取下一个节点
        if (this.getCurrentReadNode().isReadover()) {
            if (this.getCurrentReadNode().getNextByteBufferNode() != null) {
                //将当前读的内存节点加入到空闲节点列表中. 为什么可以用读节点?
                //目的是重用内存节点. 在创建LinkedByteBufferList时,分别创建了读和写的内存块
                //如果发生了读操作(有写,就一定有读), 则读完了的节点数据是不需要的了.注意:不能用写节点,因为写节点保存的是数据.
                //在创建新的内存块的时候,优先使用bbnIdleList.毕竟创建一个节点是有一定开销的,能直接使用就直接用.
                this.bbnIdleList.add(this.getCurrentReadNode());

                //将下一个内存块节点设置为当前读节点: 当前节点数据读取完毕,下一次读数据时,从下一个节点开始读数据.
                this.setCurrentReadNode(this.getCurrentReadNode().getNextByteBufferNode());
                return this.getCurrentReadNode();
            }
        }
        return null;
    }

    // 流控写入. 在put数据之前要先调用该方法判断是否可以写入数据
    // 如果找到满足的内存块, 则wait()会被释放, 写数据会用找到的内存块开始写数据.
    public ByteBufferNode waitForPut(long interval) {
        ByteBufferNode found = this.findReadableNode();
        if (found != null) {
            return found;
        }

        synchronized (this) {
            if (this.hasNotified) {
                this.hasNotified = false;
                found = this.findReadableNode();
                if (found != null) {
                    return found;
                }
            }

            try {
                this.wait(interval);
                return this.findReadableNode();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                this.hasNotified = false;
            }
        }
        return null;
    }


    public ByteBufferNode getCurrentReadNode() {
        return currentReadNode;
    }


    public void setCurrentReadNode(ByteBufferNode currentReadNode) {
        this.currentReadNode = currentReadNode;
    }


    public LinkedBlockingDeque<ByteBufferNode> getBbnIdleList() {
        return bbnIdleList;
    }


    public int getNodeTotal() {
        return nodeTotal;
    }
}
