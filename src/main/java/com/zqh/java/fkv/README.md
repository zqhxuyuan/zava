https://github.com/seanlinwang/fkv

# Introduction
Fkv is a fast key-value store written in java, it applies to embedded scene.
It is suitable for the scene where record quantity not huge but has a very frequently reading and writing  requirement.

## Advantage
- Realtime storage, based in java.nio.MappedByteBuffer.
- Quick write/read speed, 100,0000+ put/s, 2000,0000+ get/s in my macbookpro(i7,8G,HDD) with 8 byte size key and 10 byte size value.
  (see https://github.com/seanlinwang/fkv/blob/master/src/test/java/com/dianping/fkv/FkvPerfTest.java)
- Small db size, because of fixed key/value length, new input record will reuse the deleted space first, and then append in the end if no deleted space

## Weakness
- Fixed record size, key length and value length when create new Fkv database.
- Large memory usage because of total value cache

## Architecture
![architect](http://ww1.sinaimg.cn/mw600/648d6e26gw1do4szstuhaj.jpg "fkv architect")

## Exmaple
```java		
	File dbFile = new File("/tmp/fkvtest.db"); 
	// load old database or create new one(if file not exists) which can store 10000 records
	Fkv fkv = new FkvImpl(dbFile, 10000, 8, 10); 
	// key must be 8 byte size
	String key = "01234567"; 
	// value must be 10 byte size
	String value = "0123456789"; 
	fkv.put(key, value);
	fkv.delete(key);
	fkv.get(key);
	fkv.close();
```

## Feedback
[http://weibo.com/seanlinwang](weibo.com/seanlinwang)  xailnx@gmail.com
