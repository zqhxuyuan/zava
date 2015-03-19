package com.github.zangxiaoqiang.dfc;

import java.util.ArrayList;
import java.util.List;

import com.github.zangxiaoqiang.dfc.server.Partition;

public class CacheNode {
	private String name;
	private List<Partition> partitionList = new ArrayList();

	public CacheNode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Partition> getPartitions() {
		return partitionList;
	}

	public void setPartitions(List<Partition> partitionList) {
		this.partitionList = partitionList;
	}

	public void addPartition(Partition p) {
		partitionList.add(p);
	}

}
