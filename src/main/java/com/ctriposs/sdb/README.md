##_DISCLAIMER_
#### This project is still under construction. Beta testers are more than welcome.

# SessDB

A Big, Fast, Persistent Key/Value Store based on a variant of LSM([Log Structured Merge Tree](http://en.wikipedia.org/wiki/Log-structured_merge-tree)), inspired by [Google LevelDB](http://code.google.com/p/leveldb/).

For a more performant in-memory cache **without persistence** support, please refer to [LevelCache](https://github.com/ctriposs/levelcache) which:
>1. uses design and algorithm similar to SessDB; 
2. stores key/vaue data in OffHeap memory instead of Memory Mapped File.

## Feature Highlight:
1. **High Read/Write Performance**: write performance close to O(1) direct memory access, worst average read performance close to O(1) disk acess, tailored for session data scenarios, also suitable for caching data scenarios.
2. **Persistence**: all data is persisted in disk file, no data eviction issue like Memcached, suitable for session data scenarios.
3. **Big**: can store data size bigger than memory.
4. **Efficient Memory Usage**: uses only a small amount of heap memory, leverages a hierarchical storage mechanism, only most recently inserted fresh data resides in heap memory, a big amount of less fresh data resides in memory mapped file, a huge amout of old data resides in disk file; hierarchical sotarge ensures high read/write performance, while heap GC has no big performance impact.
5. **Thread Safe**: supporting multi-threads concurrent and non-blocking access.
6. **Crash Resistance**: all data is durable, process crashes or dies, all data can be quickly restored by just restarting the machine or process.
7. **Compaction**: automatic expired and deleted data cleanup, avoiding disk and memory space waste.
8. **Light in Design & Implementation**: simple Map like interface, only supports Get/Put/Delete operations, cross platform Java based, small codebase size, embeddable.

## Performance Highlight:
Suppose 10 bytes key and 100 bytes value, on normal PC, random read can be **> 500,000** ops/sec, random write can be **> 200,000** ops/sec;
On server grade machine, random read can be **> 2,000,000** ops/sec, random write can be **> 500,000** ops/sec.


## The Architecture
![sessdb architecture](https://raw.githubusercontent.com/ctriposs/sessdb/master/doc/sessdb_arch.png)

## How to Use
* Direct jar or source reference

Download jar from repository mentioned in version history section below, latest stable release is [1.0.0](https://github.com/ctriposs/ctriposs-repo/tree/master/repository/com/ctriposs/sdb/sessdb/1.0.0).
>***Note*** : sessdb depends on slf4j, google guava, snappy compression library, for details, please refer to pom [here](https://github.com/ctriposs/ctriposs-repo/blob/master/repository/com/ctriposs/sdb/sessdb/1.0.0/sessdb-1.0.0.pom).

* Maven dependency

```xml
    <dependency>
      <groupId>com.ctriposs.sdb</groupId>
      <artifactId>sessdb</artifactId>
      <version>1.0.0</version>
    </dependency>
	<repository>
	  <id>github.ctriposs.repo</id>
	  <url>https://raw.githubusercontent.com/ctriposs/ctriposs-repo/master/repository/</url>
	</repository>
```

* Sample Usage

```java

    	// new SessDB with provided DB directory,
    	// this may open exiting DB if already exists
        SDB sdb = new SDB(dbDir, DBConfig.SMALL);
        
        try {
	        // put key/value into the DB
	        sdb.put("helloKey".getBytes(), "helloValue".getBytes());
	        // get value from the DB by key
	        byte[] valueBytes = sdb.get("helloKey".getBytes());
	        
	        System.out.println("value for helloKey is " + new String(valueBytes));
	        
	        // delete key/value from DB by key
	        sdb.delete("helloKey".getBytes());
	        
	        // get non-exiting or already deleted key/value will get null value
	        valueBytes = sdb.get("helloKey".getBytes());
	        if (valueBytes == null) {
	        	System.out.println("helloKey has been deleted");
	        }
	        
	        
	        // put more key/value pairs
	        for(int i = 0; i < 1024; i++) {
	        	sdb.put(("key" + i).getBytes(), ("value" + i).getBytes());
	        }
	        
	        // get more key/value pairs
	        for(int i = 0; i < 1024; i++) {
	        	valueBytes = sdb.get(("key" + i).getBytes());
	        	System.out.println(new String(valueBytes));
	        }
	        
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            // make sure you close the db to shutdown the database
        	// and avoid resource leaks.
            try {
    			sdb.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
```

See sample maven project [here](https://github.com/ctriposs/sessdb/tree/master/sample/hello). 

For simple usage, please refer to source [HelloSessDB.java](https://raw.githubusercontent.com/ctriposs/sessdb/master/sample/hello/src/main/java/com/ctriposs/sdb/sample/HelloSessDB.java).

For advanced usage involving serialization, multi-threading, huge amount of key/value pairs(>1,000,000), please refer to source [SDBPerfTest.java](https://raw.githubusercontent.com/ctriposs/sessdb/master/sample/hello/src/test/java/com/ctriposs/sdb/sample/SDBPerfTest.java).


## Docs
1. [(Chinese)高性能Key/Value存储引擎SessionDB](https://github.com/ctriposs/sessdb/raw/master/doc/SessionDB.docx)


## Version History
#### 1.0.0 — *May 25, 2014* : [repository](https://github.com/ctriposs/ctriposs-repo/tree/master/repository/com/ctriposs/sdb/sessdb/1.0.0)

  * Initial version:)

##Copyright and License
Copyright 2014 ctriposs

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

 