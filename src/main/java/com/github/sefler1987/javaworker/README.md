
## Linear Design

**Task TaskProcessor**
Task表示任务, TaskProcessor是真正执行任务的逻辑. 执行任务会产生新的URL, 会添加到Task的待挖掘URL集合中.
Task有两个变量: String targetURL表示目标URL, HashSet<String> minedURLs是从这个目标URL中挖掘出来的链接
TaskProcessor会访问任务的目标URL, 并将目标URL的内容中的链接加入到任务的minedURLS中.
一个目标URL都会产生一个任务. 并分配给工人执行任务.

**Worker Thread**
将URL传给Task,给工人分配任务. 工人会用队列来保存任务. 并启动后台线程从队列中消费任务, 然后使用TaskProcessor运行任务.

**Event Listener**
由于任务的执行可能很长, 因此给工人分配完任务后, 不是等待工人完成任务. 而是立即返回任务的id.当工人任务完成的时候,需要通知调用者.
在还没启动工人的工作线程之前, 要给工人注册事件的监听器. 当TaskProcessor完成任务,会触发一种事件类型. 并回调监听器的onEvent方法.
listenerMap保存了一个WorkerEvent和对应的监听器列表List<WorkerListener>. 在注册的时候添加到listenerMap中.
在触发事件时,从listenerMap中根据任务完成的状态对应不同的事件,取出对应的监听器列表,进行回调.
所以自定义的监听器需要对不同的事件做出不同的响应.
LinearURLMiningMain的onEvent对完成的任务,取出任务的minedURLs加入到foundURLs中.
在主线程中会取出foundURLs的URL,作为新的任务分配给工人执行.

## MapReduce Design

URL的挖掘分成2个步骤:
1. 访问URL, 获取URL的页面内容
2. 使用正则表达式匹配页面内容中的链接

MapReduce会将第一步交给Map完成, 第二步交给Reduce. Map的结果会给Reduce.
Map工作的监听器是Map2ReduceConnector, Reduce的监听器是MapReduceURLMiningMain

当Map任务完成时, 触发其监听器Map2ReduceConnector的onEvent方法: 找出一个reduce任务,将map任务的结果交给它处理
当Reduce任务完成时,触发其监听器MapReduceURLMiningMain的回调: 取出reduce任务的执行结果将需要挖掘的URL加入foundURLs

Reduce任务的监听器MapReduceURLMiningMain的回调 和 LinearURLMiningMain的回调一样.
Linear是线性执行任务(访问URL后,寻找链接),最终将链接加入foundURLs.
而MapReduce的Reduce任务完成后,就是一个Job的完成, 其最终要实现的功能也是要将找到的链接加入foundURLs.

在MapReduceURLMiningMain中只需要将MapTask交给不同的Worker处理:一个MapTask交给一个Map工人执行.
当MapTask完成后,会将任务的执行结果设置到任务中. 然后触发监听器的回调: 选择一个Reduce,将任务添加到Reduce工人中.

下图表示了MapTask的结果可以交给ReduceTask处理.
 _______       _________
|MapTask| --> |MapWorker|
 _______       _________            __________       ____________
|MapTask| --> |MapWorker|          |ReduceTask| --> |ReduceWorker|
 _______       _________            __________       ____________
|MapTask| --> |MapWorker|          |ReduceTask| --> |ReduceWorker|
 _______       _________
|MapTask| --> |MapWorker|


## Summary

线性的挖掘URL使用MapReduce算法,将不同的步骤分解成对应的Mapper和Reducer. 这样可以使用多个Mapper进行多线程执行.
线性的挖掘任务完成后, 触发的监听器回调将找到的URL加入待挖掘的列表中.
Map执行完毕后要触发的监听器回调, 会选择一个Reduce处理, Reduce处理完毕后, 也会触发监听器的回调将找到的URL加入待挖掘的列表中.

线性的挖掘任务只需要在任务最后完成时,触发监听器回调. 而MapReduce则在Map,Reduce任务完成后都触发相应的监听器回调.