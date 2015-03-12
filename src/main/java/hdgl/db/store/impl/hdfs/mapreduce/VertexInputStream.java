package hdgl.db.store.impl.hdfs.mapreduce;

import hdgl.db.conf.GraphConf;
import hdgl.util.StringHelper;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

public class VertexInputStream extends GraphInputStream {	
	private static long fileLength[] = null;
	
	public VertexInputStream(long id, Configuration conf) throws IOException
	{
		super(id - 1, conf, 0);
		
		REGULAR_BLOCK_SIZE = GraphConf.getVertexTrunkSize(conf);
		limit = REGULAR_BLOCK_SIZE - Parameter.OFFSET_MAX_LEN;
		
		getFileLength(GraphConf.getPersistentGraphRoot(conf) + "/" + Parameter.VERTEX_REGULAR_FILE_NAME);
		int ret = locate(GraphConf.getPersistentGraphRoot(conf) + "/" + Parameter.VERTEX_REGULAR_FILE_NAME);
		fileIrr = ret;
	}
	
	public static void getFileLength(String file) throws IOException
	{
		if (fileLength != null) return;
		fileLength = new long[Parameter.REDUCER_NUMBER];
		Path path = null;
		for (int i = 0; i < Parameter.REDUCER_NUMBER; i++)
		{
			path = new Path(file + "-r-" + StringHelper.fillToLength(i));
			fileLength[i] = hdfs.getFileStatus(path).getLen();
		}
	}
	
	public int locate(String file) throws IOException
	{
		long count = 0;
		int ret = 0;
		for (int i = 0; i < Parameter.REDUCER_NUMBER; i++)
		{
			long temp = fileLength[i] / REGULAR_BLOCK_SIZE;
			if (count + temp > id)
			{
				ret = i;
				break;
			}
			count = count + temp;
		}
		inputStream = FSDataInputStreamPool.getVsp_f(hdfs, conf, ret);
		//inputStream.seek((id - count + 1)*REGULAR_BLOCK_SIZE - 8);
		//assert offset>0;
		//offset = inputStream.readLong();
		//System.out.println("id " + (id+1) + " : offset " + offset);
		id = id - count;
		inputStream.seek(id*REGULAR_BLOCK_SIZE);
		return ret;
	}
}
