package hdgl.db.store.impl.hdfs.mapreduce;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;

public class JumpInputStream extends InputStream {

	private static final int BUFFERSIZE = 64;
	
	byte[] buffer = new byte[BUFFERSIZE];
	int buflen = 0;
	int bufreaded = 0;
	boolean jumped = false;
	FSDataInputStream in1;
	FSDataInputStream in2;
	int jumplen;
	FileStatus[] jumpfiles;
	FileSystem fs;
	
	public JumpInputStream(FileStatus[] in1, FileStatus[] in2, long seekpos, int jumplen, FileSystem fs) throws IOException {
		int currentFile = 0;
		FileStatus f = in1[currentFile++];
		while(f.getLen() <= seekpos){
			if(currentFile >= jumpfiles.length){
				throw new EOFException("end of the pseudo file");
			}
			seekpos -= f.getLen();
			f = in1[currentFile++];
		}
		this.in1 = fs.open(f.getPath());
		this.in1.seek(seekpos);
		this.jumplen = jumplen - 8;
		this.jumpfiles = in2;
		this.fs= fs;
	}

	boolean loadbuffer() throws IOException{
		if(bufreaded == BUFFERSIZE){
			buflen = 0;
			bufreaded = 0;
		}
		if(!jumped){
			if(jumplen==0){
				long offset = in1.readLong();
				int currentFile = 0;
				FileStatus f = jumpfiles[currentFile++];
				while(f.getLen() <= offset){
					if(currentFile>=jumpfiles.length){
						throw new EOFException("end of the pseudo file");
					}
					offset -= f.getLen();
					f = jumpfiles[currentFile++];
				}
				in2 = fs.open(f.getPath());
				in2.seek(offset);
				int len = BUFFERSIZE - buflen;
				int readed = in2.read(buffer, buflen, len);
				if(readed==0) return false;
				buflen += readed;
			}else{
				int len = BUFFERSIZE - buflen;
				int readed = in1.read(buffer, buflen, jumplen>len?jumplen:len);
				if(readed==0) return false;
				jumplen -= readed;
				buflen += readed;
			}
		}else{
			int len = BUFFERSIZE - buflen;
			int readed = in2.read(buffer, buflen, len);
			if(readed==0) return false;
			buflen += readed;
		}
		return true;
	}
	
	@Override
	public int read() throws IOException {
		if(buflen <= bufreaded){
			loadbuffer();
		}
		return buffer[bufreaded++];
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	@Override
	public int read(byte[] b, int offset, int length) throws IOException {
		int count=0;
		while(length>0){
			if(buflen <= bufreaded){
				loadbuffer();
			}
			int len = buflen - bufreaded;
			len = len>length?length:len;
			System.arraycopy(buffer, bufreaded, b, offset, len);
			length -=len;
			offset+=len;
			count+=len;
		}
		return count;
	}
	
	@Override
	public void close() throws IOException {
		try{
			in1.close();
		}finally{
			if(in2!=null){
				in2.close();
			}
		}
	}

}
