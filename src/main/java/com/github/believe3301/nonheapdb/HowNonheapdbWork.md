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

## Put
添加记录时首先创建Record对象, Record提供getBuffer和setBuffer.
Record记录的格式是:MAGIC|next|keyLength|valLength|key|value
根据添加的key,value构造Record,就可以组合出记录的格式,getBuffer就能获取到记录的字节形式.可以用于MemoryBuffer的put操作.

在添加前后会用到BucketManager桶管理.桶用来解决key的散列冲突问题.
相同key如果在同一个bucket里,要使用链表的形式将多条记录放在同一个bucket里.
这里链表的实现采用了Record的next和parent. next类似指针指向下一条记录,而parent则是上一条记录.

数据结构中添加元素到链表表头时,返回的是最新添加的元素的操作过程:
````java
    Node oldFirst = first   //没有添加元素前的链表表头
    first = new Node()      //要添加的最新元素
    first.next = oldFirst   //新元素的下一个元素是旧的表头.现在first是表头了
    return first
````

BucketManager的getBucket和setBucket发生在添加记录前后.
在添加前getBucket是为了知道当前记录的下一个记录(原先的表头)是哪条记录(next代表了RecordIndex,就能找到代表的记录)
在添加后setBucket是为了将当前记录的RecordIndex.getBucket()设置到桶里.

## Bucket
RecordIndex的getBucket怎么和BucketManager的bucket联系上的?
保存在BucketManager数组buckets中的每个元素的值是RecordIndex.getBucket()的计算结果.

BucketManager的底层用数组实现,实际上Map也是用数组实现的. 数组的每个元素对应一个(Map的)桶.
存放在桶里的数据也是bucket:即RecordIndex的getBucket()的计算结果.
在添加记录时,如果被散列在已经有记录存在的同一个桶里,则最新加入的记录的getBucket()会覆盖先前存在记录的bucket的值.

## Get
在put时设置记录的next引用. Record的RecordIndex index和Record parent会在get时设置.

BucketManager
 buckets
    |          ___ next   ___ next   ___
[bucket01]    |___|----> |___|----> |___|
    |          ___
[bucket02]    |___|
    |          ___ next   ___
[bucket03]    |___|----> |___|
    |
    |->firstRecord's bucket

获取数据时,根据key定位到buckets的桶,取出里面存放的bucket,但是这个值并不一定是我们要的记录的RecordIndex的bucket.
因为其他记录通过key散列后可能也落在这个桶里,但是buckets[]桶里保存的是第一个/最近加入记录的RecordIndex的bucket.
所以我们要遍历桶里面存放的链表,找到key相同的才算找到key对应的记录. 这个时候记录的next指针排上用场了.

查找链表的过程: 首先根据当前桶的值创建RecordIndex进而得到Record.判断Record的key是否是想要的key.如果是就直接返回.
如果不是,将循环的index设置为当前记录的rec.getNext()在下一次循环时,就能根据最新的index找到下一条记录的RecordIndex.

## removeRecord
Record的setParent发生在get时找到元素时,因为在上一次的循环时保留了上一条记录的RecordIndex.
getParent操作发生在removeRecord删除记录时. parent为空在链表表头的记录一定是满足的.
如果删除的是表头,因为原先bucket桶保存的是表头的RecordIndex的getBucket计算结果.
删除表头后,要将表头的下一个记录的RecordIndex.getBucket保存在桶里.
    map.setBucket(key, rec.getNext());
如果删除的不是表头,则删除的链表的中间节点. 只需要更改前一个节点的next引用为下一个节点的引用.
    pb.putLong(rec.getNext(), rec.getParent().offset() + 1);
上面第一个参数是写入的内容,第二个参数是offset.
写入的数据是下一个节点的引用:存在当前节点的next字段里.
要写的offset位置是上一个节点的next字段:找到当前节点的父节点的offset,再加上一个字节的偏移量.

数据结构删除链表中非表头节点的操作过程:
```java
    Node prev = node.parent     //要删除节点的上一个节点
    Node next = node.next       //要删除节点的下一个节点
    prev.next = next            //将(删除节点的)上一个节点的next引用指向(删除节点的)下一个节点
```
 ___ parent ___ next  ___
|_p_|<-----|_D_|---->|_n_|      p = D.parent; n = D.next
             |
             |-->Delete         D.parent.next = D.next
                                    |
 ___      next        ___           |
|_p_|--------------->|_n_|      p.next = n
           ___
          |_D_|                 D has deleted!


## set and get time
Operation                  |When
---------------------------|-----------------------------------
BucketManager.getBucket     MM.put添加记录前设置当前记录的next指针
                            MM.getRecord根据key构造RecordIndex,找到桶的链表表头记录
BucketManager.setBucket     MM.put添加记录后更新index
                            MM.removeRecord删除记录前,如果parent为空,设置桶里存放的数据为要删除记录的next值
RecordIndex.getBucket       MM.put添加记录后,将当前RecordIndex计算的bucket设置到对应的桶里
Record.setParent            MM.getRecord找到记录,设置上一条记录为当前记录的父节点
Record.getParent            MM.removeRecord
Record.getBuffer            MM.put添加记录时获取字节数据
Record.setBuffer            MM.getRecordInner读取字节数据构造Record

#### How DBCache Work
DBCache封装了MemoryManager, 它提供的put方法会给客户端使用.它的实现会调用MM的put和getRecord方法.

#### WHAT HAVE U LEARNED
1. API分层封装. DBCache给客户端调用, MemoryManager给DBCache使用,并调用MemoryBuffer. MB针对底层的ByteBuffer.
2. MemoryBuffer使用Java NIO的ByteBuffer.使用2个ByteBuffer, 通过slice创建ByteBuffer,会共享底层的数据
3. RecordIndex索引记录的构造和解析,通过bucket可以还原出组成RecordIndex的三个字段.反之亦可构造bucket.
4. 空闲块的管理,放入空闲块的数据是RecordIndex构造出来的bucket. 实际上是记录的索引. 这样可以方便地定位到空闲块.
5. MM管理多个内存块MB. 写入记录优先选择内存块中的空闲块写入.
6. MM使用BucketManager桶管理来解决查找数据的散列冲突.只有在查找时要解决散列冲突,添加记录时不需要考虑,只管存数据.
7. BucketManager的桶存放的是散列冲突链表表头记录的RecordIndex的getBucket.
8. 因此在添加,删除,获取时都需要和BucketManager桶里存放的bucket值交互.
9. 碎片整理