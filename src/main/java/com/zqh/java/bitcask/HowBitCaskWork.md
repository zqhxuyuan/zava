####From TEST TO KNOW API

```java
BitCask<Student> studentBitCask = BitCask.of("Student");
String key = "zqh";
Student value = new Student("zqh",25,"zqhxuyuan@gmail.com");
studentBitCask.put(key, value);
assertThat(studentBitCask.get(key), is(value));
studentBitCask.dumpIndexTo("Student.index");
```

####连续写入,随机读取

写入数据,put的key是字符串, value是自定义对象. 读取时根据key,返回对应的自定义对象.
通常是对同一种类型的数据集, 有不同的key和value.
然后要求查找时在该类数据集中根据key, 查找出对应的value.

文件的写入直接在文件末尾追加是最快的.磁盘文件只存储自定义对象序列化后的二进制数据.
由于同一个数据集不同的value都写到同一个磁盘数据文件中.
读取value时,要能区分是哪个key的数据内容.

需要在内存中维护写入的key和磁盘数据文件对应的value的映射关系:
对于文件而言,每写入一次数据,都会在指定的offset处开始写入数据,写入长度为size的数据内容.

所以如果能够在内存中维护key, offset, size的信息, 则要获取key对应的value时,
查找内存中的key对应的offset,size. 然后到指定的数据集对应的磁盘数据文件中
定位到offset处,读取出size个字节, 就能还原出写入时的数据内容.
显然要知道写入和读取操作的文件名,所以在内存中除了key,offset,size,还要知道数据集对应文件名filename.

key要和offset, size, filename进行映射.用面向对象的方式是定义一个Index Bean,包含这4个字段
然后用Map在内存中定义这种映射关系: key-->Index(key,offset,size,filename)

由于数据可以持久化到磁盘数据文件中. 写入数据就可以持久保存. 但是如果内存中的索引信息一旦丢失,
数据文件根本没有任何数据对应的key信息, 所以应该考虑把内存中的索引信息也持久化一份.
否则只有数据没有索引, 要查找key时,由于内存的key索引数据丢失,value信息根本无法获取.

假设数据文件是Student, 则索引文件是Student.index
索引文件的数据结构是一个Map:<key, Index(key,offset,size,filename)>, 我们定义为Indexer Bean

#### How To PUT
put(key, value)的过程:
1. value序列化
2. value写入磁盘数据文件. 要返回写入时候的开始位置即offset
3. 更新内存中的索引信息: key是put已知的,offset是上一步确定的,size由value计算

indexer.put(key, new Index(key, this.name, offset, bytes.length));

更新索引时,要更新indexer这个Map. 每当新添加一个key,value键值对时,就往indexer map中添加一个映射元素.
map元素的key是键值对的键, value能够返回键值对的键在文件中的索引信息.

#### How To GET
get(key)的过程:
1. 根据key从indexer中后去Index对象
2. 根据Index对象的内容读取文件名为filename的磁盘文件, 从offset开始读取,总共读取size长度的字节数组
3. 将字节数组反序列化为自定义的对象. 因为我们put到磁盘文件的是自定义对象序列化后的字节数组

#### DUMP INDEX FILE
数据每次put时都会往indexer中放入一个键值对元素. 序列化时只需要将indexer写入到索引文件中即可

#### LOAD INDEX FILE
什么时候开始加载索引文件? 磁盘文件存在的情况下应该加载出索引文件.

类似于数据库的表数据和索引数据.
数据内容存放在表里, 某一列(假设是主键)的索引信息放在索引文件里.
当你要根据主键查找这一行的数据内容时, 首先要去索引文件中检索出主键对应的数据文件的位置
然后去数据文件中的指定位置开始读取数据.
数据库用于持久化数据. 同样我们实现的数据文件和索引文件也用于持久化的目的.

假设put和get都是一次性的.我们当然不需要将indexer对应的索引文件持久化
因为put时将<key,Index>写入内存的indexer, get时只需要根据key从内存中的indexer中读取Index即可.

#### WHEN TO DUMP INDEX
我们的数据文件追加了数据后, 内存中的indexer会更新数据.
最好索引文件也能及时更新, 当然也可以批量写入, 比如测试例子中在最后进行持久化索引文件.
好比数据库写入数据时,如果建立了索引,则会自动触发索引的更新.

#### WHY INDEX FILE IS A MAP
为什么索引文件不像数据文件一样直接追加? 而是一个Map? Map持久化成索引文件后是无序的.
因为我们要保证key->Index这种映射关系在内存中, 这样能快速根据key找到Index对象.

#### WHAT HAVE U LEARNED
1. File Property for read and write: offset, size
2. Serialize and DeSerialize : u write the object, and u can get that object rightly
3. In-Memory Map from putting key to Index which can get out putting value
4. Index File is need for persistent

