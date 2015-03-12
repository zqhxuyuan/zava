package hdgl.db.store.impl.hdfs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;


import hdgl.db.conf.GraphConf;
import hdgl.db.graph.Edge;
import hdgl.db.graph.HGraphIds;
import hdgl.db.graph.Vertex;
import hdgl.db.store.GraphStore;
import hdgl.db.store.HConf;
import hdgl.db.store.impl.hdfs.mapreduce.EdgeInputStream;
import hdgl.db.store.impl.hdfs.mapreduce.FSDataInputStreamPool;
import hdgl.db.store.impl.hdfs.mapreduce.HEdge;
import hdgl.db.store.impl.hdfs.mapreduce.HFullPseudoEdge;
import hdgl.db.store.impl.hdfs.mapreduce.HPseudoVertex;
import hdgl.db.store.impl.hdfs.mapreduce.HVertex;
import hdgl.db.store.impl.hdfs.mapreduce.Parameter;
import hdgl.db.store.impl.hdfs.mapreduce.VertexInputStream;
import hdgl.util.NetHelper;
import hdgl.util.StringHelper;

public class HdfsGraphStore implements GraphStore {
	
	static class PrefixPathFilter implements PathFilter{

		String prefix;
		
		public PrefixPathFilter(String prefix) {
			super();
			this.prefix = prefix;
		}

		@Override
		public boolean accept(Path path) {
			return path.getName().startsWith(prefix);
		}
		
	}
	
	static class SuffixComparator implements Comparator<FileStatus>{
		public static final SuffixComparator I = new SuffixComparator();
		
		@Override
		public int compare(FileStatus o1, FileStatus o2) {
			return o1.getPath().getName().compareTo(o2.getPath().getName());
		}
		
	}
	
	//vertex fixed length file
	FileStatus[] v_f;
	//vertex vary length file
	FileStatus[] v_v;
	//edge fixed length file
	FileStatus[] e_f;
	//edge vary length file
	FileStatus[] e_v;
	
	FileSystem fs;
	
	Configuration conf;
	
	int vtrunkSize;
	int etrunkSize;
	
	long vcount;
	long ecount;
	long vcountPerBlock;
	long ecountPerBlock;
	
	public HdfsGraphStore(Configuration conf) throws IOException{
		this.conf = conf;
		String root = GraphConf.getPersistentGraphRoot(conf);
		fs = HConf.getFileSystem(conf);
		vtrunkSize = GraphConf.getVertexTrunkSize(conf);
		etrunkSize = GraphConf.getEdgeTrunkSize(conf);
		Path rootPath = new Path(root);
		v_f = fs.listStatus(rootPath, new PrefixPathFilter(Parameter.VERTEX_REGULAR_FILE_NAME));
		v_v = fs.listStatus(rootPath, new PrefixPathFilter(Parameter.VERTEX_IRREGULAR_FILE_NAME));
		e_f = fs.listStatus(rootPath, new PrefixPathFilter(Parameter.EDGE_REGULAR_FILE_NAME));
		e_v = fs.listStatus(rootPath, new PrefixPathFilter(Parameter.EDGE_IRREGULAR_FILE_NAME));
		Arrays.sort(v_f, 0, v_f.length, SuffixComparator.I);
		Arrays.sort(v_v, 0, v_v.length, SuffixComparator.I);
		Arrays.sort(e_f, 0, e_f.length, SuffixComparator.I);
		Arrays.sort(e_v, 0, e_v.length, SuffixComparator.I);
		long len=0;
		for(FileStatus f:v_f){
			len+=f.getLen();
		}
		vcount = len/vtrunkSize;
		len=0;
		for(FileStatus f:e_f){
			len+=f.getLen();
		}
		ecount = len/etrunkSize;
		
	}
	
	public InputStream getVertexData(long id) throws IOException
	{
		VertexInputStream eis = new VertexInputStream(id, conf);
		return eis;
//		long seekPos = (id - 1) * vtrunkSize;
//		return new JumpInputStream(v_f, v_v, seekPos, vtrunkSize, fs);
	}
	
	public InputStream getEdgeData(long id) throws IOException
	{
		EdgeInputStream eis = new EdgeInputStream(id, conf);
		return eis;
		//long seekpos = (-id - 1) * etrunkSize;
		//return new JumpInputStream(e_f, e_v, seekpos, etrunkSize, fs);
	}
	
