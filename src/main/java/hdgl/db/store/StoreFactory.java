package hdgl.db.store;

import java.io.IOException;

import hdgl.db.store.impl.cache.MemoryCacheGraphStore;
import hdgl.db.store.impl.hdfs.HdfsGraphStore;
import hdgl.db.store.impl.hdfs.HdfsLogStore;
import org.apache.hadoop.conf.Configuration;

public class StoreFactory {
	
	static byte[] data(Object obj){
		byte[] data=new byte[4];
		int code=obj.hashCode();
		data[0]=(byte) (code>>>24&0xff);
		data[1]=(byte) (code>>>16&0xff);
		data[2]=(byte) (code>>>8&0xff);
		data[3]=(byte) (code&0xff);
		return data;
	}
	
	public static GraphStore createGraphStore(Configuration conf) throws IOException{
		return new HdfsGraphStore(conf);
//		try{
//			return new MemoryGraphStoreTest().test();
//		}catch (Exception e) {
//			throw new RuntimeException(e);
//		}
	}
	
	public static IndexGraphStore createIndexGraphStore(Configuration conf) throws IOException{
		return new MemoryCacheGraphStore(conf);
	}
	
	public static LogStore createLogStore(Configuration conf, int sessionId) throws IOException{
		return new HdfsLogStore(conf, sessionId);
	}
}
