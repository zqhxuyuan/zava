package hdgl.db.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Writable;

public class MessagePackWritable implements Writable{

	ArrayList<Long> receivers = new ArrayList<Long>();
	ArrayList<MessageWritable> msgs = new ArrayList<MessageWritable>();
	
	synchronized public void add(long receiver, MessageWritable msg){
		receivers.add(receiver);
		msgs.add(msg);
	}

	@Override
	synchronized public void readFields(DataInput in) throws IOException {
		receivers.clear();
		msgs.clear();
		int len = in.readInt();
		for(int i=0;i<len;i++){
			long receiver=in.readLong();
			MessageWritable msg=new MessageWritable();
			msg.readFields(in);
			receivers.add(receiver);
			msgs.add(msg);
		}
	}

	@Override
	synchronized public void write(DataOutput out) throws IOException {
		int len = receivers.size();
		out.writeInt(len);
		for(int i=0; i<len; i++){
			out.writeLong(receivers.get(i));
			msgs.get(i).write(out);
		}		
	}	
	
	public int size(){
		return receivers.size();
	}
	
	public long getReceiver(int index){
		return receivers.get(index);
	}
	
	public MessageWritable getMessage(int index){
		return msgs.get(index);
	}
}