	public hdgl.db.graph.Vertex parseVertex(long id) throws IOException
	{
		VertexInputStream vis = null;
		try{
			vis = (VertexInputStream)getVertexData(id);		
			HVertex v = new HVertex(-vis.readInt(), "", this);
			int outNum, edge, vertex, inNum, num;
			outNum = vis.readInt();
			inNum = vis.readInt();
			for (int i = 0; i < outNum; i++)
			{
				edge = -vis.readInt();
				vertex = -vis.readInt();
				v.addOutEdge(edge, vertex);
			}
			for (int i = 0; i < inNum; i++)
			{
				edge = -vis.readInt();
				vertex = -vis.readInt();
				v.addInEdge(edge, vertex);
			}
			num = vis.readInt();
			int len;
			for (int i = 0; i < num; i++)
			{
				len = vis.readInt();
				byte[] b = new byte[len];
				String key = null, value = null;
				if (len == vis.read(b))
				{
					key = new String(b);
				}
				len = vis.readInt();
				b = new byte[len];
				if (len == vis.read(b))
				{
					value = new String(b);
				}
				if (!(key.length() == 0))
				{
					if (key.compareTo("type") == 0)
					{
						v.setType(new String(StringHelper.stringToBytes(value)));
					}
					else {
						v.addLabel(key, StringHelper.stringToBytes(value));
					}
				}
			}			
			return v;
		}finally{
			if(vis!=null){
				vis.close();
			}
		}
	}
	
	public hdgl.db.graph.Edge parseEdge(long id) throws IOException
	{
		EdgeInputStream eis = null;
		try{
			eis = (EdgeInputStream)getEdgeData(id);
			int eid, v1, v2;
			eid = -eis.readInt();
			v1 = -eis.readInt();
			v2 = -eis.readInt();
			HEdge e = new HEdge(eid, "", v1, v2, this);
			int num;
			num = eis.readInt();
			int len;
			for (int i = 0; i < num; i++)
			{
				len = eis.readInt();
				byte[] b = new byte[len];
				String key = null, value = null;
				if (len == eis.read(b))
				{
					key = new String(b);
				}
				len = eis.readInt();
				b = new byte[len];
				if (len == eis.read(b))
				{
					value = new String(b);
				}
				if (!(key.length() == 0))
				{
					if (key.compareTo("type") == 0)
					{
						e.setType(new String(StringHelper.stringToBytes(value)));
					}
					else {
						e.addLabel(key, StringHelper.stringToBytes(value));
					}
				}
			}
			return e;
		}finally{
			if(eis!=null){
				eis.close();
			}
		}
	}

	@Override
	public String[] bestPlacesForVertex(long vId) throws IOException {
		vId = HGraphIds.extractEntityId(vId);
		long offset= vId * vtrunkSize;
		int nthfile=0;
		while (v_f[nthfile].getLen()<offset) {
			offset -= v_f[nthfile].getLen();
			nthfile++;
			if(nthfile>=v_f.length){
				return new String[0];
			}
		}		
		BlockLocation[] locs = fs.getFileBlockLocations(v_f[nthfile], offset, vtrunkSize);
		Set<String> hosts=new HashSet<String>();
		for(BlockLocation loc:locs){
			for(String host:loc.getHosts()){
				hosts.add(host.equals("localhost")?NetHelper.getMyHostName():host);
			}
		}
		return hosts.toArray(new String[0]);
	}

	@Override
	public String[] bestPlacesForEdge(long entityId) throws IOException {
		entityId = HGraphIds.extractEntityId(entityId);
		long offset= entityId * etrunkSize;
		int nthfile=0;
		while (e_f[nthfile].getLen()<offset) {
			offset -= e_f[nthfile].getLen();
			nthfile++;
			if(nthfile>=e_f.length){
				return new String[0];
			}
		}	
		BlockLocation[] locs = fs.getFileBlockLocations(e_f[nthfile], offset, etrunkSize);
		Set<String> hosts = new HashSet<String>();
		for(BlockLocation loc:locs){
			for(String host:loc.getHosts()){
				hosts.add(host);
			}
		}
		return hosts.toArray(new String[0]);
	}

	@Override
	public long getVertexCount() throws IOException {
		return vcount;
	}

	@Override
	public long getEdgeCount() throws IOException {
		return ecount;
	}

	@Override
	public long getVertexCountPerBlock() throws IOException {
		return v_f[0].getBlockSize()/vtrunkSize;
	}

	@Override
	public long getEdgeCountPerBlock() throws IOException {
		return e_f[0].getBlockSize()/etrunkSize;
	}

	@Override
	public void close() {
		try{
			fs.close();
			FSDataInputStreamPool.close();
		}catch(Exception ex){
			
		}
	}

	@Override
	public Vertex getVertex(long id) throws IOException {
		return new HPseudoVertex(id, this);
	}

	@Override
	public Edge getEdge(long id) throws IOException {
		return new HFullPseudoEdge(id, this);
	}
}
