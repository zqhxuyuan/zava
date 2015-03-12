package hdgl.db.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Writable;

public class RegionMapWritable extends HashMap<Integer, InetSocketAddress> implements Writable {

	private static final long serialVersionUID = 7648903434154487286L;

	public RegionMapWritable(){
		
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		int len=in.readInt();
		InetSocketAddressWritable addr=new InetSocketAddressWritable();
		clear();
		for(int i=0;i<len;i++){
			int id = in.readInt();
			addr.readFields(in);
			put(id, addr.toAddress());
		}
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(size());
		InetSocketAddressWritable addr=new InetSocketAddressWritable();
		for(Map.Entry<Integer,InetSocketAddress> item:entrySet()){
			out.writeInt(item.getKey());
			addr.setHost(item.getValue().getHostName());
			addr.setPort(item.getValue().getPort());
			addr.write(out);
		}
	}
	
	
	
}
