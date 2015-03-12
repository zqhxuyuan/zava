package hdgl.db.store.impl.hdfs.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import hdgl.db.conf.GraphConf;
import hdgl.db.graph.Entity;
import hdgl.db.graph.HGraphIds;
import hdgl.db.task.AsyncResult;
import hdgl.db.task.CallableAsyncResult;
import hdgl.util.StringHelper;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Callable;

public class MutableGraph implements hdgl.db.graph.MutableGraph {
	
	private OutputStream outputStream;
	private FileSystem hdfs;
	private long vertex = 0;
	private long edge = 0;
	Configuration configuration;
	int sessionId;
	
	public MutableGraph(Configuration conf, int sessionId)
	{
		configuration = conf;
		this.sessionId = sessionId;
		try
		{
			hdfs = FileSystem.get(conf);
			Path dfs = new Path(GraphConf.getGraphSessionRoot(conf, sessionId),"log/1");
			outputStream = new BufferedOutputStream(hdfs.create(dfs, true), 4096);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getVertexNum()
	{
		return (int) vertex;
	}
	
	public int getEdgeNum()
	{
		return (int) edge;
	}
	
	public void close() 
	{
		try 
		{
			outputStream.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}finally{
			try {
				hdfs.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	private long createVertex()
	{
		vertex--;
		StringBuffer line = new StringBuffer("[add vertex ");
		line.append(vertex);
		line.append(":]\n");
		byte[] buff = line.toString().getBytes();
		try
		{
			outputStream.write(buff, 0, buff.length);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return vertex;
	}
	private long createEdge(long vertex1, long vertex2)
	{
		edge++;
		StringBuffer line = new StringBuffer("[add edge ");
		line.append(edge);
		line.append(":");
		line.append(vertex1 + " - " + vertex2);
		line.append("]\n");
		byte[] buff = line.toString().getBytes();
		try
		{
			outputStream.write(buff, 0, buff.length);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return edge;
	}
	private void setVertexLabel(long vertex, String name, byte[] value_b)
	{
		String value = StringHelper.bytesToString(value_b);
		StringBuffer line = new StringBuffer("[add label vertex ");
		line.append(vertex);
		line.append(":" + name + " = ");
		line.append(value);
		line.append("]\n");
		byte[] buff = line.toString().getBytes();
		try
		{
			outputStream.write(buff, 0, buff.length);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	private void setEdgeLabel(long edge, String name, byte[] value_b)
	{
		String value = StringHelper.bytesToString(value_b);
		StringBuffer line = new StringBuffer("[add label edge ");
		line.append(edge);
		line.append(":" + name + " = ");
		line.append(value);
		line.append("]\n");
		byte[] buff = line.toString().getBytes();
		try
		{
			outputStream.write(buff, 0, buff.length);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public AsyncResult<Boolean> commit() {
		close();
		return new CallableAsyncResult<Boolean>(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				PersistentGraph persistentGraph 
					= new PersistentGraph(configuration, sessionId, getVertexNum(), getEdgeNum());
				return persistentGraph.runMapReduce();
			}
		});
	}
	@Override
	public AsyncResult<Boolean> abort() {
		return null;
	}
	
	@Override
	public long createVertex(String type) {
		long id = createVertex();
		setVertexLabel(id, "type", type.getBytes());
		return id;
	}
	@Override
	public long createEdge(String type, long start, long end) {
		long id=createEdge(start, end);
		setEdgeLabel(id, "type", type.getBytes());
		return id;
	}
	@Override
	public void setLabel(long entity, String name, byte[] value) {
		if(HGraphIds.isEdgeId(entity)){
			setEdgeLabel(entity, name, value);
		}else{
			setVertexLabel(entity, name, value);
		}		
	}
	@Override
	public void deleteEntity(Entity e) {
		
	}
	@Override
	public void deleteLabel(Entity e, String name) {
		
	}
}
