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

    //内存节点/内存缓冲区, LinkedByteBufferList管理所有的内存节点. 因为要保存所有的请求信息.
    //只用一个内存缓冲区显然不够,而多个内存缓冲区就需要LinkedByteBufferList进行管理
    class ByteBufferNode {
        //和Connection.ReadMaxBufferSize大小一样.
        public static final int NODE_SIZE = 1024 * 1024 * 4;

        //写缓冲区的当前写入的偏移量
        private final AtomicInteger writeOffset = new AtomicInteger(0);

        //一个节点包括2个缓冲区. 但是读缓冲区使用了写缓冲区的slice拷贝.这样只需要在写缓冲区中保存一份写入的数据.
        //在读取时虽然使用读缓冲区来读取,但是读取的数据来源是写缓冲区中的数据.
        //什么时候写缓冲区: 客户端发起调用请求,Connection.putRequest;
        //或者服务端调用完毕,将结果写入: Connection.dispatchReadRequest在rpcServerProcessor.process后

        //由于客户端发起调用请求或者服务端写入响应结果,对方都没有立即得到调用: 服务端没有立即读取调用请求,客户端没有立即读取结果数据.
        //如果是立即读取,则因为建立了连接通道,可以直接用通道来读取数据.但是通道没有提供流控等优化措施.
        //这里没有立即读取,所以需要用一个写缓冲区来临时保存写入的数据. 当真正要读取数据时,会**从缓冲区中读取出之前写入的数据**
        //问题:这样先放入写缓冲区,再用读缓冲区从写缓冲区中读取出数据.是不是速度会更慢,毕竟使用通道的话,直接从通道中读取数据啊!
        private ByteBuffer byteBufferWrite;
        //读取数据时,共享写缓冲区数据.
        //以客户端发送调用请求为例,客户端并没有直接将数据写到通道中,用写缓冲区byteBufferWrite临时保存写入的调用请求.
        //客户端Connection的写线程启动,由于最终是要将写缓冲区中临时保存的数据写到通道中.
        //如果没有使用读缓冲区byteBufferRead,而用byteBufferWrite的话,要将byteBufferWrite的position设置到写之前,这样比较麻烦
        //用额外的读缓冲区,但是共享写缓冲区的内容,在不需要操作写缓冲区的条件下,也能读取出写缓冲区中的内容!
        //所以将读缓冲区的内容发送给通道,即可完成客户端调用请求的发送.
        private ByteBuffer byteBufferRead;
        private volatile ByteBufferNode nextByteBufferNode;

        public ByteBufferNode() {
            LinkedByteBufferList.this.nodeTotal++;
            this.nextByteBufferNode = null;
            this.byteBufferWrite = allocateNewByteBuffer(NODE_SIZE);
            //read和write共用内存缓冲区:写入写缓冲区的数据[生产者]会被读缓冲区读取出来[消费者]
            this.byteBufferRead = this.byteBufferWrite.slice();
        }

        private ByteBuffer allocateNewByteBuffer(final int size) {
            return ByteBuffer.allocate(size);
        }

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
    //为什么要使用2个内存节点? 分别表示当前的读和写内存块. 和ByteBufferNode里的byteBufferWrite,byteBufferRead是不是有对应关系
    //currentWriteNode -->  byteBufferWrite : currentWriteNode.getByteBufferWrite().put(byteData)
    //currentReadNode  -->  byteBufferRead  : [Connection.WriteSocketService] socketChannel.write(currentReadNode.getByteBufferRead())
    private ByteBufferNode currentWriteNode;
    private ByteBufferNode currentReadNode;
    private final LinkedBlockingDeque<ByteBufferNode> bbnIdleList = new LinkedBlockingDeque<LinkedByteBufferList.ByteBufferNode>();

    // 是否已经被Notify过
    protected volatile boolean hasNotified = false;

    // 在ByteBufferNode中的读写缓冲区是共用数据的. 这里只是在初始化时两个节点引用同一个内存节点的数据.
    // 在一个内存节点中读写缓冲区总是共用底层数据的. 但是对于LinkedByteBufferList而言,这2个节点不总是引用同一份数据的.
    // 比如对于写操作频繁的应用,写完一个writeNode后,又创建了新的内存块来存放数据. 而读取操作发生的比较少(比如增加putData里wait时间)
    // 这样读节点可能读取的仍然是第一个内存块. 只有当读完一个完整的内存块,当前读内存块才引用到下一个写内存块
    // 这种读写的顺序不一致性,导致了currentReadNode和currentWriteNode,只有在最开始的时候才是相同的.

    // 还有一点是读内存块可以重用. 而写内存块因为要保存数据是不能重用的. 重用的读内存块会加入到bbnIdleList中
    // 当读完一个完整的内存块,加入到空闲列表. 在写数据时,如果写满一个内存块,优先使用空闲的内存块.
    // 使用空闲列表的好处是减少内存块的内存分配.
    // 比如一开始一共有1个写内存块和1个读内存块. 写满一个写内存块后,再新建一个写内存块:设置新建的内存块是上一个写内存块的parent.
    // 当读满一个读内存块后, 设置下一个内存块为当前读内存块,这样下一次读操作从下一个内存块开始.
    // 并且把读满的那个内存块加入到空闲列表中. 现在一共有2个写内存块, 一个读内存块(这个内存块和第二个写内存块有相同的引用), 一个空闲块
    // 当第二个写内存块又写满时,就不需要再新建新的内存块了,而是优先使用空闲块. 所以这时一共有2个写内存块,一个读内存块, 没有空闲块了.
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
                //数据写入写缓冲区,byteBufferWrite的position也会自动往后移动,为什么需要用原子的writeOffset来手动控制?
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
            System.out.println("write position:"+currentWriteNode.getByteBufferWrite().position());

            // 需要创建新的Buffer
            if (!this.currentWriteNode.getByteBufferWrite().hasRemaining()) {
                ByteBufferNode newNode = null;
                // 尝试从空闲处取
                // 什么时候会将节点加入到IdleList中? 在读取数据后,当读完一个内存块的节点后,可以将那个内存块认为是空闲的了.
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

    public ByteBufferNode findReadableNode() {
        //当前内存节点可读,直接返回当前内存块.
        if (this.getCurrentReadNode().isReadable()) {
            return this.getCurrentReadNode();
        }
        //当前内存节点不可读,如果是读取到了当前内存节点的末尾, 则读取下一个内存节点. 还有一种情况是到了写的位置,没有数据可读,返回null
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
        //什么情况没有可读的? 当byteBufferRead.position位置到达writeOffset,说明读到了写的位置了.写之后没有数据,就没有可读的了.
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

    public ByteBufferNode getCurrentWriteNode() {
        return currentWriteNode;
    }

    public void setCurrentWriteNode(ByteBufferNode currentWriteNode) {
        this.currentWriteNode = currentWriteNode;
    }

    public int getNodeTotal() {
        return nodeTotal;
    }

    //--------------------------------------
    public static void main(String[] args) {
        LinkedByteBufferList test = new LinkedByteBufferList();
        test.testByteBufferNode();
    }

    public void testByteBufferNode(){
        LinkedByteBufferList list = new LinkedByteBufferList();
        ByteBufferNode currentWriteNode = list.currentWriteNode;
        System.out.println(currentWriteNode.byteBufferWrite.remaining());

        int reqId = 1;
        byte[] data = new byte[10];
        for (int i = 0; i < 10; i++) {
            data[i] = (byte)i;
        }

        final int HEADER_SIZE = 8;
        ByteBuffer header = ByteBuffer.allocate(HEADER_SIZE);
        header.putInt(data.length);
        header.putInt(reqId);
        header.flip();

        currentWriteNode.byteBufferWrite.put(header);
        currentWriteNode.writeOffset.addAndGet(HEADER_SIZE);
        currentWriteNode.byteBufferWrite.put(data);
        currentWriteNode.writeOffset.addAndGet(data.length);

        System.out.println(currentWriteNode.byteBufferWrite.remaining());

        ByteBufferNode node = list.waitForPut(100);
        ByteBuffer byteBufferRead = node.byteBufferRead;
        System.out.println(byteBufferRead.position());

        //TODO 怎么模拟消费了ByteBuffer中的数据, ByteBuffer的偏移量右移? 下面的方法不行!
        //貌似只能用SocketChannel来模拟了.然后看看channel.write(buf)后buf.position是否右移
        byteBufferRead.get(18);
        System.out.println(byteBufferRead.position());
    }
}
