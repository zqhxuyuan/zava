package com.github.zangxiaoqiang.common.hadoop;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.util.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HadoopUtil {

	private static final Logger log = LoggerFactory.getLogger(HadoopUtil.class);

	private static FileSystem fs;
	private static CompressionCodecFactory codecFactory;

	public static void setFileSystem(FileSystem fileSystem) {
		fs = fileSystem;
	}

	static {
		init();
	}

	public static void init() {
		Configuration conf = new Configuration();

		while (!init(conf)) {
			log.error("Could not init HDFS by time:"
					+ System.currentTimeMillis());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private static boolean init(Configuration conf) {
		try {
			fs = FileSystem.newInstance(conf);
			codecFactory = new CompressionCodecFactory(conf);
			return fs != null && codecFactory != null;
		} catch (Exception e) {
			log.error("fail to connect to hadoop, sleep 1s", e);
		}
		return false;
	}

	public static FileSystem getFileSystem() {
		return fs;
	}

	public static boolean existPath(Path path) throws IOException {
		return getFileSystem().exists(path);
	}

	public static long getLastModifiedTime(String path) throws IOException {
		return getFileSystem().getFileStatus(new Path(path))
				.getModificationTime();
	}

	public static InputStream openFile(String path) throws IOException {
		return openFile(new Path(path));
	}

	public static InputStream openFile(Path path) throws IOException {
		FileSystem fs = getFileSystem();
		if (!fs.exists(path)) {
			return null;
		}
		return fs.open(path);
	}

	public static boolean isDir(String path) throws IOException{
		Path p = new Path(path);
		return fs.exists(p) && !fs.isFile(p);
	}
	
	public static List<String> getSubDirs(Path parentDir) throws IOException {
		if (!existPath(parentDir)) {
			return null;
		}
		FileSystem fs = getFileSystem();
		List<String> subPaths = new ArrayList<String>();
		if (!fs.isFile(parentDir)) {
			FileStatus[] fsArray = fs.listStatus(parentDir);
			for (FileStatus status : fsArray) {
				subPaths.add(status.getPath().getName());
			}
			return subPaths;
		}
		return null;
	}

	public static List<String> getSubDirs(String parentDir) throws IOException {
		return getSubDirs(new Path(parentDir));
	}

	/**
	 * @param parentDir
	 * @return all files and folder under the parentDir
	 * @throws IOException
	 */
	public static FileStatus[] listAll(Path parentDir) throws IOException {
		if (parentDir == null) {
			return null;
		}
		if (!existPath(parentDir)) {
			return null;
		}
		FileSystem fs = getFileSystem();
		FileStatus fss = fs.getFileStatus(parentDir);
		if (fss.isDirectory()) {
			return fs.listStatus(parentDir);
		}
		return null;
	}

	/**
	 * @param path
	 * @return the codec which can be used for the specified path
	 */
	public static CompressionCodec getCodec(Path path) {
		return codecFactory.getCodec(path);
	}

	/**
	 * Get the lineReader which can be used to read uncompressed lines from the
	 * path. Current, Hive only support '\n' as the lines terminator.
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static LineReader getLineReader(Path path) throws IOException {
		InputStream inputStream = openFile(path);
		CompressionCodec codec = getCodec(path);
		return new LineReader(codec == null ? inputStream
				: codec.createInputStream(inputStream));
	}

	public static long getFileTotalSize(String path) throws IOException {
		if(path == null || path.isEmpty()){
			return 0;
		}
		Path hdfsPath = new Path(path);
		FileSystem fs = getFileSystem();
		FileStatus stauts = fs.getFileStatus(hdfsPath);
		return getFileTotalSize(fs, stauts);
	}
	
	public static long getFileTotalSize(List<String> paths) throws IOException {
		long allSize = 0;
		for (String path : paths) {
			allSize += getFileTotalSize(path);
		}
		return allSize;
	}

	public static long getFileTotalSize(FileSystem fs, FileStatus stauts)
			throws IOException {
		long totalSize = 0;
		if (!stauts.isDirectory()) {
			totalSize = stauts.getLen();
		} else {
			FileStatus[] stautsArray = fs.listStatus(stauts.getPath());
			for (FileStatus subStauts : stautsArray) {
				totalSize += getFileTotalSize(fs, subStauts);
			}
		}
		return totalSize;
	}

	public static void write(String uri, String value) throws IOException {
		Path path = new Path(uri);

		FSDataOutputStream out = null;
		FileSystem fs = getFileSystem();
		if (fs.exists(path)) {
			out = fs.append(path);
		} else {
			out = fs.create(path);
		}

		out.writeBytes(value);
		out.close();
	}

	public static boolean deleteFile(String filePath) throws IOException {
		Path path = new Path(filePath);
		if(!fs.exists(path)){
			return true;
		}
		return fs.delete(path, false);
	}

	public static boolean deleteFolder(String filePath) throws IOException {
		Path path = new Path(filePath);
		if(!fs.exists(path)){
			return true;
		}
		return fs.delete(path, true);
	}
}
