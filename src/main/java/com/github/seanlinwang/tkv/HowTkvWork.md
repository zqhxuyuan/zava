#### Local TEST

```
File dbFile = new File("/tmp/tkvtest.db");
Tkv tkv = new TkvImpl(dbFile);

String key = "01234567";
String value = "ayellowdog";

tkv.put(key, value.getBytes(), "pet", "dog");
tkv.get(key);
Record r  = tkv.getRecord("pet", key);
```

#### How LocalImpl Work
1. 根据put(k,v,t)创建Record
2. 将Record序列化到文件中
3. 添加2个索引信息

创建Record的时候根据k,v,t设置到Record对象中.
在序列化的时候,在开始位置首先写入keyLen,valLen,tagLen.
这3个值可以分别由k,v,t计算出来.

索引信息放到keyValueIndex,保存key->IndexItem的映射.

当get时,根据key从keyValueIndex中获取IndexItem读取出这条记录对应的body,并解析出value.

#### How Tag Work
一个key可以有多个标签. 标签存储在文件中以指定的分隔符存储.
索引标签采用倒排索引. 假设一个key有多个标签. 则循环每个标签,都把当前key放入每个标签中
同时还在keyValueIndex的IndexItem中存储tag-pos-index.即key对应的每个tag出现的顺序

查询时除了提供key参数,还可以提供额外的tag参数查询.
这就用到了前面的倒排索引,根据tag查询key是否属于这个tag.

使用tag的目的是查询属于这个tag的上一个或下一个key.
因为倒排索引已经把数据当前tag的key都放入List中.所以遍历List就可以快速找出上一个/下一个key.

#### HDFS Test

```
hdfs = new HdfsImpl(localHdfsDir, localDir, localIndexFile, localDataFile, 64, 100);
hdfs.startWrite();
hdfs.put(m1.getKey(), value1.getBytes());
hdfs.buildIndex();
hdfs.endWrite();
```

#### How HDFSImpl Work
Local存在磁盘的文件包括了key,value,tags以及对应的length放在每条记录的最前面.
并且在内存中维护keyValueIndex和tagListIndex,用于根据key快速定位offset和根据tag获取keyList

但是HDFS中不能在内存中维护上面2个索引.所以需要单独把索引也保存在HDFS中.索引文件和数据文件是分开存储的.
Meta对象记录了key和offset的映射关系,当然也有key对应的tags. Meta最终会存储成HDFS文件.

写数据的流程是:
1.准备写,创建HDFS文件的输出流对象
2.追加数据,往HDFS输出流中追加数据
3.构建索引,先往本地文件中追加索引数据
4.结束写,将本地索引文件复制到HDFS上

写数据时,直接追加到HDFS的dataStore文件里.注意在追加之前,先获取文件的长度,作为写入数据的offset,记录在Meta中
每追加一条数据,就往HDFS的数据文件中追加数据,但是并没有往索引文件中追加数据.考虑到数据大量写入时,索引数据分开操作
因此在追加数据之后,将构建的Meta暂时保存到内存的List metas中. 只有在手动调用buildIndex时才将metas写到索引文件中
索引文件也并不是直接写到HDFS中,而是先保存在本地,当手动调用endWrite()时才将索引文件复制到HDFS上.

            |-->HdfsIndexStore indexStore ---> RAFIndexStore
HdfsImpl ---|
            |-->HdfsDataStore dataStore

#### HDFS IndexFile Format
和Local的文件存储不一样的是, HDFS数据单独存储,所以索引文件的格式也是不一样的.
Local File Format:
  |keyLen|valLen|tagLen|key|value|tags|

HDFS IndexFile Format:
  |key|offset|valueLength|tags|

其中key的长度和tags的长度都是在构造RAFIndexStore时传入的,因此是固定的,而offset和valueLength都是int类型=4个字节
并没有为IndexStore设计成如下格式: keyLen|valueLength|offset|key|tags
而是固定字节数.如果写入的key,tags没有够默认的,读取时也是读取固定的字节数.这样的好处是不需要进行额外的计算.

RAFIndexStore的构造函数中计算一条记录的索引长度indexLength的方式:
  this.indexLength = this.keyLength + OFFSET_LEN + LENGTH_LEN + this.tagLength;
分别对应了HDFS IndexFile Format四个字段的长度.

#### RAFIndexStore
HdfsIndexStore的实现是通过RAFIndexStore来完成的.HdfsIndexStore还要完成HDFS文件系统的索引文件的存储.
RAFIndexStore的append方法完成了索引文件格式的存储.其中offset和value.length通过右移位来完成.
因为这2个字段都是4个字节=4*8=32位.右移24位=只取前8位=最高位字节/第一位字节.
右移16位=取前16位,由于要转成byte,只取最右边的8位....

#### WHAT HAVE U LEARNED
1. LocalImpl use keyValueIndex to keep key->offset on memory
2. HdfsImpl store index file and data file separate.
3. HDFS DataFile format and IndexFile format
4. HDFS DataFile and IndexFile operation step.
