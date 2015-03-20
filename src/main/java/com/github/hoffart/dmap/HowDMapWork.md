#### TEST

```
DMapBuilder dmapBuilder = new DMapBuilder(mapFile);
dmapBuilder.add(key, value);
dmapBuilder.build();

DMap dmap = new DMap.Builder(mapFile)
		.preloadOffsets()   // loads all key offset information at start
		.preloadValues()    // loads all values along with key at start
		.build();           // returns a DMap configured based on previous calls
byte[] retrieved = dmap.get(key);
```

#### How DMapBuilder add
1. 接收文件参数,用于持久化数据到文件中
2. 添加key-value键值对
3. 持久化数据到文件中

添加数据时,直接追加写到临时文件中,临时文件的格式是keyLen,key,valLen,val.
这样可以加快写的速度.而只在build时创建MapFile.
MapFile包括数据块Block, 数据块Trailer: BlockTrailer, 数据块Trailer的Trailer:GlobalBlockTrailer.

由于MapFile的数据格式最开始是Block,所以在读取临时文件的过程中,要保存每个Block的Offset.
当一个Block的数据块内容达到设定值,开始写入BlockTrailer.

为什么要把Block和BlockTrailer:里面的内容实际上是value和key分开来写.
主要是为了value的压缩. 因为value的数据信息一般都是一样的.

BlockTrailer记录key和offset, offset是value在Block中的位置.
GlobalBlockTrailer记录的是Block和BlockTrailer在文件中的位置.

#### How DMap get
put操作由DMapBuilder完成,并进行持久化. get操作由DMap完成,并读取MapFile文件加载到内存中.
Map的读取可以预先加载key-offset或者加载全部的value到内存中.

构造DMap时, 会先读取和key相关的BlockTrailer:
预加载key-offset, 放到blockTrailerKeys: <BlockTrailerOffset, <key,offset>>
没有预加载key-off, 放到blockTrailerBuffer_: <BlockTrailerOffset, trailerBuffer>
    trailerBuffer存的是BlockTrailer的信息. BlockTrailer里有多个key

预加载value, 缓存cachedByteBuffers_: <firstKey, mappedBuffer_>
    mappedBuffer_存的是Block的信息, Block里有多个value
如果没有预加载value, 则新建cachedByteBuffers_,在get时,获取到数据后存到缓存里

#### Get AND Cache
在get(key)时, 因为offset保存在BlockTrailer里, 要首先获取value的offset.
如果在构造DMap时,有预加载key-offset,则直接从blockTrailerKeys中读取
如果没有预加载过key-offset, 则从blockTrailerBuffer_中读取trailerBuffer循环比对,比对成功即可以读取出offset

cachedByteBuffers_缓存的是firstKey和数据块的buffer. 要获取key对应的value,
根据key得到数据块的buffer:blockMapBuffer, 判断是否为空, 如果为空, 说明没有预加载value.
如果有值,说明在构造时预加载了value. 如果为空,则读取Block内容,放到cachedByteBuffers_中.

现在我们有了offset和blockMapBuffer. 其中blockMapBuffer对应了一个完整的数据块的内容.通过offset即可读取出value

#### Iterator and Next
MapFile文件的格式是Block, Block, ..., BlockTrailer, BlockTrailer, ... , GlobalBlockTrailer.
DMap中有2个Iterator分别对应了是否预加载key-offset. 因为读取的时候传的是key. 所以无需考虑是否预加载value的Iterator.

提供迭代器的目的是,当我们读取一个key-value后,要连续读取接下来的数据. 而不是传递一个key,随机读取
根据key随机读取, 或者连续读取, 都需要BlockTrailer信息来定位value所在的Block的offset.

因为连续读取并没有手动传递key, 就需要自己去找出当前key接下来的key. 而key是在BlockTrailer里.
有没有预加载key-offset的不同点是是否手动解析BlockTrailer里下一个key或者直接从内存中读取key.

如果没有预加载key-offset, 则用blockTrailerBuffer_构造BlockTrailer Iterator.
因为在读取完一个BlockTrailer里所有的key后,要能够接下去读取下一个BlockTrailer的key.

如果预加载了key-offset, 则用blockTrailerKeys构造BlockTrailer Iterator.

BlockTrailer里面的key的迭代获取. 在有key-offset时,blockTrailerKeys的迭代器的keySets都是key.
因为blockTrailerKeys:<blockTrailerOffset, <key, offset>>即这里的key.
没有key-offset时,从blockTrailerBuffer_迭代器获取的元素是一个Buffer,因此要手动读取

#### What Have U Learned
put->DMapBuilder

1. Map File Format has 3 part: Block, BlockTrailer, GlobalBlockTrailer.
2. A BlockTrailer is the trailer of the Block, and GlobalBlockTrailer is the trailer of all Blocks and BlockTrailers.
3. When write, keep sth in mem for later write. such as trailer info.
4. key and offset are all in BlockTrailer, when get key, search the offset. and u can get value from Block.

get->DMap
1. Provide two option for quick search. preLoad key-offset, or values.
2. When get, the only param provide to us is the key. so BlockTrailer is the most import to operate.
3. For preLoad key-offset, load BlockTrailer info into memory. and contains key direct to offset.
   for unPreLoad, load all total BlockTrailer Buffer
4. Get the value of key, the first thing is getting value's offset. which is in BlockTrailer.
5. If preLoad value, cache the firstKey map to total Block Buffer.If not, cache is happen when get key.
6. For iterate read. We support two kind of Iterator which based on whether preLoad of key-offset or not.
7. Iterate read should consider about the BlockTrailer boundary.
   because we want to read all value of all Block Data. so we should iterator all BlockTrailer.
