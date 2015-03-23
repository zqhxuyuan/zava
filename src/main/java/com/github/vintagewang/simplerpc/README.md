https://github.com/vintagewang/simplerpc

#### Traditional I/O Program
```
Client:
SocketChannel socketChannel = socketChannel.connect(ip,port);

byte[] buf = new byte[];
// ... make buf data to be send
socketChannel.write(buf);   // 1.客户端发送调用请求

byte[] res = new byte[];
socketChannel.read(res);    // 4. 客户端读取服务端发送的调用结果

Server:
SocketChannel socketChannel = ServerSocketChannel.accept();
byte[] buf = new byte[];
socketChannel.read(buf);    // 2. 服务端读取客户端发送的调用请求
// ... resolve buf data, make rpc invoke

byte[] res = new byte[];
socketChannel.write(res);   // 3. 服务端将调用结果发送给客户端
```

#### SimpleRPC's Component
这里比传统的IO编程多了一个**Connection**对象,用于管理读写线程.
不使用ByteBuffer,而是使用了针对写优化的**LinkedByteBufferNode**.

1. RPCServer        服务端
2. RPCProcessor     业务处理器   自定义业务处理逻辑
3. Connection       连接管理     读写线程
4. RPCClient        客户端

####  WorkFlows

Connect

1. 客户端发起连接请求,根据SocketChannel创建客户端的Connection
2. 服务端接受客户端连接,使用和客户端连接的通道SocketChannel创建服务端的Connection

Request

3. 客户端发起调用请求, 使用客户端的currentWriteNode.byteBufferWrite写缓冲区保存数据
4. 客户端的Connection写线程通过流控,使用currentReadNode.byteBufferRead读取写到写缓冲区中的数据发送给客户端的SocketChannel
4. 服务端Connection的读线程会从建立到客户端连接的SocketChannel中读取数据到byteBufferRead
5. 服务端从byteBufferRead解析出请求消息的reqId和消息内容,回调自定义的业务处理逻辑

Response

6. 服务端RPC调用产生的结果放入服务端的currentWriteNode的byteBufferWrite中
7. 服务端Connection的写线程通过流控,使用currentReadNode.byteBufferRead读取写到写缓冲区中的数据发送给建立到客户端连接的SocketChannel
8. 客户端Connection的读线程会从客户端的SocketChannel中读取RPC调用结果到byteBufferRead

下图描述了客户端发送RPC调用请求,到服务端的RPC调用过程. 服务端将调用结果返回给客户端的过程和这个类似.

                 Client                                     |     Server
               _____________                                |  _____________
byteData ---> |socketChannel|    --->>>                     | |socketChannel|  ---> byteData   (传统模式)
              |_____________|                               | |_____________|
                                                            |
------------------------------------------------------------|-------------------------------------------
               ________________  write   _______________    |
byteData ---- |currentWriteNode| -----> |byteBufferWrite|   |
              |________________|        |_______________|   |
                                                            |
 Connection    ________________  read    _______________    |
   写线程      |currentReadNode | -----> |byteBufferRead |   |
              |________________|        |_______________|   |
                                               | write      |  Connection读线程
                                         _____\|/_____      |  _____________  read
                                        |socketChannel|-->> | |socketChannel| ----->  byteBufferRead --> ... RPC调用
                                        |_____________|     | |_____________|


#### Connection : Write and Read Thread
写线程要负责将数据写到SocketChannel中. 比如客户端的写线程要将调用请求写到SocketChannel中,
这样服务端的SocketChannel就可以读取到客户端发送的数据.

读线程负责从SocketChannel中读取出数据,并解析数据.
比如服务端的读线程读取出客户端发送的调用请求后,会调用真正的业务处理逻辑实现.

因为Connection的读写线程是Client和Server共用的. 所以Client和Server处理消息的格式是一样的.
比如客户端发送的调用请求消息和Server读取的消息是一致的. 同样Server发送的返回结果和客户端接收的结果也是一致的.
所以客户端发送的调用消息: rpcClient.call --> Connection.putRequest
和服务端读线程执行业务逻辑后的返回结果: Connection.this.linkedByteBufferList.putData(reqId, response)调用了相同的方法.

#### LinkedByteBufferList ByteBufferNode ByteBuffer
LinkedByteBufferList可以管理多个ByteBufferNode, 包括
+  当前写内存块currentWriteNode,
+  当前读内存块currentReadNode,
+  空闲块列表bbnIdleList.
**当前**表示要将数据写到哪个内存块,或者从哪个内存块读取数据.
如果写满一个内存块,则要新建一个内存块作为当前写内存块,并将写满的那个内存块的next引用指向新建的内存块.
如果读满一个内存块,则用读满内存块的下一个内存块作为下一次读取数据的来源.并且将读满的内存块加入空闲块列表.
当写满内存块时,优先选择空闲块作为下一个写入的内存块,如果没有,则分配新的内存块.

读内存块读完后加入空闲块,可以重用的原因是读完的数据就没有用了.而写内存块不可以重用,因为写的数据不能被覆盖.

private ByteBufferNode currentWriteNode;
private ByteBufferNode currentReadNode;

为什么要使用2个内存节点? 分别表示当前的读和写内存块. 和ByteBufferNode里的byteBufferWrite,byteBufferRead是不是有对应关系
currentWriteNode -->  byteBufferWrite : currentWriteNode.getByteBufferWrite().put(byteData)
currentReadNode  -->  byteBufferRead  : [Connection.WriteSocketService] socketChannel.write(currentReadNode.getByteBufferRead())

当前读写的内存块并不一定是相同的内存块. 但是一个内存块里有2个ByteBuffer:byteBufferWrite, byteBufferRead指向的是相同的底层数据
在读写数据时,我们关心的是内存块级别. 所以针对一个内存块的读/写并不会影响另一个内存块的写/读.
具体到内存块里的读写缓冲区. 写入数据时首先写到写缓冲区中, 然后使用读缓冲区读取出写缓冲区的数据,并发送到通道中. 完成数据的发送.

#### TODO
1. 业务逻辑实现类目前只支持一个,如果要实现多个,则在Server端要进行服务名称和服务实现类的绑定.