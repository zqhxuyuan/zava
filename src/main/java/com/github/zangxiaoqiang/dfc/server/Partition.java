package com.github.zangxiaoqiang.dfc.server;

import com.github.zangxiaoqiang.dfc.CacheNode;

public class Partition {
	private String name;
	private DataNode dataNode;
	private CacheNode cacheNode;

	public Partition(String name) {
		this.name = name;
	}

	public DataNode getDataNode() {
		return dataNode;
	}

	public void setDataNode(DataNode dataNode) {
		this.dataNode = dataNode;
	}

	public CacheNode getCacheNode() {
		return cacheNode;
	}

	public void setCacheNode(CacheNode cacheNode) {
		this.cacheNode = cacheNode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
