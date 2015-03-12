package hdgl.db.store.impl.hdfs.mapreduce;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;

public class GraphInputStream extends InputStream{
	protected FSDataInputStream inputStream = null;
	protected static FileSystem hdfs = null;
	protected long id;
	protected int position = 0;
	protected int limit;
	protected int fileIrr = 0;
	private int flag = 0;
	protected boolean hasNext = true;
	protected int REGULAR_BLOCK_SIZE;
	protected Configuration conf;
	
	public GraphInputStream(long id, Configuration conf, int flag) throws IOException
	{
		this.id = id;
		this.flag = flag;
		this.conf = conf;
		if (hdfs == null) hdfs = FileSystem.get(conf);
	}
	
	public void close() throws IOException
	{
		//inputStream.close();
	}

	@Override
	public int read() throws IOException 
	{
		int ret;
		if (position >= limit)
		{
			changeFile();
			ret = inputStream.read();
		}
		else
		{
			ret = inputStream.read();
		}
		position++;
		return ret;
	}
	
	private boolean changeFile() throws IOException
	{
		if (!hasNext) return false; 
		long offset = inputStream.readLong();
		if (flag == 0) inputStream = FSDataInputStreamPool.getVsp_v(hdfs, conf, fileIrr);
		else inputStream = FSDataInputStreamPool.getEsp_v(hdfs, conf, fileIrr);
		inputStream.seek(offset);
		position = 0;
		limit = inputStream.readInt();
		hasNext = false;
		return true;
	}

	public int read(byte[] b, int off, int len) throws IOException
	{
		if (len == 0) return 0;
		if (off + len > b.length) return 0;
		if (limit - position >= len)
		{
			inputStream.read(b, off, len);
			position = position + len;
			return len;
		}
		else
		{
			inputStream.read(b, off, limit - position);
			int left = len - (limit - position);
			position = limit;
			if (!changeFile()) return (len - left);
			return (len - left + read(b, off + len - left, left));
		}
	}
	
	public int read(byte[] b) throws IOException
	{
		if (b.length == 0) return 0;
		return read(b, 0, b.length);
	}

	public int readInt() throws IOException
	{
		int[] bs = new int[4];
		int ret = 0;
		if (limit - position >= 4)
		{
			position = position + 4;
			return inputStream.readInt();
		}
		for (int i = 0; i < 4; i++)
		{
			bs[i] = read();
			if (bs[i] == -1)
			{
				bs[i] = 0;
			}
		}
		if (bs[0] >= 128)
		{
			long tmp = (((long)bs[0]) << 24) + (((long)bs[1]) << 16) + (((long)bs[2]) << 8) + (long)bs[3];
			ret = (int) (tmp - (((long)1) << 32));
		}
		else
		{
			ret = (bs[0] << 24) + (bs[1] << 16) + (bs[2] << 8) + bs[3];
		}
		return ret;
	}
}
