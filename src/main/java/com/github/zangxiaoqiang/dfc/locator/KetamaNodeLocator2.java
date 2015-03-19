package com.github.zangxiaoqiang.dfc.locator;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.github.zangxiaoqiang.common.conf.ConfigurationManager;
import com.github.zangxiaoqiang.common.conf.GitConfiguration;
import com.github.zangxiaoqiang.dfc.CacheNode;
import com.github.zangxiaoqiang.dfc.HashAlgorithm;
import com.github.zangxiaoqiang.dfc.server.DataNode;
import com.github.zangxiaoqiang.dfc.server.Partition;
import com.github.zangxiaoqiang.dfc.utils.Context;

public final class KetamaNodeLocator2 extends CommonNodeLocator{
	static GitConfiguration conf = ConfigurationManager.getDefaultConfig();
	private TreeMap<Long, CacheNode> ketamaCacheNodes;
	private HashAlgorithm hashAlg;
	//private int numReps = 160;
	public static final String SERVER_FILE = "server.properties";
	private int cacheNodeNumber ;
	private int allPartitionNumber = 0 ;
	private List<DataNode> allDataNodes;
	private int replication;
	
	public KetamaNodeLocator2(HashAlgorithm alg) {
		hashAlg = alg;
		ketamaCacheNodes = new TreeMap<Long, CacheNode>();
		//numReps = nodeCopies;
		allDataNodes = getDataNodes();
		replication = Integer.valueOf(conf.getValue("data.replication", "3"));
		cacheNodeNumber = (int) Math.ceil((double)allPartitionNumber/(double)replication);
		
		for(int i = 0 ; i < cacheNodeNumber; i++){
			CacheNode cn = new CacheNode("CacheNode-" + i);
			byte[] md5 = hashAlg.computeMd5(cn.getName());
			long m = hashAlg.hash(md5, 0);
			ketamaCacheNodes.put(m, cn);
		}
		
		for (DataNode dn : allDataNodes) {
			for (int i = 0 ; i < dn.getPartitionSize(); i++) {
				Partition p = new Partition(dn.getHostname() + "-Partition-" + i);
				dn.addPartition(p);
				p.setDataNode(dn);
				CacheNode cn = getCacheNode(p.getName());
				cn.addPartition(p);
				p.setCacheNode(cn);
			}
		}
	}
	
	private List<DataNode> getDataNodes() {
		allPartitionNumber = 0 ;
		List<DataNode> nodes = new ArrayList<DataNode>();
		List<String> serverList = Context.loadFile(SERVER_FILE);
		for (String server : serverList) {
			String hostname = server.split(":")[0];
			int port = Integer.valueOf(server.split(":")[1]);
			DataNode node = new DataNode(hostname, port);
			node.setPartitionSize(Integer.valueOf(server.split(":")[2]));
			allPartitionNumber += Integer.valueOf(server.split(":")[2]);
			nodes.add(node);
		}
		
		return nodes;
	}

	@Override
	public CacheNode getCacheNode(final String filePath) {
		CacheNode rv = getCacheNodeByKey(getPathHash(hashAlg, filePath));
		return rv;
	}

	private CacheNode getCacheNodeByKey(long hash) {
		final CacheNode rv;
		Long key = hash;
		if (!ketamaCacheNodes.containsKey(key)) {
			// SortedMap<Long, Node> tailMap = ketamaNodes.tailMap(key);
			// if (tailMap.isEmpty()) {
			// key = ketamaNodes.firstKey();
			// } else {
			// key = tailMap.firstKey();
			// }
			
			// For JDK1.6 version
			key = ketamaCacheNodes.ceilingKey(key);
			if (key == null) {
				key = ketamaCacheNodes.firstKey();
			}
		}
		rv = ketamaCacheNodes.get(key);
		return rv;
	}
}
