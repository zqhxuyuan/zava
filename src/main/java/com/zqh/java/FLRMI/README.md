https://github.com/yuriyao/FLRMI @yuriyao
================================

```
HelloWorld helloWorld = new HelloWorldImpl();
FLRMI.registerFLRMIService("hello", helloWorld);
FLRMI.startServer();

HelloWorld helloWorld = (HelloWorld) FLRMI.getFLRMIService("hello", new Class[]{HelloWorld.class});
helloWorld.hello("jeff");
```

#### Server Side
1. 注册服务到服务中心ServiceCenter(底层实际上是一个Map, 注册服务是put,获取服务是get)
2. 启动服务器FLRMIServer: 监听Socket连接,创建新线程用于服务客户端的连接,启动ServerTask运行任务
3. ServerTask会读取客户端的调用请求

#### Client Side
1. 创建RMITarget:设置serverName和RMIClient:设置server的host和port,RMIProxy:设置RMITarget,RMIClient
2. 通过动态代理调用Proxy的invoke方法创建Proxy的代理类
3. 调用客户端的接口方法会调用到服务端的实现类的方法

#### Client Send Msg
1. RMIProxy的invoke方法交给RMIClient实现
2. RMIClient会连接Server[对应RMIServer接受连接,交给ServerTask读取数据]
3. RMIClient会用连接Server的Socket创建一个输出流
4. 并创建Peer信元消息,用Socket的输出流发送信元消息
5. RMIClient会读取调用结果:返回给客户端一个代理类. 客户端读取的消息来自于ServerTask写入的消息

#### Server Read Msg
1. Server根据Client发送的接口类,寻找注册在ServiceCenter的对应实现类
2. 通过动态调用,调用服务端实现类的方法,返回结果同样封装成信元
3. 将信元发送给客户端RMIClient

```
Client
    //1.请求服务端
    peer.writeMessageMeta(messageMeta);
    //5.获得响应
    MessageMeta result = peer.readMessageMeta();

Server
    //2.读取信元
    MessageMeta messageMeta = peer.readMessageMeta();
    //3.调用具体实现类的方法
    result = messageMeta.getMethod().invoke(service, messageMeta.getParams());

    MessageMetaImpl messageMetaImpl = new MessageMetaImpl();
    messageMetaImpl.setTarget(result);
    //4.发送信息
    peer.writeMessageMeta(messageMetaImpl);
```

#### target transform
1. Client的发送信元中target是RMITarget,封装了serviceName.
2. Server根据serviceName能找在注册中心找到对应的实现类
3. Server的发送信元中target是Server调用实现类的返回结果!

#### Design Patterns
1. 通过面向接口编程实现Client和Server在不同机器上运行. 调用Client的接口的方法会远程调用到Server的实现类的方法
2. 动态代理+动态调用: 通过Proxy.newProxyInstance返回的是代理类,调用代理类的方法会调用其invoke方法


#### Peer and MessageMeta
MessageMeta信元封装了调用某个类上的方法(并传递参数)需要的序列化信息,因为调用信息要在网络上传输给Server.
MessageMeta实现了Externalizable接口,其实现类重写了writeExternal和readExternal
writeExternal接受输出流,将当前对象写到输出流中[序列化],readExternal接受输入流,从输入流中读取数据到当前对象[反序列化]
序列化和反序列化交给了SerizableMessageMeta实现类完成. 底层实际上只是二进制对象的读写.

那么输入流和输出流是何时传入的?
RMIClient在writeMessageMeta时传递MessageMeta实现类,
调用了Peer的writeMessageMeta.Peer实现类会使用Socket的输出流将MessageMeta写入到输出流中.
因为MessageMeta实现了Externalizable接口,所以会用Socket的输出流作为writeExternal的输出流参数.

#### Role at RMI
1. Server (register, dynamic invoke)
2. Client (Proxy, Target)
3. MessageMeta (serialize method,service,params)
4. RMI (the entry of client and server invoking)

#### WHAT HAVE U LEARNED
1. 服务的注册和获取,使用Map设置服务名serviceName和服务类server的对应关系
2. Socket数据发送和接收. Client发送数据,Server接收数据,Server发送数据,Client接收数据
3. 动态代理newProxyInstance和动态调用method.invoke(object,params)
4. RMITarget, RMIClient, RMIProxy的对象分装,和调用处理转发: Proxy封装了Target和Client,调用Proxy的方法交给Client处理
5. 客户端发送的Target和服务端发送的Target的不同:
   客户端因为只知道接口,所以将serviceName设置到Target中,Server会从注册中心中根据serviceName找出service
   服务端发送给客户端的Target是调用service实现类方法的返回结果.
6. 消息在客户端和服务器传送的对象都是MessageMeta实现类.
   客户端发送给服务器时要设置method,target(serviceName),params, 而服务器发给客户端的只需要target(result)
   因为服务器要知道客户端调用的是哪个类的哪个方法以及传递的参数,而客户端只需要知道返回结果即可.
7. Server和Client都需要建立Socket输入流和输出流,因为他们同时都要发送和接收数据
8. 消息的序列化方式. MessageMeta实现了Externalizable.其底层的输入流和输出流来自于建立Socket时的IO流


#### the original github doc
五 逐层分析
5.1 传输层（peer+信元）
5.1.1 功能
　　负责底层的请求和响应的传输
5.1.2 实现
　　实现文件主要是Peer.java,TCPPeerImpl.java(peer），这是对等端，只进行信元的发送和接收。
   MessageMeta.java,MessageMetaImpl.java，实现信元，封装了调用和放回结果的数据，是整个通信的逻辑的最小单元。
　　
5.2 序列化层
5.2.1 功能
　　提供灵活的信元序列化方法。
5.2.2 实现
　　序列化层由序列化中心和序列化工具组成。序列化中心负责注册和获取序列化工具，序列化工具负责实际的序列化工作。

整个序列化过程是按照一定的协议进行的，首先会序列化序列化工具的标志，这个由信元自己完成，然后调用实际的序列化工具队信元进行序列化。
反序列化时，首先会读取序列化工具的标志，然后查找序列化中心，获取实际的序列化工具，进行反序列化工作。
序列化中心由FLRMISerizableCenter.java完成
序列化工具只需要继承AbstractSerizableMessageMeta抽象类(实际是后面接口的部分实现)或者实现SerizableMessageMeta接口就可以。

5.3 客户端
5.3.1 功能
　　负责客户端的请求和创建套接字工作。

5.4 代理层
5.4.1 功能
　　代理整个服务，使用户可以像调用本地方法一样调用远程的服务。
5.4.2 实现 FLRMIProxy.java
　　使用jdk的代理封装底层的请求和响应

5.5 服务端
5.5.1 功能
　　提供连接请求，并创建服务线程，提供服务。
5.5.2 实现
　　详看 FLRMIServer.java

5.6 服务中心
5.6.1 功能
　　负责服务的注册和查找
5.6.2 实现
ServiceCenter.java 负责服务的注册和简单查找
ServiceFinder.java 负责target对象到服务的实际查找工作，这个由用户具体实现，可以定制自己的查找方式，
    所以可以不用使用ServiceCenter，但是为了能让系统感知工具的存在，必须注册到查找工具中心TargetFinderCenter。
TargetFinderCenter.java 负责注册和为target对象查找合适的查找工具。