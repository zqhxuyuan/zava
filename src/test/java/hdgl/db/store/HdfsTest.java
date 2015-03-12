package hdgl.db.store;

import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

public class HdfsTest {

	@Test
	public void test() throws Exception {
		try{
			FileSystem fs=FileSystem.get(new URI("hdfs://localhost:9000/"),new Configuration());
			FSDataOutputStream out=fs.create(new Path("test"));
			out.writeUTF("test");
			out.close();
			fs.close();
		}catch(Throwable ex){
			while(ex!=null){
				ex.printStackTrace();
				ex =ex.getCause();
			}
		}
	}

}
