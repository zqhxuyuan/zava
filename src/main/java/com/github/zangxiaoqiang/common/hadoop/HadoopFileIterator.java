package com.github.zangxiaoqiang.common.hadoop;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

public class HadoopFileIterator {
	private String partitionName;
	private String lowerLimit;
	private String upperLimit;
	private Queue<Path> folderQueue = new ConcurrentLinkedQueue<Path>();
	private Queue<Path> fileQueue = new ConcurrentLinkedQueue<Path>();

	public HadoopFileIterator(Path basePath, String partitionName, String lowerLimit,
			String upperLimit) throws IOException {
		if (partitionName != null) {
			this.partitionName = partitionName;
			this.lowerLimit = lowerLimit;
			this.upperLimit = upperLimit;
		}
		fetchFolder(basePath);
	}

	public HadoopFileIterator(Path basePath) throws IOException {
		this(basePath, null, null, null);
	}

	private void fetchFolder(Path path) throws IOException {
		FileStatus[] files = HadoopUtil.listAll(path);
		if (files == null) {
			return;
		}
		for (FileStatus fileStatus : files) {
			if (fileStatus.isDirectory()) {
				Path subDirectory = fileStatus.getPath();
				String partition = subDirectory.getName();
				int index = partition.indexOf('=');
				if (index >= 0) {
					String pName = partition.substring(0, index);
					String pValue = partition.substring(index + 1);
					if (partitionName != null && partitionName.equals(pName)) {
						if (lowerLimit != null
								&& pValue.compareTo(lowerLimit) < 0) {
							continue;
						} else if (upperLimit != null
								&& pValue.compareTo(upperLimit) > 0) {
							continue;
						}
					}
				}
				folderQueue.add(subDirectory);
			} else {
				fileQueue.add(fileStatus.getPath());
			}
		}
	}

	public Path next() throws IOException {
		while (fileQueue.isEmpty()) {
			if (folderQueue.isEmpty()) {
				return null;
			}
			Path currentFolder = folderQueue.remove();
			fetchFolder(currentFolder);
		}
		return fileQueue.remove();
	}
}
