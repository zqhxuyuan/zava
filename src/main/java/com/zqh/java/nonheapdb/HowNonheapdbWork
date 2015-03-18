#### What's Non-Heap

https://www.yourkit.com/docs/kb/sizes.jsp

Heap and Non-Heap Memory

The JVM memory consists of the following segments:

+ Heap Memory, which is the storage for Java objects
+ Non-Heap Memory, which is used by Java to store loaded classes and other meta-data
+ JVM code itself, JVM internal structures, loaded profiler agent code and data, etc.

JVM主要管理两种类型的内存：堆和非堆
堆是运行时数据区域，所有类实例和数组的内存均从此处分配
堆就是Java代码可及的内存，是留给开发人员使用的；非堆就是JVM留给 自己用的，
所以方法区、JVM内部处理或优化所需的内存(如JIT编译后的代码缓存)、
每个类结构(如运行时常数池、字段和方法数据)以及方法和构造方法 的代码都在非堆内存中

#### How Nonheapdb Work
Nonheapdb包括三个组件DBCache, MemoryManager, MemoryBuffer 以及2个模型: Record, RecordIndex
其中组件提供了记录的操作. 模型表示记录和记录的索引格式

## Put
MemoryBuffer使用了2个ByteBuffer来操作记录. 它们通过slice都共享底层的数据.
添加记录时先写在buf_append中追加记录,这条记录的开始写位置offset是buf_append的position.
即buf_append写到哪里,哪里就是一条新记录的开始.
putData提供了ByteBuffer和一个可选的offset.
如果只有一个参数的ByteBuffer,则返回记录的索引.
如果带offset参数,则直接在指定的offset开始写入ByteBuffer数据.
一般对外提供的是不带offset的putData,而带offset的putData提供给其他方法调用.

## RecordIndex
记录的索引RecordIndex包括offset:记录在内存块中的偏移量,capacity:记录的容量,index:在哪个索引块
RecordIndex的构造函数接受long类型的bucket,通过bucket可以解析出RecordIndex的三个字段.反之也可以构造出bucket.

为什么添加记录后返回的是记录的索引,而不是记录本身? 不需要返回记录,是因为记录的value往往是比较大的,
实际上通过记录的索引就可以定位到内存块中的记录. 而且记录的索引占用的字节数也是比较小的.

## Get
获取记录时,使用buf的slice拷贝,因为buf和buf_append的底层数据是共享的.

## Remove
删除记录时,会将记录标记为空闲块,并加入空闲池fp:free pool中.
或者在内存块的末尾剩余的空间不够写入记录的长度,则将剩余空间保留起来,用在后面记录长度较小时分配.

删除记录或者保留空间都会产生空闲块,写数据时优先写在空闲块里.如果空闲块不够存放数据,则写在当前块的后面(或者新建一个内存块)
放入fp中的是RecordIndex.getBucket的计算结果.根据bucket可以还原出RecordIndex.
因此removeRecord和remainToRecord都会传递RecordIndex参数或者手动构造出RecordIndex,计算bucket放入fp中.

## FreeBlock
放入fp中的空闲块在添加记录时,如果可以写空闲块,则优先选择空闲块存储.选择满足写入记录长度的空闲块findFreeBlock是写操作的前奏.
由于每个空闲块的大小不一样,可能比要写入的记录的长度length大很多,或者小.所以对fp按照RecordIndex的capacity进行排序.

找到空闲块后,要将当前空闲块从fp删除,并且返回当前空闲块,和添加记录一样,返回的也是RecordIndex对象.
因为RecordIndex封装了offset,index等信息,我们才能知道要往哪个内存块(index)的哪个空闲块(offset)开始写数据.

如果满足长度的最小空闲块的大小都比要写入记录的length的2倍还大,如果把空闲块都分给记录显然太浪费了.
所以当前选择的空闲块除了记录需要的length外,剩余的空间可以再分配出一个空闲块出来.

## defragment碎片整理
为什么会产生碎片? 因为写入的记录和删除的记录的长度的不一致,新添加的记录在写入删除记录产生的空闲块后可能剩余一点空间.
如此累加起来,系统就会产生碎片,这和Windows的碎片整理类似.

TODO HOW DEFRAGMENT WORK

#### MemoryManager
内存管理,管理所有的内存块. 内存块的单元是MemoryBuffer.
MemoryManager的put方法参数是String key, byte[] value更加上层,而MemoryBuffer针对ByteBuffer更加底层.

添加记录时首先创建Record对象, Record提供getBuffer和setBuffer.
Record记录的格式是:MAGIC|next|keyLength|valLength|key|value
根据添加的key,value构造Record,就可以组合出记录的格式,getBuffer就能获取到记录的字节形式.可以用于MemoryBuffer的put操作.



