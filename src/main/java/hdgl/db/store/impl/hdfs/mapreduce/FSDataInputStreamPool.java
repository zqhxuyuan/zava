package hdgl.db.store.impl.hdfs.mapreduce;

import java.io.IOException;

import hdgl.db.conf.GraphConf;
import hdgl.util.StringHelper;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class FSDataInputStreamPool {
	static FSDataInputStream[] vsp_f = new FSDataInputStream[Parameter.REDUCER_NUMBER];
	static FSDataInputStream[] vsp_v = new FSDataInputStream[Parameter.REDUCER_NUMBER];
	static FSDataInputStream[] esp_f = new FSDataInputStream[Parameter.REDUCER_NUMBER];
	static FSDataInputStream[] esp_v = new FSDataInputStream[Parameter.REDUCER_NUMBER];
		
	public static void close()
	{
		try
		{
			for (int i = 0; i < Parameter.REDUCER_NUMBER; i++)
			{
				vsp_v[i].close();
				vsp_f[i].close();
				esp_v[i].close();
				esp_f[i].close();
			}
		}
		catch (Exception e)
		{
			
		}
	}
	
	public static FSDataInputStream getVsp_f(FileSystem hdfs, Configuration conf, int id) throws IOException
	{
		if (vsp_f[id] == null)
		{
			Path path = new Path(GraphConf.getPersistentGraphRoot(conf) + "/" + Parameter.VERTEX_REGULAR_FILE_NAME + "-r-" + StringHelper.fillToLength(id));
			vsp_f[id] = hdfs.open(path);
		}
		return vsp_f[id];
	}
	
	public static FSDataInputStream getVsp_v(FileSystem hdfs, Configuration conf, int id) throws IOException
	{
		if (vsp_v[id] == null)
		{
			Path path = new Path(GraphConf.getPersistentGraphRoot(conf) + "/" + Parameter.VERTEX_IRREGULAR_FILE_NAME + "-r-" + StringHelper.fillToLength(id));
			vsp_v[id] = hdfs.open(path);
		}
		return vsp_v[id];
	}

	public static FSDataInputStream getEsp_f(FileSystem hdfs, Configuration conf, int id) throws IOException
	{
		if (esp_f[id] == null)
		{
			Path path = new Path(GraphConf.getPersistentGraphRoot(conf) + "/" + Parameter.EDGE_REGULAR_FILE_NAME + "-r-" + StringHelper.fillToLength(id));
			esp_f[id] = hdfs.open(path);
		}
		return esp_f[id];
	}
	
	public static FSDataInputStream getEsp_v(FileSystem hdfs, Configuration conf, int id) throws IOException
	{
		if (esp_v[id] == null)
		{
			Path path = new Path(GraphConf.getPersistentGraphRoot(conf) + "/" + Parameter.EDGE_IRREGULAR_FILE_NAME + "-r-" + StringHelper.fillToLength(id));
			esp_v[id] = hdfs.open(path);
		}
		return esp_v[id];
	}
}
