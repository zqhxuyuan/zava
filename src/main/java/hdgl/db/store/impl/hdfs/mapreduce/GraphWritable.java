package hdgl.db.store.impl.hdfs.mapreduce;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.hadoop.io.WritableComparable;


public class GraphWritable implements WritableComparable<GraphWritable>{
	protected int id;
	protected ArrayList<Label> labels;
	protected ByteBuffer bb = ByteBuffer.allocate(1048576);
	protected int count = 0;
	protected boolean needIrr = false;
	protected boolean isIrr = false;
	protected long offset = -1;
	private int REGULAR_BLOCK_SIZE = 0;
	
	public GraphWritable(int id, int blockSize)
	{
		REGULAR_BLOCK_SIZE = blockSize;
		this.id = id;
		labels = new ArrayList<Label>();
	}
	
	public boolean getNeedIrr()
	{
		return needIrr;
	}
	
	public long prepareData(long offset)
	{
		long ret = 0;
		
		this.offset = offset;
		if (count > REGULAR_BLOCK_SIZE - Parameter.OFFSET_MAX_LEN) 
		{
			ret = count - REGULAR_BLOCK_SIZE + Parameter.OFFSET_MAX_LEN;
			needIrr = true;
			ret = ret + 4;
		}
		return ret;
	}
	
	public void setIrr(boolean flag)
	{
		isIrr = flag;
	}
	
	@Override
	public void readFields(DataInput input) throws IOException 
	{
	}

	@Override
	public void write(DataOutput output) throws IOException 
	{
		if (isIrr)
		{
			byte[] dst = new byte[count - REGULAR_BLOCK_SIZE + Parameter.OFFSET_MAX_LEN];
			bb.position(REGULAR_BLOCK_SIZE - Parameter.OFFSET_MAX_LEN);
			bb.get(dst, 0, count - REGULAR_BLOCK_SIZE + Parameter.OFFSET_MAX_LEN);
			output.writeInt(dst.length);
			output.write(dst);
		}
		else
		{
			int temp;
			byte[] dst;
			if (REGULAR_BLOCK_SIZE - Parameter.OFFSET_MAX_LEN > count) temp = count + Parameter.OFFSET_MAX_LEN;
			else temp = REGULAR_BLOCK_SIZE;
			dst = new byte[REGULAR_BLOCK_SIZE - Parameter.OFFSET_MAX_LEN];
			bb.position(0);
			bb.get(dst, 0, temp - Parameter.OFFSET_MAX_LEN);
			output.write(dst);
			output.writeLong(offset);
		}
	}

	@Override
	public int compareTo(GraphWritable o) 
	{
		return id - o.id;
	}
}
