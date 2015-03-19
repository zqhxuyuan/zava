# DMap - A Minimalistic Disk-Backed Java Key-Value Store

## Overview
A very simple disk-backed key-value store written in Java. 

The map can be built only once, afterwards it is read-only, thus the use-cases 
are somewhat limited to a scenario where the key-value pairs need to be served read-only.
Map只能被创建一次,之后是只读的,因此适用的场景是:键值对只读

I wrote this mainly to have a simplistic store that is memory efficient and
does not come with a lot of overhead as Lucene or Berkeley DB. The main 
trade-off is speed and memory efficiency (DMap) versus flexibility(Lucene, BDB).

Keys and values are byte[], on disk the overhead are one additional int per 
key and value for keeping the size, plus one additional int per key-value-pair
for keeping the offset. The underlying file is put into memory on demand using
a java.nio.MappedByteBuffer, with the benefit that everything can be preloaded on startup.
键值对都是以字节数组的形式存储,因此在磁盘上都需要额外的int长度来保存键值的大小,还要保存一个键值对的offset.
底层的磁盘文件会被放到内存中的MappedByteBuffer,它的好处是在启动时可以把文件都加载到内存中

## Example
```
// Build dmap.
File mapFile = new File("FILEPATH");
byte[] key = ByteBuffer.allocate(4).putInt(2).array();
byte[] value = ByteBuffer.allocate(4).putInt(23).array();

/*
 * Following constructor sets the default block size to 1MB.
 * To customize block size use the version (mapFile, blockSize).
 */
DMapBuilder dmapBuilder = new DMapBuilder(mapFile);
dmapBuilder.add(key, value);
// Finalize map. 持久化Map数据
dmapBuilder.build();

// Load Customized DMap using Builder
DMap dmap = new DMap.Builder(mapFile)
		.preloadOffsets() // loads all key offset information at start
		.preloadValues() // loads all values along with key at start
		.build(); // returns a DMap configured based on previous calls
// Get key.
byte[] retrieved = dmap.get(key); 
```

## Usage Scenario

Read-only, random access key reads with no iteration.
适用场景是: 只读,随机读取

## Upcoming Features

 * Make DMapBuilder appendable.
 * Special int-int mode for more efficient storage.
 * String-String mode.
