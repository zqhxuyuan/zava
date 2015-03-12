package hdgl.db.store.impl.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.Writable;

import hdgl.db.exception.HdglException;
import hdgl.db.graph.Edge;
import hdgl.db.graph.LabelValue;
import hdgl.db.graph.Vertex;
import hdgl.db.store.GraphStore;
import hdgl.util.IterableHelper;
import hdgl.util.NetHelper;

public class MemoryGraphStore implements GraphStore {

	Map<Long,byte[]> vdata = new HashMap<Long, byte[]>();
	Map<Long,byte[]> edata = new HashMap<Long, byte[]>();
	
	public static void writeVertex(Vertex v, DataOutput out) throws IOException{
		out.writeLong(v.getId());
		out.writeUTF(v.getType());
		Iterable<Edge> outedges = v.getOutEdges();
		out.writeInt(IterableHelper.count(outedges));
		for(Edge e:outedges){
			out.writeLong(e.getId());
		}
		Iterable<Edge> inedges = v.getInEdges();
		out.writeInt(IterableHelper.count(inedges));
		for(Edge e:inedges){
			out.writeLong(e.getId());
		}
		Iterable<LabelValue> labels = v.getLabels();
		out.writeInt(IterableHelper.count(labels));
		for(LabelValue l:labels){
			out.writeUTF(l.getName());
			out.writeInt(l.getValue().length);
			out.write(l.getValue());
		}
	}
	
	public static void writeEdge(Edge e, DataOutput out) throws IOException{
		out.writeLong(e.getId());
		out.writeUTF(e.getType());
		out.writeLong(e.getOutVertex().getId());
		out.writeLong(e.getInVertex().getId());
		Iterable<LabelValue> labels = e.getLabels();
		out.writeInt(IterableHelper.count(labels));
		for(LabelValue l:labels){
			out.writeUTF(l.getName());
			out.writeInt(l.getValue().length);
			out.write(l.getValue());
		}
	}
	
	public MemoryVertexImpl getVertex(long id){
		try {
			return (MemoryVertexImpl) parseVertex(id);
		} catch (IOException e) {
			throw new HdglException("Unexpected bad format");
		}
	}
	
	public MemoryEdgeImpl getEdge(long id){
		try {
			return (MemoryEdgeImpl) parseEdge(id);
		} catch (IOException e) {
			throw new HdglException("Unexpected bad format");
		}
	}
	
	public void addVertex(Vertex v){
		try{
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			if(v instanceof Writable){
				((Writable) v).write(new DataOutputStream(buf));
			}else{
				writeVertex(v, new DataOutputStream(buf));
			}
			byte[] data = buf.toByteArray(); 
			vdata.put(v.getId(), data);
		}catch(IOException ex){
			throw new HdglException("unexpected exception", ex);
		}
	}
	
	public void addEdge(Edge e){
		try{
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			if(e instanceof Writable){
				((Writable) e).write(new DataOutputStream(buf));
			}else{
				writeEdge(e, new DataOutputStream(buf));
			}
			byte[] data = buf.toByteArray(); 
			edata.put(e.getId(), data);
		}catch(IOException ex){
			throw new HdglException("unexpected exception", ex);
		}
	}
	
	@Override
	public InputStream getVertexData(long id) throws IOException {
		if(!vdata.containsKey(id)){
			throw new HdglException("Bad vertex id: "+id);
		}
		return new ByteArrayInputStream(vdata.get(id));
	}

	@Override
	public InputStream getEdgeData(long id) throws IOException {
		if(!edata.containsKey(id)){
			throw new HdglException("Bad edge id: "+id);
		}
		return new ByteArrayInputStream(edata.get(id));
	}

	@Override
	public Vertex parseVertex(long id) throws IOException {
		MemoryVertexImpl v=new MemoryVertexImpl(this);
		DataInputStream in=new DataInputStream(getVertexData(id));
		v.readFields(in);
		in.close();
		return v;
	}

	@Override
	public Edge parseEdge(long id) throws IOException {
		MemoryEdgeImpl e=new MemoryEdgeImpl(this);
		DataInputStream in=new DataInputStream(getEdgeData(id));
		e.readFields(in);
		in.close();
		return e;
	}

	@Override
	public String[] bestPlacesForVertex(long entityId) throws IOException {
		return new String[]{NetHelper.getMyHostName()};
	}

	@Override
	public String[] bestPlacesForEdge(long entityId) throws IOException {
		return new String[]{NetHelper.getMyHostName()};
	}

	@Override
	public long getVertexCount() throws IOException {
		return vdata.size();
	}

	@Override
	public long getVertexCountPerBlock() throws IOException {
		return vdata.size();
	}

	@Override
	public long getEdgeCount() throws IOException {
		return edata.size();
	}

	@Override
	public long getEdgeCountPerBlock() throws IOException {
		return edata.size();
	}

	@Override
	public void close() {
		
	}

}
