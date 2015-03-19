https://github.com/seanlinwang/tkv

## Introduction
Tkv is a key-value store written in java, it applies to embedded scene.

## Advantage
- No in memory value cache.
- Every record associated with multi tags, so each tag associated with a record list order by created time.
- support hdfs or local filesystem.

## Weakness
- Read and append only, no update and delete.

## Architecture
![architect](http://ww2.sinaimg.cn/large/648d6e26gw1dqawvzkia7j.jpg "tkv architect")


## Exmaple
```java		
	File dbFile = new File("/tmp/tkvtest.db"); 
	Tkv tkv = new TkvImpl(dbFile); 
	String key = "01234567"; 
	String value = "ayellowdog"; 
	//put key-value with tags
	tkv.put(key, value.getBytes(), "pet", "dog");
	// get value by key
	tkv.get(key);
	// get record by tag and key
	Record r  = tkv.getRecord("pet", key);
	// navigate in the same tag
	Record next = tkv.getRecord("pet", r.getNextKey());
	Record privious = tkv.getRecord("pet", r.getPriviousKey());

	tkv.close();
```

## Feedback
[http://weibo.com/seanlinwang](weibo.com/seanlinwang)  xailnx@gmail.com
