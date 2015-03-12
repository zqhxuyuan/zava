package hdgl.db.store.impl.file;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.fs.FSDataInputStream;

public class JumpInputStream extends InputStream {

	boolean jumped = false;
	FSDataInputStream in2;
	long offset;
	
	FSDataInputStream in1;
	long jumpoffset;
	
	public JumpInputStream(FSDataInputStream in1, FSDataInputStream in2,
			long offset, int jumplen) throws IOException {
		super();
		this.in1 = in1;
		this.in2 = in2;
		in1.seek(offset);
		jumpoffset=offset+jumplen-8;
	}

	@Override
	public int read() throws IOException {
		if(!jumped){
			if(in1.getPos()<jumpoffset){
				return in1.read();
			}else{
				jumped = true;
				long pos = in1.readLong();
				in2.seek(pos);
				return in2.read();
			}
		}else{
			return in2.read();
		}
	}

//	@Override
//	public int read(byte[] buf, int offset, int len) throws IOException {
//		if(!jumped){
//			long pos=in1.getPos();
//			if(pos<jumpoffset-buf.length){
//				return in1.read(buf,offset,len);
//			}else{
//				int l1 = in1.read(buf, offset, (int)(jumpoffset-pos + 1));
//				in2.seek(in1.readLong());
//				return l1 + in2.read(buf, offset+l1, len-l1);
//			}
//		}else{
//			return in2.read(buf, offset, len);
//		}
//	}
	
	@Override
	public void close() throws IOException {
		try{
			in1.close();
		}finally{
			in2.close();
		}
	}
}
