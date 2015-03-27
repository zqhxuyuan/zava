sponge
======

https://github.com/netcomm/sponge

#带缓冲的ThreadPoolExecutor

======
.使用方式跟普通的ThreadPoolExecutor没有任何差别，开发人员没有任何感觉。

.在突发情况极大并发请求下，保证系统的稳定性为第一目标。

.能自动缓冲超过某个阀值的任务请求。

.缓冲持久化的方式灵活，可根据使用场景进行选择，如基于文件(系统吞吐量巨大)、数据库(系统吞吐量大)、redis(系统吞吐量巨大)、…。

.任务的请求处理顺序默认先进先出模式。

.实现原理简单、代码简单。

.默认实现基于文件的缓冲持久化模式。

.关键参数可调整。

## Design

SpongeThreadPoolExecutor是自定义的ThreadPoolExecutor,它的生成过程:
1. 文件持久化策略
2. SpongeService服务基于策略
3. 创建自定义队列SpongeArrayBlockingQueue,并传入服务类,服务会服务于队列
4. 将队列传给线程池,创建线程池
5. 使用线程池来初始化查看队列中是否有需要消费的任务

按照上面的线程池的生成过程,前面的类都会给后面的类使用:
FilePersistence --> SpongeService --> SpongeArrayBlockingQueue --> ThreadPoolExecutor

## 队列与线程池

1. 队列里存放的是要执行的任务
2. 线程池里放的是线程. 线程是用来执行任务的.
3. 所以队列里的任务要执行,需要从线程池中获取线程来执行.

## 初始化

每个类在创建的过程都会有一些初始化的动作
1. FilePersistence初始化当前已经消费过的的位置: 读取索引文件,并定位到数据文件的curFetchPosi位置,下次消费从这里开始
2. SpongeService类引用了PersistenceIntf接口,所以底层的持久化策勒可以更换. 服务类提供了set,get方法,用于获取底层持久化实现类
3. SpongeArrayBlockingQueue初始化时会创建一个内存缓冲区MemoryItemList.用于在队列满了之后,将溢出的任务暂时保存到内存缓冲区中
4. 线程池ThreadPoolExecutor的创建需要提供BlockingQueue的实现,这里就是SpongeArrayBlockingQueue
5. SpongeArrayBlockingQueue.doFetchData_init(ThreadPoolExecutor)初始化会使用前面3.中创建的内存缓冲区,调用fetchData_init
   因为系统不一定是第一次就运行的,可能已经持久化过了,在接下来的启动中,需要还原之前写到持久化文件中的任务.

## Memory And Persistence

6. MemoryItemList提供了添加请求到缓冲区中,生成一批字节流并调用往持久化文件中写入一批数据的抽象方法(持久层实现),初始化,和消费.
   当队列满时,就往缓冲区中添加请求; 当请求的数量超过设定的阈值,就开始生成一批字节流,字节流有一定的序列化格式以保证数据的紧凑.

   持久层的接收到数据后,也不是立即写,而是使用inMemoryDataList内存来存放.
   并且有一个后台线程用于从inMemoryDataList中消费数据才将字节流写到文件中.

   注意对于MemoryItemList而言是请求数量达到阈值(200)时开始批量写,而对于BasePersistence则是内存不超过阈值(50M)则继续写文件.
   BasePersistence提供的后台线程在消费inMemoryDataList中的字节流时也不是一次性的将整批数据写入,而是分批处理,一批20个的写.

   为什么要分开设置2个阈值. 因为对于队列而言,关注的更加是队列中的任务数量. 而要写文件时不能一次将大量内存数据写到磁盘,所以更关注内存.
   WHAT? WE ARE SAYING MEMORY,NOT QUEUE?! BUT MemoryItemList IS A REF IN BLOCKING QUEUE. AND ALSO INIT AT CONSTRUCT TIME.
   那么队列中请求的数量怎么和内存进行关联? 在生成批量数据时,会将请求序列化成字节流,传给BasePersistence.这样BP就可以根据字节流计算内存了.
   BasePersistence中内存的计算公式是进入inMemoryDataList中则内存增加,后台线程从中取出数据写到磁盘后,则内存减少.

## Let's Talk About Consume

当队列中没有需要消费的任务时,会去查看内存中和磁盘中是否有需要消费的任务. 任务的写对应生产任务,任务的读则是消费任务了.
写到持久化文件中的任务是按照一定序列化格式的.读取出存储的任务也是按格式反序列化的(注意在写任务时是一批一批任务的写).

数据文件的格式如下:

MAGIC HEADER|totalSize|ItemSize1|   Item  |ItemSize2|  Item   |...Other Items
2bytes      |4bytes   |4bytes   |ItemSize1| 4bytes  |ItemSize2|

取出一批数据后,我们要依次将其中的每一个序列化后的Item转换成任务类,并用线程池去执行任务,从而完成任务的消费.
任务一旦消费完毕,就不需要再次消费了. 所以有一个索引文件用来存储当前已经消费的位置. 下次消费时要读取这个值继续消费.

在BasePersistence.fetchOneBatchBytes中调用持久层获取一批数据后,可能已经没有要消费的任务了.
但是还有其他情景: 因为调用持久层只是针对持久化的实现, 但是要注意的是溢出队列的任务首先是存储在内存缓冲区中的!
所以要再次分别检查后台线程的theOutBytes是否有待消费数据优先返回, 如果没有则检查inMemoryDataList中是否有待消费的数据.
因为在检查后台线程的这段时间内,后台线程可能没有及时检测到,但是实际上内存中还可能有需要消费的数据的.

## Resource Release
没有需要消费的任务,即可以释放资源. 因为持久化文件中记录了之前溢出的任务, 这些任务都被消费后,这个文件就可以删除了!