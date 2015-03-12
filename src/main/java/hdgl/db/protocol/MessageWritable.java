package hdgl.db.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Writable;

public class MessageWritable implements Writable{

	ArrayList<Integer> stateIds = new ArrayList<Integer>();
	ArrayList<long[]> paths = new ArrayList<long[]>();
	
	synchronized public void add(int stateId, long[] path){
		stateIds.add(stateId);
		paths.add(path);
	}
	
	synchronized public void addAll(MessageWritable other){
		stateIds.addAll(other.stateIds);
		paths.addAll(other.paths);
	}
	
	public int size(){
		return stateIds.size();
	}
	
	public int getState(int index){
		return stateIds.get(index);
	}
	
	public long[] getPath(int index){
		return paths.get(index);
	}

	@Override
	synchronized public void readFields(DataInput in) throws IOException {
		stateIds.clear();
		paths.clear();
		int len = in.readInt();
		for(int i=0;i<len;i++){
			int stateId=in.readInt();
			int pathlen = in.readInt();
			long[] path=new long[pathlen];
			for(int j=0;j<pathlen;j++){
				path[j]=in.readLong();
			}
			stateIds.add(stateId);
			paths.add(path);
		}
	}

	@Override
	synchronized public void write(DataOutput out) throws IOException {
		int len = stateIds.size();
		out.writeInt(len);
		for(int i=0; i<len; i++){
			out.writeInt(stateIds.get(i));
			long[] path=paths.get(i);
			out.writeInt(path.length);
			for(long p:path){
				out.writeLong(p);
			}
		}		
	}	
}
