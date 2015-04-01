Reactive Streams in Java
====

https://github.com/jhusain/learnrxjava/

A sequential program runs on a flat timeline.  Each task is only started after the previous one completes. In concurrent programs, multiple tasks may be running during the same time period and a new task may begin at any time.

单线程的程序按照时间的先后执行. 每个任务只有在前一个任务完成后才开始启动.
在并发编程中, 多个任务可以在同一个时间点运行, 并且在任务时候新的任务都会启动.

In threaded programs, introducing concurrency trades space for time. Allocating memory for more threads allows application servers to make network requests concurrently instead of sequentially. Threaded network requests can dramatically reduce server response times, but like all trade-offs this approach has its limits. Unchecked thread creation can cause a server to run out of memory or to spend too much time to context switching. Thread pools can help manage these problems, but under heavy load the number of threads in an application server’s pool will eventually be exhausted.  When this happens network requests will be serialized, causing response times to rise. At this point the only way to bring down response times again is to scale up more servers, which increases costs.

在多线程程序中, 引入了并发, 会有一定的时间消耗的代价. 为服务器分配更多的内存, 可以处理更多的线程并发请求, 而不是处理顺序请求.
并发请求会降低服务器的响应时间, 因为如果服务器一次处理一个请求, 后面的请求则要等待前一个请求处理完毕才能处理下一个请求,
并发就不会有这个问题, 但是并发请求也有一定的限制条件, 任何优化都是有一定代价的. 比如以时间换取空间, 或者以空间换时间都是有代价的.

不断创建线程会导致服务器内存溢出, 或者在上下文切换中花费了太多的时间(因为线程太多了!服务器要管理这些线程,来处理不同的请求).
线程池可以解决这种问题. 但是如果服务器加载了大量的线程(重负载), 也会造成服务器性能下降. 所以应该选择适合数量的线程在线程池中, 一旦不使用,应该回收.
当这种情况发生时, 网络请求会被序列化 导致服务器的响应时间升高. 因为序列化后, 服务器在处理请求时, 还要进行反序列化.
在这种情景下, 降低服务器响应时间的唯一方式是增加服务器的数量(当然成本也增加).

Reactive programming allows concurrent network requests to be made without threads. Instead of creating a thread which immediately blocks on IO, a callback is asynchronously invoked when data is received from the stream. This dramatically increases the number of open connections an application server can manage at any time. Furthermore it allows application servers to better tolerate long-running connections, either due to a failure in a downstream service, or the use of a persistent connection protocol such as web sockets.

Reactive编程允许并发请求, 但是没有使用线程来处理. 因为创建一个线程阻塞IO. Reactive使用回调的方式: 当从流中接收到数据时, 回调会被异步地调用.
这种方式能够显著地提升服务器的连接请求数, 并且可以在任何时候管理这些打开的连接. 并且允许容忍长连接, 比如下游服务的失败??, 或者使用持久化的连接协议.

Concurrent programming is inherently more complicated than sequential programming, because concurrent programs force us to think multi-dimensionally. At first, concurrency may seem overwhelming. How to produce clear, concise, and correct code in the face of all of this additional complexity?

并发编程天生比顺序编程要来的复杂, 因为它迫使我们以多线程的维度思考问题(多线程引入了锁,同步等复杂的机制).
一开始,并发看起来势不可挡. 然而当需求变得复杂时, 如何写出清晰的, 精确的, 正确的代码是一个巨大的挑战.

Reactive programming adds additional complications. Reactive APIs hold onto references to our objects through our callbacks. In the event of an error we must ensure that these Reactive APIs free their references to our callbacks so that they can be garbage collected, as well as cancel any ongoing tasks.

Reactive编程面临一个新的问题. Reactive的API通过回调机制为我们的对象持有(某种事件的)引用. 如果处理的事件发生错误, 我们要保证API能够释放持有的引用对象从而回收内存.

The good news is that in practice, managing concurrency with reactive programming is not as complicated as it appears. In fact most concurrency and parallel problems can be solved with a few simple functions. First you will learn how to use these functions to transform I datatype that you are already comfortable with: the Java List. Then we will learn how you can apply the same functions to streams of data arriving over time.

好消息是在实际中, 使用Reactive编程来管理并发并不像看起来那么复杂. 实际上只要写一点点的函数就可以解决大部分的并发/并行问题.
首先我们要学习如何使用Java的List运用这些函数. 然后我们将流数据运用到这些函数上.

