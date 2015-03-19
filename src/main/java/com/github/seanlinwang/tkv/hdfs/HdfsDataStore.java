/**
 * 
 */
package com.github.seanlinwang.tkv.hdfs;

import java.io.IOException;

import com.github.seanlinwang.tkv.DataStore;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * @author sean.wang
 * @since Mar 7, 2012
 */
public class HdfsDataStore implements DataStore {
	private long length = 0;

	private Path path;

	private FileSystem fs;

	private FSDataOutputStream output;

	private FSDataInputStream input;

	public HdfsDataStore() {

	}

	public HdfsDataStore(FileSystem fs, String hdfsFilename) throws IOException {
		this.path = new Path(fs.getWorkingDirectory(), hdfsFilename);
		this.fs = fs;
	}

	@Override
	public void append(byte b) throws IOException {
		synchronized (this.output) {
			this.output.write(b);
			this.length++;
		}
	}

	@Override
	public void append(byte[] bytes) throws IOException {
		synchronized (this.output) {
			this.output.write(bytes);
			this.length += bytes.length;
		}
	}

    //不支持随机写,只支持顺序写
	@Override
	public void append(long offset, byte[] bytes) throws IOException {
		throw new UnsupportedOperationException("Hdfs unsupport random write!");
	}

	@Override
	public void close() throws IOException {
		this.closeOutput();
		this.closeInput();
		this.closeFlieSystem();
	}

	private void closeFlieSystem() throws IOException {
		if (this.fs != null) {
			this.fs.close();
			this.fs = null;
		}
	}

	public void openOutput() throws IOException {
		if (this.output == null) {
			this.output = this.fs.create(this.path);
		}
	}

	public void flushAndCloseOutput() throws IOException {
		if (this.output != null) {
			this.output.flush();
			this.closeOutput();
		}
	}

	public void closeOutput() throws IOException {
		if (this.output != null) {
			this.output.close();
			this.output = null;
		}
	}

	public void openInput() throws IOException {
		if (this.input == null) {
			this.input = this.fs.open(this.path, 1024);
		}
	}

	public void closeInput() throws IOException {
		if (this.input != null) {
			this.input.close();
			this.input = null;
		}
	}

    //随机读是支持的
	@Override
	public byte[] get(long offset, int length) throws IOException {
		FSDataInputStream in = this.input;
		if (in == null) {
			throw new IllegalStateException("input can't null");
		}
		byte[] bytes = new byte[length];
		synchronized (in) {
			in.seek(offset);
			int actual = in.read(bytes);
			if (actual != length) {
				throw new IOException(String.format("readed bytes expect %s actual %s", length, actual));
			}
		}
		return bytes;
	}

	@Override
	public long length() throws IOException {
		return this.length;
	}

	@Override
	public boolean delete() throws IOException {
		boolean localDeleted = this.deleteLocal();
		boolean remoteDeleted = this.deleteRemote();
		return localDeleted && remoteDeleted;
	}

	public boolean deleteRemote() throws IOException {
		boolean remoteDeleted = false;
		if (this.fs != null) {
			remoteDeleted = this.fs.delete(path, false);
		}
		return remoteDeleted;
	}

	public boolean deleteLocal() {
		return true;
	}

}
