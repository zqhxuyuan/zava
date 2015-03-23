package com.github.vintagewang.simplerpc;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.github.vintagewang.simplerpc.LinkedByteBufferList.ByteBufferNode;

/**
 * 一个Socket连接对象，Client与Server通用
 *
 * @author vintage.wang@gmail.com shijia.wxr@taobao.com
 */
public class Connection {
    private static final int ReadMaxBufferSize = 1024 * 1024 * 4; //4MB

    private final SocketChannel socketChannel;
    private final RPCProcessor rpcServerProcessor;
    private final ThreadPoolExecutor executor;
    private final LinkedByteBufferList linkedByteBufferList = new LinkedByteBufferList();
    private int dispatchPostion = 0;
    private ByteBuffer byteBufferRead = ByteBuffer.allocate(ReadMaxBufferSize);

    private WriteSocketService writeSocketService;
    private ReadSocketService readSocketService;

    public Connection(final SocketChannel socketChannel,
                      final RPCProcessor rpcServerProcessor,
                      final ThreadPoolExecutor executor) {
        this.socketChannel = socketChannel;
        this.rpcServerProcessor = rpcServerProcessor;
        this.executor = executor;

        //Socket连接通道
        try {
            this.socketChannel.configureBlocking(false);
            this.socketChannel.socket().setSoLinger(false, -1);
            this.socketChannel.socket().setTcpNoDelay(true);
            this.socketChannel.socket().setReceiveBufferSize(1024 * 64);
            this.socketChannel.socket().setSendBufferSize(1024 * 64);

            //使用连接通道创建读写服务线程
            this.writeSocketService = new WriteSocketService(this.socketChannel);
            this.readSocketService = new ReadSocketService(this.socketChannel);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        this.readSocketService.start();
        this.writeSocketService.start();
    }

    public void shutdown() {
        this.writeSocketService.shutdown(true);
        this.readSocketService.shutdown(true);
        this.close();
    }

    //写服务. 由于Client和Server都会创建自己的Connection并都启动了Connection线程.
    //所以Client和Server都会维护自己的2个读写线程. 构造函数的SocketChannel用于读和写的通道.
    //如果是写操作,则要将数据写入到SocketChannel中, 这样对端就可以从SocketChannel中读取到数据
    //如果是读操作,则将SocketChannel的数据(对端一定将数据放到了通道中)读到缓冲区中.当前端就可以直接从缓冲区中得到数据

    //Client的SocketChannel
    //1.写线程 SocketChannel.write(byteBuf),将byteBuf数据写到channel: Client使用Channel发送RPC调用请求, Server就可以在对端的Channel中读取数据
    //4.读线程 SocketChannel.read(byteBuf),读取channel数据到byteBuf : Client的Channel读取Server端Channel写入的调用结果,保存到byteBuf中

    //Server的SocketChannel
    //3.写线程 SocketChannel.write(byteBuf),将byteBuf数据写到channel: Server将调用结果写到Channel,Client的对端Channel就可以从中读取数据
    //2.读线程 SocketChannel.read(byteBuf),读取channel数据到byteBuf: Server使用Channel读取Client端Channel写入的请求消息,解析并执行RPC调用请求

    // Client                |         Server
    //       Channel            Channel
    // 1.channel.write(buf)            2.channel.read(buf)
    //     buf->channel   ----------->    channel->buf
    //
    // 4.channel.read(buf)             3.channel.write(buf)
    //     buf<-channel   <-----------    channel<-buf
    class WriteSocketService extends ServiceThread {
        private final Selector selector;
        private final SocketChannel socketChannel;

        public WriteSocketService(final SocketChannel socketChannel) throws IOException {
            this.selector = Selector.open();
            this.socketChannel = socketChannel;
            //在开始服务前, 设置这个通到可以写: 可以往这个通道写入数据
            this.socketChannel.register(this.selector, SelectionKey.OP_WRITE);
        }

        public void run() {
            System.out.println(this.getServiceName() + " service started");
            while (!this.isStoped()) {
                try {
                    this.selector.select(1000);
                    int writeSizeZeroTimes = 0;
                    while (true) {
                        //socketChannel.write(byteBuffer) 将缓冲区Buffer数据写入到SocketChannel中.
                        //那么缓冲区Buffer一定是有数据才可以写的.
                        //按照网络编程CS的模式,只要CS双方建立好连接通道,客户端写入数据,服务端立刻就可以从通道中读取出数据.
                        //然后服务端调用实现类方法,并返回结果给客户端(可以新创建一个线程来处理调用逻辑防止只用单个线程处理引起的阻塞).
                        //最后服务端将调用结果写到通道中,客户端就可以从通道中读出调用结果.

                        //通道读写的参数是ByteBuffer,一般要设置好足够的缓冲区大小来保证消息可以正常发送.
                        //在正常编程模式下,缓冲区的使用是一次性的.并且都是手动构造的:构造一个缓冲区填充数据,将数据写入到通道中.
                        //或者构造一个缓冲区,从通道中将数据读取到缓冲区中. 这里缓冲区要根据数据的大小进行设置.

                        //写线程是将缓冲区的数据写到通道中,比如客户端发起调用请求会将请求消息写到通道中,或者服务端将调用结果写到通道中.
                        //缓冲区的数据都是来自于ByteBufferNode currentReadNode的byteBufferRead
                        //这里引入ByteBufferNode的目的是解决: 缓冲区的管理,以及缓冲区的写优化!
                        //写优化的一个目的是流控: 控制客户端大量发起请求,或者控制服务端一下子返回批量调用结果.

                        //返回ByteBufferNode currentReadNode条件是: byteBufferRead.position < writeOffset
                        //假设客户端在100ms内发起了多个请求调用,writeOffset保存了写缓冲区下一次写入的位置.
                        //通过流控不需要在100ms内立即将多个请求调用发给服务端.
                        //读缓冲区还没使用,写线程100ms后判断可以读,则一次性将读缓冲区的内容都写到通道中.
                        //TODO 问题是: 读过的数据会不会被再读到? 如何更新byteBufferRead的position?
                        ByteBufferNode node = Connection.this.linkedByteBufferList.waitForPut(100);
                        if (node != null) {
                            //byteBuffer.limit(writeOffset) 读取时最多不能超过写的位置,因为当前写位置之后没有数据了!
                            node.getByteBufferRead().limit(node.getWriteOffset().get());
                            //为什么使用读缓冲区将数据发送给通道? 在ByteBufferNode的ByteBuffer byteBufferRead上有解释
                            //简单说就是如果使用节点的写缓冲区,要实现获取数据,则要定位到写之前的位置,再读取数据.
                            //使用额外的读缓冲区,共享写缓冲区的内容,在不操作写缓冲区,一样可以读取出写缓冲区的数据.
                            //让写缓冲区专注于写,读缓冲区只负责读.
                            int writeSize = this.socketChannel.write(node.getByteBufferRead());
                            //ByteBufferNode currentReadNode在将数据写到通道后,其position会自动往后移动,
                            //这样确保了读过的数据不会被再次重复读取. -->
                            //有点类似生产者消费者模式:客户端发起请求,是生产者,将消息放入缓冲队列,写线程是消费者,从队列中取出消息进行消费.
                            System.out.println("readable node position:"+node.getByteBufferRead().position());
                            if (writeSize > 0) {
                            }
                            else if (writeSize == 0) {
                                if (++writeSizeZeroTimes >= 3) {
                                    break;
                                }
                            }
                            else {
                            }
                        }
                        else {
                            break;
                        }
                    }
                }
                catch (Exception e) {
                    System.out.println(this.getServiceName() + " service has exception.");
                    System.out.println(e.getMessage());
                    break;
                }
            }
            //如果执行到这里, 则isStoped=true, 表示服务停止, 则取消通道的选择写服务
            SelectionKey sk = this.socketChannel.keyFor(this.selector);
            if (sk != null) {
                sk.cancel();
            }
            //关闭连接通道
            try {
                this.selector.close();
                this.socketChannel.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(this.getServiceName() + " service end");
        }


        @Override
        public String getServiceName() {
            return WriteSocketService.class.getSimpleName();
        }


        @Override
        public void shutdown() {
            super.shutdown();
        }
    }

    //读线程. 假设客户端使用自己的Connection.socketChannel发送调用请求, 则服务端使用自己的Connection.socketChannel接收数据
    //注意: 客户端一旦将请求发往SocketChannel, 则服务端的SocketChannel立即就能收到数据.
    //这个过程并没有通过流控进行延迟/消费. 这和传统的IO编程模式一样.

    //假设上一步是客户端的写线程通过流控使用读缓冲区的消息内容写到socketChannel中.这里服务端的读线程开始工作了.
    class ReadSocketService extends ServiceThread {
        private final Selector selector;
        private final SocketChannel socketChannel;


        public ReadSocketService(final SocketChannel socketChannel) throws IOException {
            this.selector = Selector.open();
            this.socketChannel = socketChannel;
            //在开始服务前, 设置这个通到可以读: 可以从这个通道读取数据
            this.socketChannel.register(this.selector, SelectionKey.OP_READ);
        }


        public void run() {
            System.out.println(this.getServiceName() + " service started");
            while (!this.isStoped()) {
                try {
                    this.selector.select(1000);
                    boolean ok = Connection.this.processReadEvent();
                    if (!ok) {
                        System.out.println("processReadEvent error");
                        break;
                    }
                }
                catch (Exception e) {
                    System.out.println(this.getServiceName() + " service has exception.");
                    System.out.println(e.getMessage());
                    break;
                }
            }
            SelectionKey sk = this.socketChannel.keyFor(this.selector);
            if (sk != null) {
                sk.cancel();
            }
            try {
                this.selector.close();
                this.socketChannel.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(this.getServiceName() + " service end");
        }


        @Override
        public String getServiceName() {
            return ReadSocketService.class.getSimpleName();
        }
    }


    /**
     * 处理select读事件
     *
     * @return 返回处理结果
     */
    public boolean processReadEvent() {
        int readSizeZeroTimes = 0;
        //缓冲区还有剩余空间才允许读数据. 因为从对端读取进来的数据要先放进内存的缓冲区中.
        //如果缓冲区没有剩余空间可以写,则无法读取对端的数据.
        while (this.byteBufferRead.hasRemaining()) {
            try {
                //从连接通道中读取数据到缓冲区byteBufferRead中.注意现在的数据来源是socketChannel.
                //假设是客户端发送的调用请求,会将消息写到客户端的socketChannel中
                //则这里的服务端的socketChannel因为是和客户端建立的连接通道,所以可以立即获取到客户端发送的数据.
                int readSize = this.socketChannel.read(this.byteBufferRead);
                if (readSize > 0) {
                    readSizeZeroTimes = 0;
                    //分发读取的请求
                    this.dispatchReadRequest();
                }
                else if (readSize == 0) {
                    if (++readSizeZeroTimes >= 3) {
                        break;
                    }
                }
                else {
                    // TODO ERROR
                    System.out.println("read socket < 0");
                    return false;
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private void dispatchReadRequest() {
        //缓冲区用来填充对端发送过来的数据: 读取对端的数据写/填充到自己的缓冲区中.
        //读和写的概念其实是相对的. 比如读取数据,然后写到缓冲区中.
        //byteBufferRead在上一步已经通过通道读取数据了: socketChannel.read(byteBufferRead)
        //那么byteBufferRead应该是读完后有数据了. 这个时候的位置岂不是写完了的位置?? THAT'S RIGHT!

        //这个writePosition其实是下一条消息的写入位置. 因为当前消息已经写入到缓冲区了.
        //这个方法是分发读请求,所以要从缓冲区中解析出发送过来的请求消息(request),交给对应的业务逻辑处理:rpcServerProcessor.process
        int writePosition = this.byteBufferRead.position();
        // 针对线程池优化
        final List<ByteBuffer> requestList = new LinkedList<ByteBuffer>();

        while (true) {
            int diff = this.byteBufferRead.position() - this.dispatchPostion;
            //一条消息至少包括8个字节:msgSize4个字节+reqId4个字节+消息内容.如果不够,说明发送过来的不是完整的消息.
            // | msgSize | reqId | msg content |
            // |   4     |   4   |   msgSize   |
            // |-dispatchPos                   |-byteBufferRead.pos = writePosition
            // |<-----------diff-------------->|
            if (diff >= 8) {
                //处理消息,那么消息的格式是什么样的?
                //初始时,dispatchPostion=0,byteBufferRead的位置是写完一条消息后的位置.两者差表示一条消息的长度.[消息即请求]
                //消息的前4个字节(int)填充的是消息的大小(msgSize),然后填充的是请求id.

                // msgSize不包含消息reqId
                int msgSize = this.byteBufferRead.getInt(this.dispatchPostion);
                final Integer reqId = this.byteBufferRead.getInt(this.dispatchPostion + 4); //4表示前面的int类型的4bytes
                //等价于下面语句,不同的是执行下面语句会改变byteBufferRead的位置.而上面一行并不会改变读取指针的位置.
                //byteBufferRead.position(dispatchPostion);
                //int msgSize = byteBufferRead.getInt();
                //Integer reqId = byteBufferRead.getInt();
                // 可以凑够一个请求
                if (diff >= (8 + msgSize)) {
                    //TODO 这里为什么要定位到内存缓冲区的起始位置? 而不是消息在内存缓冲区的起始位置??
                    //byteBufferRead.position(dispatchPostion);
                    //起始定位到起始位置,并不是要从这里读,只是为了下面的slice做一份内存的拷贝.
                    //要读取消息的起始位置,还是要定位到dispatchPostion, 即后面的request.position(this.dispatchPostion + 8);
                    this.byteBufferRead.position(0);

                    //--------------------读取一条消息. 这条消息的字节数据保存在ByteBuffer request里.
                    //一条消息请求的字节缓冲区
                    final ByteBuffer request = this.byteBufferRead.slice();
                    //定位到msgSize+reqId之后的请求消息正文. dispatchPostion是这条消息在内存中的起始位置
                    request.position(this.dispatchPostion + 8);
                    //消息正文的长度存在msgSize里. 所以我们限制本次读取的长度
                    request.limit(this.dispatchPostion + 8 + msgSize);
                    //--------------------

                    //请求消息读取完毕,将读取指针设置到消息的尾部. 即一开始的writePosition
                    //通过控制writePosition,目的是重用一开始创建出来的缓冲区. 这个缓冲区大小和ByteBufferNode的大小一样
                    this.byteBufferRead.position(writePosition);
                    //dispatchPostion为下一条消息的偏移量做准备
                    this.dispatchPostion += 8 + msgSize;

                    if (this.executor != null) {
                        // if (this.executor.getActiveCount() >=
                        // (this.executor.getMaximumPoolSize() - 16)) {
                        // requestList.add(request);
                        // continue;
                        // }

                        try {
                            this.executor.execute(new Runnable() {
                                public void run() {
                                    try {
                                        //交给具体的业务逻辑处理类处理这条消息.
                                        byte[] response = Connection.this.rpcServerProcessor.process(reqId, request);
                                        if (response != null) {
                                            //将结果放入ByteBuffer LinkedList中是因为要将返回结果发送给客户端.
                                            //reqId标示了要发送给那个客户端: 即发起请求的那个客户端
                                            Connection.this.linkedByteBufferList.putData(reqId, response);
                                        }
                                    }
                                    catch (Throwable e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (RejectedExecutionException e) {
                            requestList.add(request);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //单线程模式
                        byte[] response = Connection.this.rpcServerProcessor.process(reqId, request);
                        if (response != null) {
                            Connection.this.linkedByteBufferList.putData(reqId, response);
                        }
                    }

                    continue;
                }
                // 无法凑够一个请求
                else {
                    // ByteBuffer满了，分配新的内存
                    if (!this.byteBufferRead.hasRemaining()) {
                        this.reallocateByteBuffer();
                    }
                    break;
                }
            }
            else if (!this.byteBufferRead.hasRemaining()) {
                this.reallocateByteBuffer();
            }

            break;
        }

        // 一个线程内运行多个任务
        for (boolean retry = true; retry;) {
            try {
                if (!requestList.isEmpty()) {
                    this.executor.execute(new Runnable() {
                        public void run() {
                            for (ByteBuffer request : requestList) {
                                try {
                                    final int reqId = request.getInt(request.position() - 4);
                                    byte[] response = Connection.this.rpcServerProcessor.process(reqId, request);
                                    if (response != null) {
                                        Connection.this.linkedByteBufferList.putData(reqId, response);
                                    }
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }

                retry = false;
            } catch (RejectedExecutionException e) {
                try {
                    Thread.sleep(1);
                }
                catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    //重新分配内存缓冲区: 将byteBufferRead中剩余的数据拷贝到新的内存缓冲区中
    private void reallocateByteBuffer() {
        ByteBuffer bb = ByteBuffer.allocate(ReadMaxBufferSize);
        //dispatchPostion表示当前内存缓冲区的所有消息写位置: 读和写是相对的,
        //因为消息已经存放在byteBufferRead里,所以要从中dispatchPostion读取出消息
        int remain = this.byteBufferRead.limit() - this.dispatchPostion;
        //从dispatchPostion开始拷贝
        bb.put(this.byteBufferRead.array(), this.dispatchPostion, remain);
        //新的缓冲区从0开始存放消息
        this.dispatchPostion = 0;
        //分发读请求是从byteBufferRead获取消息的
        this.byteBufferRead = bb;
    }

//    private void reallocateByteBuffer() {
//        int remain = this.byteBufferRead.limit() - this.dispatchPostion;
//        if (remain > 0) {
//            byte[] remainData = new byte[remain];
//            this.byteBufferRead.position(this.dispatchPostion);
//            this.byteBufferRead.get(remainData);
//            this.byteBufferRead.position(0);
//            this.byteBufferRead.put(remainData, 0, remain);
//        }
//
//        this.byteBufferRead.position(remain);
//        this.byteBufferRead.limit(ReadMaxBufferSize);
//        this.dispatchPostion = 0;
//    }


    public void putRequest(final int reqId, final byte[] data) {
        this.linkedByteBufferList.putData(reqId, data);
    }


    public int getWriteByteBufferCnt() {
        return this.linkedByteBufferList.getNodeTotal();
    }


    public SocketChannel getSocketChannel() {
        return socketChannel;
    }


    public void close() {
        if (this.socketChannel != null) {
            try {
                this.socketChannel.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
