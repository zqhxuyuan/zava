/**
 * 
 */
package com.github.seanlinwang.tkv.hdfs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Comparator;

import com.github.seanlinwang.tkv.local.RAFIndexStore;
import com.github.seanlinwang.tkv.util.IoKit;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import com.github.seanlinwang.tkv.IndexStore;
import com.github.seanlinwang.tkv.Meta;

/**
 * @author sean.wang
 * @since Mar 7, 2012
 */
public class HdfsIndexStore implements IndexStore {
	private RAFIndexStore localIndexStore;

	private FileSystem fs;

	private Path path;

	public HdfsIndexStore(FileSystem fs, String hdfsFilename, File localFile, int keyLength, int tagLength) throws IOException {
		this.fs = fs;
        //本地磁盘索引文件的存储方式是RAFIndexStore.
		this.localIndexStore = new RAFIndexStore(localFile, keyLength, tagLength);
        //还要在HDFS中存储一份.注意本地和HDFS的文件名是一样的,因为localFile的名称和这里的hdfsFilename一样
		this.path = new Path(fs.getWorkingDirectory(), hdfsFilename);
	}

	@Override
	public void append(Meta meta) throws IOException {
		this.localIndexStore.append(meta);
	}

	@Override
	public void close() throws IOException {
		this.localIndexStore.close();
		this.fs.close();
	}

	@Override
	public boolean delete() throws IOException {
		boolean localDeleted = this.deleteLocal();
		boolean remoteDeleted = this.deleteRemote();
		return localDeleted && remoteDeleted;
	}

	public boolean deleteLocal() throws IOException {
		return this.localIndexStore.delete();
	}

	public boolean deleteRemote() throws IOException {
		boolean remoteDeleted = false;
		if (this.fs != null) {
			remoteDeleted = this.fs.delete(path, false);
		}
		return remoteDeleted;
	}

	public void download() throws IOException {
		InputStream input = fs.open(path);
		OutputStream output = this.localIndexStore.getOutputStream();
		IoKit.copyAndClose(input, output);
	}

	@Override
	public void flush() throws IOException {
		this.localIndexStore.flush();
        //本地磁盘文件
		InputStream input = this.localIndexStore.getInputStream();
        //HDFS文件
		OutputStream output = fs.create(path);
        //拷贝本地文件到HDFS上
		IoKit.copyAndClose(input, output);
	}

	@Override
	public Meta getIndex(long indexPos) throws IOException {
		return this.localIndexStore.getIndex(indexPos);
	}

	@Override
	public Meta getIndex(String key) throws IOException {
		return this.localIndexStore.getIndex(key);
	}

	@Override
	public Meta getIndex(String key, Comparator<byte[]> keyComp) throws IOException {
		return this.localIndexStore.getIndex(key, keyComp);
	}

	@Override
	public Meta getIndex(String key, String tagName) throws IOException {
		return this.localIndexStore.getIndex(key, tagName);
	}

	@Override
	public Meta getIndex(String key, String tagName, Comparator<byte[]> keyComp) throws IOException {
		return this.localIndexStore.getIndex(key, tagName, keyComp);
	}

	@Override
	public int getIndexLength() {
		return this.localIndexStore.getIndexLength();
	}

	@Override
	public long length() throws IOException {
		return this.localIndexStore.length();
	}

	@Override
	public long size() throws IOException {
		return length() / getIndexLength();
	}

}
