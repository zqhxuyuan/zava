package hdgl.db.store.impl.hdfs.mapreduce;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.*;

public class GraphOutputFormat<K, V> extends FileOutputFormat<K, V> 
{
	
	protected static class LineRecordWriter<K, V>
		extends RecordWriter<K, V> {
		private static final String utf8 = "UTF-8";
		
		protected DataOutputStream out;
		private final byte[] keyValueSeparator;

		public LineRecordWriter(DataOutputStream out, String keyValueSeparator) 
		{
			this.out = out;
			try 
			{
				this.keyValueSeparator = keyValueSeparator.getBytes(utf8);
			} 
			catch (UnsupportedEncodingException uee) 
			{
				throw new IllegalArgumentException("can't find " + utf8 + " encoding");
			}
		}

		private void writeObject(Object o) throws IOException 
		{
			if ((o instanceof GraphWritable) || (o instanceof Edge) || (o instanceof Vertex)) 
			{
				GraphWritable go = (GraphWritable)o;
				go.write(out);
			} 
			else if (o instanceof Text)
			{
				Text to = (Text)o;
		        out.write(to.getBytes(), 0, to.getLength());
			}
			else 
			{
				out.write(o.toString().getBytes(utf8));
			}
		}

		public synchronized void write(K key, V value) throws IOException 
		{
      
			boolean nullKey = key == null || key instanceof NullWritable;
			boolean nullValue = value == null || value instanceof NullWritable;
			if (nullKey && nullValue) 
			{
				return;
			}
			if (!nullKey) 
			{
				writeObject(key);
			}
			if (!(nullKey || nullValue)) 
			{
				out.write(keyValueSeparator);
			}
			if (!nullValue) 
			{
				writeObject(value);
			}
		}

		public synchronized void close(TaskAttemptContext context) throws IOException 
		{
			out.close();
		}
	}

	public RecordWriter<K, V>getRecordWriter(TaskAttemptContext job) 
			throws IOException, InterruptedException 
	{
		Configuration conf = job.getConfiguration();
		boolean isCompressed = getCompressOutput(job);
		String keyValueSeparator= conf.get("mapred.textoutputformat.separator", "\t");
		CompressionCodec codec = null;
		String extension = "";
		if (isCompressed) 
		{
			Class<? extends CompressionCodec> codecClass = 
					getOutputCompressorClass(job, GzipCodec.class);
			codec = (CompressionCodec) ReflectionUtils.newInstance(codecClass, conf);
			extension = codec.getDefaultExtension();
		}
		Path file = getDefaultWorkFile(job, extension);
		FileSystem fs = file.getFileSystem(conf);
		if (!isCompressed) 
		{
			FSDataOutputStream fileOut = fs.create(file, false);
			return new LineRecordWriter<K, V>(fileOut, keyValueSeparator);
		} 
		else 
		{
			FSDataOutputStream fileOut = fs.create(file, false);
			return new LineRecordWriter<K, V>(new DataOutputStream(codec.createOutputStream(fileOut)),
					keyValueSeparator);
		}
	}
}
