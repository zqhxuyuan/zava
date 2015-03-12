package hdgl.db.store.impl.file;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import hdgl.db.conf.GraphConf;
import hdgl.db.exception.HdglException;
import hdgl.db.graph.Edge;
import hdgl.db.graph.LabelValue;
import hdgl.db.graph.Vertex;
import hdgl.db.store.GraphStore;
import hdgl.util.IterableHelper;
import hdgl.util.NetHelper;
import hdgl.util.WritableHelper;

public class FileGraphStore implements GraphStore {

	// Map<Long, byte[]> vdata = new HashMap<Long, byte[]>();
	// Map<Long, byte[]> edata = new HashMap<Long, byte[]>();

	Configuration conf;
	FileSystem fs;
	FileStatus v_f;
	FileStatus v_v;
	FileStatus e_f;
	FileStatus e_v;
	FSDataOutputStream sv_f;
	FSDataOutputStream sv_v;
	FSDataOutputStream se_f;
	FSDataOutputStream se_v;
	int vsize;
	int esize;

	public FileGraphStore(Configuration conf) throws IOException {
		this.conf = conf;
		fs = FileSystem.get(conf);
		if(!fs.exists(new Path(GraphConf.getGraphRoot(conf), "v.f"))){
			fs.create(new Path(GraphConf.getGraphRoot(conf), "v.f")).close();
		}
		if(!fs.exists(new Path(GraphConf.getGraphRoot(conf), "v.v"))){
			fs.create(new Path(GraphConf.getGraphRoot(conf), "v.v")).close();
		}
		if(!fs.exists(new Path(GraphConf.getGraphRoot(conf), "e.f"))){
			fs.create(new Path(GraphConf.getGraphRoot(conf), "e.f")).close();
		}
		if(!fs.exists(new Path(GraphConf.getGraphRoot(conf), "e.v"))){
			fs.create(new Path(GraphConf.getGraphRoot(conf), "e.v")).close();
		}
		v_f = fs.getFileStatus(new Path(GraphConf.getGraphRoot(conf), "v.f"));
		v_v = fs.getFileStatus(new Path(GraphConf.getGraphRoot(conf), "v.v"));
		e_f = fs.getFileStatus(new Path(GraphConf.getGraphRoot(conf), "e.f"));
		e_v = fs.getFileStatus(new Path(GraphConf.getGraphRoot(conf), "e.v"));
		vsize = GraphConf.getVertexTrunkSize(conf);
		esize = GraphConf.getEdgeTrunkSize(conf);
	}

	public static void writeVertex(Vertex v, DataOutput out) throws IOException {
		out.writeLong(v.getId());
		out.writeUTF(v.getType());
		Iterable<Edge> outedges = v.getOutEdges();
		out.writeInt(IterableHelper.count(outedges));
		for (Edge e : outedges) {
			out.writeLong(e.getId());
		}
		Iterable<Edge> inedges = v.getInEdges();
		out.writeInt(IterableHelper.count(inedges));
		for (Edge e : inedges) {
			out.writeLong(e.getId());
		}
		Iterable<LabelValue> labels = v.getLabels();
		out.writeInt(IterableHelper.count(labels));
		for (LabelValue l : labels) {
			out.writeUTF(l.getName());
			out.writeInt(l.getValue().length);
			out.write(l.getValue());
		}
	}

	public static void writeEdge(Edge e, DataOutput out) throws IOException {
		out.writeLong(e.getId());
		out.writeUTF(e.getType());
		out.writeLong(e.getOutVertex().getId());
		out.writeLong(e.getInVertex().getId());
		Iterable<LabelValue> labels = e.getLabels();
		out.writeInt(IterableHelper.count(labels));
		for (LabelValue l : labels) {
			out.writeUTF(l.getName());
			out.writeInt(l.getValue().length);
			out.write(l.getValue());
		}
	}

	public FileVertex getVertex(long id) {
		try {
			return (FileVertex) parseVertex(id);
		} catch (IOException e) {
			throw new HdglException("Unexpected bad format");
		}
	}

	public FileEdge getEdge(long id) {
		try {
			return (FileEdge) parseEdge(id);
		} catch (IOException e) {
			throw new HdglException("Unexpected bad format");
		}
	}

	public void openStreams() throws IOException {
		sv_f = fs.create(v_f.getPath(), true);
		sv_f.write(new byte[vsize]);
		sv_v = fs.create(v_v.getPath(), true);
		se_f = fs.create(e_f.getPath(), true);
		se_f.write(new byte[esize]);
		se_v = fs.create(e_v.getPath(), true);
	}

	public void closeStreams() throws IOException {
		sv_f.close();
		sv_v.close();
		se_f.close();
		se_v.close();
		sv_f = null;
		sv_v = null;
		se_f = null;
		se_v = null;
	}

	public void addVertex(FileVertex v) throws IOException {
		byte[] data = WritableHelper.toBytes(v);
		if (data.length <= vsize - 8) {
			sv_f.write(data);
			for (int i = data.length; i < vsize; i++) {
				sv_f.write(0);
			}
		} else {
			sv_f.write(data, 0, vsize - 8);
			long pos = sv_v.getPos();
			sv_f.writeLong(pos);
			sv_v.write(data, vsize - 8, data.length - vsize + 8);
		}
	}

	public void addEdge(FileEdge e) throws IOException {
		byte[] data = WritableHelper.toBytes(e);
		if (data.length <= esize - 8) {
			se_f.write(data);
			for (int i = data.length; i < esize; i++) {
				se_f.write(0);
			}
		} else {
			se_f.write(data, 0, esize - 8);
			long pos = se_v.getPos();
			se_f.writeLong(pos);
			se_v.write(data, vsize - 8, data.length - esize + 8);
		}
	}

	@Override
	public InputStream getVertexData(long id) throws IOException {
		long entity = -id+1;
		return new JumpInputStream(fs.open(v_f.getPath()), fs.open(v_v
				.getPath()), entity * vsize, vsize);
	}

	@Override
	public InputStream getEdgeData(long id) throws IOException {
		long entity = id-1;
		return new JumpInputStream(fs.open(e_f.getPath()), fs.open(e_v
				.getPath()), entity * esize, esize);
	}

	@Override
	public Vertex parseVertex(long id) throws IOException {
		FileVertex v = new FileVertex(this);
		DataInputStream in = new DataInputStream(getVertexData(id));
		v.readFields(in);
		in.close();
		return v;
	}

	@Override
	public Edge parseEdge(long id) throws IOException {
		FileEdge e = new FileEdge(this);
		DataInputStream in = new DataInputStream(getEdgeData(id));
		e.readFields(in);
		in.close();
		return e;
	}

	@Override
	public String[] bestPlacesForVertex(long entityId) throws IOException {
		return new String[] { NetHelper.getMyHostName() };
	}

	@Override
	public String[] bestPlacesForEdge(long entityId) throws IOException {
		return new String[] { NetHelper.getMyHostName() };
	}

	@Override
	public long getVertexCount() throws IOException {
		return (v_f.getLen() / vsize)-1;
	}

	@Override
	public long getVertexCountPerBlock() throws IOException {
		return v_f.getBlockSize() / vsize;
	}

	@Override
	public long getEdgeCount() throws IOException {
		return (e_f.getLen() / esize)-1;
	}

	@Override
	public long getEdgeCountPerBlock() throws IOException {
		return e_f.getBlockSize() / esize;
	}

	@Override
	public void close() {

	}

}
