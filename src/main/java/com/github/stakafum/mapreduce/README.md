mapreduce
=========

https://github.com/stakafum/mapreduce

My MapReduce implementations and sample programs written by Java

## WorkFlow
1. 创建MapReduce, 指定自定义的Mapper, Reducer
2. 给Mapper指定输入数据, 通过InputData.putKeyValue添加到initialKeyValue
3. MapReduce分成三个过程: startMap, startShuffle, startReduce
MapReduce的经典过程是: 对输入的每个分片进行map, shuffle会对Map的计算结果进行排序和分组,
Reduce使用分组后的数据对相同key的数据进行reduce计算.

4. 可以为MapReduce指定并行度. 所以要将任务分配到每个线程去执行
由于每个线程在同一时刻只能执行一个任务. 但是多个线程可以同时执行不同的任务.
所以一次MapWork()执行的任务数量是线程的数量.

根据线程数和每个线程执行的任务数可以获取每个线程中每个任务[任务级别]的输入数据.
由于Mapper是由输入数据构成的,将自定义Mapper封装成MapCallable,传给FutureTask.
采用并发执行的方式提交任务执行. 任务的提交执行在MapWork中.

当任务线程启动,会调用FutureTask.call,从而调用到MapCallable.call(), 再回调到自定义Mapper的call方法
自定义的Mapper是会对输入的每条数据都通过mapper函数的转换.
MapCallable返回自定义的Mapper对象,所以任务执行完毕后,通过FutureTask.get()返回的也是Mapper对象.

Map的计算结果作为临时数据, 经过Shuffle过程的排序和分组.
InputData封装了这个过程. InputData有三个变量:
  initialKeyValue: 原始输入数据
  mappedKeyValue: map计算结果
  gKVList: 每个key对应的分组数据
原始输入数据经过map函数计算后,会设置到mappedKeyValue中. gKVList是通过对mappedKeyValue进行分组得到的.
在计算gKVList时,要先对mappedKeyValue进行排序. 这样相同的key会排列在一起. 在如何确定一个组的边界时很关键.

Reduce的过程和Map的过程很类似.不同的是Map的数量通过输入文件确定,
而Reduce的数量通过输入文件中不同key的数量确定.
因为在排序和分组后,相同的key会被同一个Reduce处理.

Reduce的输入数据来自于分组gKVList的数据. 对于同一个key, 会有多个value. 这些value的key都是相同的.

Map和Reduce过程的任务分配都涉及到任务数量不能和线程数整除的情景,最后都要做一次处理.
回调自定义的Mapper和Reducer的过程也一样. 通过FutureTask提交执行任务和获取任务的执行结果也都一样.





