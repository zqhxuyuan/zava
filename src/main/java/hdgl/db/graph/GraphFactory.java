package hdgl.db.graph;

import hdgl.db.impl.HConn;
import hdgl.db.impl.HGraph;

import org.apache.hadoop.conf.Configuration;

public class GraphFactory {

	public static Graph connect(Configuration conf){
		HConn conn=new HConn(conf);
		return new HGraph(conf, conn);
	}
	
}
