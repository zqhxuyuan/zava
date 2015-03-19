package com.github.zangxiaoqiang.dfc.server;

import java.util.ArrayList;
import java.util.List;

public class DataNode {

	private String hostname;
	private int port;
	private List<Partition> partitionList = new ArrayList<Partition>();

	DataXceiverServer dataServer;
	private int partitionSize;

	public DataNode(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
		dataServer = new DataXceiverServer(hostname, port);
	}

	private void start() {
		dataServer.start();
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String name) {
		this.hostname = name;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static void main(String[] args) {
		DataNode server = new DataNode("localhost", 50088);
		server.start();
	}

	public int getPartitionSize() {
		return partitionSize;
	}

	public void setPartitionSize(int partitionSize) {
		this.partitionSize = partitionSize;
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