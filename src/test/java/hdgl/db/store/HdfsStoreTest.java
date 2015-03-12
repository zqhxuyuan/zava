package hdgl.db.store;

import java.io.IOException;
import hdgl.db.conf.GraphConf;
import hdgl.db.store.impl.hdfs.mapreduce.Vertex;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.junit.BeforeClass;
import org.junit.Test;

public class HdfsStoreTest {

	static GraphStore g;
	static FileSystem hdfs;
	static FSDataInputStream fs, fs1;
	static String str;
	static int size;
	@BeforeClass
	public static void beforeClass() throws IOException{
		Configuration conf = GraphConf.getDefault();

		g = StoreFactory.createGraphStore(conf);
		hdfs = FileSystem.get(conf);
		str = GraphConf.getPersistentGraphRoot(conf);
		size = GraphConf.getVertexTrunkSize(conf);
	}
	
	@Test
	public void UseExampleTest() throws IOException {
		/*for(int i=0;i<1000;i++){
			g.parseVertex(1);
		}
		assertEquals(5, g.getVertexCount());
		assertEquals(9, g.getEdgeCount());
		assertEquals("person", g.parseVertex(1).getType());
		assertEquals("back", g.parseEdge(-5).getType());
		assertEquals("forward", g.parseEdge(-1).getType());
		assertEquals("jump", g.parseEdge(-8).getType());
		assertEquals(-4, ByteArrayHelper.parseInt(g.parseEdge(-5).getLabel("len")));*/
		/*for (int i = 1; i <= 100000; i++)
		{
			HVertex hv;
			hv = (HVertex) g.parseVertex(i);
			System.out.println(i + " vs " + hv.getId());
			//assertEquals(i, hv.getId());
			System.out.println("type : " + hv.getType());
			//assertEquals("v", hv.getType());
			if (i % 1000 == 0) System.out.println(".");
		}
		FSDataInputStreamPool.close();*/
		/*Path path = new Path(str + "/" + Parameter.VERTEX_REGULAR_FILE_NAME + "-r-" + StringHelper.fillToLength(0));
		fs = hdfs.open(path);
		
		long len = hdfs.getFileStatus(path).getLen() / size;
		System.out.println(len);
		fs = hdfs.open(path);
		fs1 = hdfs.open(new Path(str + "/" + Parameter.EDGE_REGULAR_FILE_NAME + "-r-" + StringHelper.fillToLength(0)));
		byte[] b = new byte[10];
		for (int i = 0; i < len; i++)
		{
			
			fs.seek(i * size);
			fs1.seek(i*size);
			for (int j = 0; j < 20; j++)
			{
				fs.readInt();
				fs1.readInt();
			}
			fs.read(b);
			fs1.read(b);
			//fs.close();
			if (i % 1000 == 0) 
			{
				fs.close();
				System.out.println(".");
			}
		}
		fs.close();*/
		for(long id=1;id<=g.getVertexCount();id++){
			hdgl.db.graph.Vertex vertex = g.parseVertex(id);
		}
		for(long id=1;id<=g.getEdgeCount();id++){
			g.parseEdge(-id);
		}
	}	

}
