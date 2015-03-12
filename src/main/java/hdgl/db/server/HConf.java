package hdgl.db.server;


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import hdgl.db.conf.GraphConf;
import hdgl.util.StringHelper;

public class HConf {

	public static String getZKMasterRoot(Configuration conf){
		return StringHelper.makePath(GraphConf.getZookeeperRoot(conf),"master");
	}
	
	public static String getZKRegionRoot(Configuration conf){
		return StringHelper.makePath(GraphConf.getZookeeperRoot(conf),"regions");
	}
	
	public static String getZKSessionRoot(Configuration conf){
		return StringHelper.makePath(GraphConf.getZookeeperRoot(conf),"sessions");
	}
	
	public static String getZKQuerySessionRoot(Configuration conf){
		return StringHelper.makePath(GraphConf.getZookeeperRoot(conf),"qsessions");
	}
	
	public static String getZKQuerySessionDir(Configuration conf, int sessionId){
		return StringHelper.makePath(getZKQuerySessionRoot(conf), "q" + String.format("%10d",	sessionId));
	}
	
	public static String getZKBSPRoot(Configuration conf){
		return StringHelper.makePath(GraphConf.getZookeeperRoot(conf),"bfs");
	}
	
	public static ZooKeeper getZooKeeper(Configuration conf, Watcher watcher) throws IOException{
		final ZooKeeper zk = new ZooKeeper(conf.get(GraphConf.ZK_SERVER, GraphConf.Defaults.ZK_SERVER),conf.getInt(GraphConf.ZK_SESSION_TIMEOUT, GraphConf.Defaults.ZK_SESSION_TIMEOUT),watcher);
		return zk;
	}
	
	public static FileSystem getFileSystem(Configuration conf) throws IOException{
		final FileSystem fs =  FileSystem.get(conf);
		return fs;
	}
}
