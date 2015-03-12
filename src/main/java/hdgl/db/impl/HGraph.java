package hdgl.db.impl;

import org.apache.hadoop.conf.Configuration;

import hdgl.db.graph.Graph;
import hdgl.db.graph.MutableGraph;
import hdgl.db.graph.Path;

public class HGraph implements Graph {

	Configuration conf;
	HConn conn;
	
	public HGraph(Configuration conf, HConn conn){
		this.conf = conf;
		this.conn = conn;
	}
	
	@Override
	public Iterable<Path> query(String queryRegex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MutableGraph beginModify() {
		return new HMutableGraph(conf, conn);
	}

}
