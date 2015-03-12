package hdgl.db.protocol;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.io.Writable;

public class ResultPackWritable implements Writable{

	long[][] result;
	
	boolean hasMore;

	public ResultPackWritable(long[][] result, boolean hasMore) {
		super();
		this.result = result;
		this.hasMore = hasMore;
	}

	public ResultPackWritable() {
		super();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeBoolean(hasMore);
		out.writeInt(result.length);
		for(long[] path:result){
			out.writeInt(path.length);
			for(long id:path){
				out.writeLong(id);
			}
		}
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		hasMore = in.readBoolean();
		int len = in.readInt();
		result = new long[len][];
		for(int i=0;i<len;i++){
			int l = in.readInt();
			long[] path =new long[l];
			for(int j=0;j<l;j++){
				path[j] = in.readLong();
			}
			result[i] = path;
		}
	}

	public long[][] getResult() {
		return result;
	}

	public void setResult(long[][] result) {
		this.result = result;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}
	
	@Override
	public String toString() {
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<result.length;i++){
			if(buf.length()>0){
				buf.append(",\n  ");
			}else{
				buf.append(result.length+" paths [\n  ");
			}
			buf.append(Arrays.toString(result[i]));
		}
		if(buf.length()>0){
			buf.append("\n]\n");
		}else{
			return "empty\n";
		}
		return buf.toString();
	}
	
	
}
