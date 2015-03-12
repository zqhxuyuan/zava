package hdgl.db.conf;

import hdgl.util.StringHelper;

import org.apache.hadoop.conf.Configuration;

public final class GraphConf {
	
	public static final String GRAPH_ROOT = "hdgl.graph.root";
	public static final String GRAPH_SESSION_ROOT="hdgl.graph.session.root";
	public static final String GRAPH_VERTEX_TRUNK_SIZE = "hdgl.graph.vertex.trunk.size";
	public static final String GRAPH_EDGE_TRUNK_SIZE = "hdgl.graph.edge.trunk.size";
	public static final String DEFAULT_FS = "fs.defaultFS";
	public static final String ZK_SERVER = "hdgl.zookeeper.servers";
	public static final String ZK_ROOT = "hdgl.zookeeper.root";
	public static final String ZK_SESSION_TIMEOUT = "hdgl.zookeeper.timeout";
	
	public class Defaults{
		public static final String GRAPH_SESSION_ROOT="session";
		public static final String ZK_SERVER="localhost:2181";
		public static final int ZK_SESSION_TIMEOUT = 60000;
		public static final String GRAPH_ROOT = "/hdgl/graph/";
		public static final String DEFAULT_FS = "hdfs://localhost:9000/";
		public static final String ZK_ROOT = "/hdgl";
		public static final int GRAPH_VERTEX_TRUNK_SIZE = 256;
		public static final int GRAPH_EDGE_TRUNK_SIZE = 64;
	}
	
	public static Configuration getDefault(){
		Configuration conf = new Configuration();
		conf.set(DEFAULT_FS, Defaults.DEFAULT_FS);
		return conf;
	}
	
	public static String getZookeeperRoot(Configuration conf){
		return conf.get(ZK_ROOT, Defaults.ZK_ROOT);
	}
	
	public static String getGraphRoot(Configuration conf){
		return conf.get(GRAPH_ROOT, Defaults.GRAPH_ROOT);
	}
	
	public static String getGraphSessionRoot(Configuration conf, int sessionId){
		String sessionRoot= StringHelper.makePath(conf.get(GRAPH_ROOT, Defaults.GRAPH_ROOT), conf.get(GRAPH_SESSION_ROOT, Defaults.GRAPH_SESSION_ROOT));
		return StringHelper.makePath(sessionRoot, Integer.toString(sessionId));
	}
	
	public static String getPersistentGraphRoot(Configuration conf)
	{
		return StringHelper.makePath(conf.get(GRAPH_ROOT, Defaults.GRAPH_ROOT), "persist");
	}
	
	public static int getVertexTrunkSize(Configuration conf){
		return conf.getInt(GRAPH_VERTEX_TRUNK_SIZE, Defaults.GRAPH_VERTEX_TRUNK_SIZE);
	}
	
	public static int getEdgeTrunkSize(Configuration conf){
		return conf.getInt(GRAPH_EDGE_TRUNK_SIZE, Defaults.GRAPH_EDGE_TRUNK_SIZE);
	}
}
