package hdgl.db.store.impl.cache;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import hdgl.db.graph.Edge;
import hdgl.db.graph.LabelValue;
import hdgl.db.graph.Vertex;
import hdgl.util.IterableHelper;

import org.apache.hadoop.io.Writable;

public class MemoryVertexImpl implements Vertex, Writable {

	long id;
	String type;
	Map<String, byte[] > labels = new HashMap<String, byte[]>();
	Set<Long> inedges  = new HashSet<Long>();
	Set<Long> outedges  = new HashSet<Long>();
	MemoryGraphStore store;
	
	public MemoryVertexImpl(MemoryGraphStore store){
		this.store = store;
	}
	
	public MemoryVertexImpl(long id, String type, Map<String, byte[]> labels,
			Set<Long> inedges, Set<Long> outedges, MemoryGraphStore store) {
		super();
		this.id = id;
		this.type = type;
		this.labels = labels;
		this.inedges = inedges;
		this.outedges = outedges;
		this.store = store;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Iterable<LabelValue> getLabels() {
		return  IterableHelper.select(labels.entrySet(), new IterableHelper.Map<Map.Entry<String,byte[]>, LabelValue>() {

			@Override
			public LabelValue select(final Entry<String, byte[]> element) {
				return new LabelValue() {
					
					@Override
					public byte[] getValue() {
						return element.getValue();
					}
					
					@Override
					public String getName() {
						return element.getKey();
					}
				};
			}

		});
				
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		
//		out.writeLong(v.getId());
		id=in.readLong();
//		out.writeUTF(v.getType());
		type=in.readUTF();
//		Iterable<Edge> outedges = v.getOutEdges();
//		out.writeInt(IterableHelper.count(outedges));
		int ocount=in.readInt();
		outedges.clear();		
//		for(Edge e:outedges){
		for(int i=0;i<ocount;i++){
//			out.writeLong(e.getId());
			outedges.add(in.readLong());
//		}
		}
//		Iterable<Edge> inedges = v.getInEdges();
//		out.writeInt(IterableHelper.count(inedges));
		int icount = in.readInt();
		inedges.clear();
//		for(Edge e:inedges){
		for(int i=0;i<icount;i++){
//			out.writeLong(e.getId());
			inedges.add(in.readLong());
//		}
		}
//		Iterable<LabelValue> labels = v.getLabels();		
//		out.writeInt(IterableHelper.count(labels));
		int lcount = in.readInt();
		labels.clear();
//		for(LabelValue l:labels){
		for(int i=0;i<lcount;i++){
//			out.writeUTF(l.getName());
			String name=in.readUTF();
//			out.writeInt(l.getValue().length);
			int size=in.readInt();
			byte[] data=new byte[size];
//			out.write(l.getValue());
			in.readFully(data);
			labels.put(name, data);
//		}
		}

	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeLong(id);
		out.writeUTF(type);
		out.writeInt(IterableHelper.count(outedges));
		for(long e:outedges){
			out.writeLong(e);
		}
		out.writeInt(IterableHelper.count(inedges));
		for(long e:inedges){
			out.writeLong(e);
		}
		Iterable<LabelValue> labels = getLabels();
		out.writeInt(IterableHelper.count(labels));
		for(LabelValue l:labels){
			out.writeUTF(l.getName());
			out.writeInt(l.getValue().length);
			out.write(l.getValue());
		}
	}

	@Override
	public Iterable<Edge> getOutEdges() {
		return IterableHelper.select(outedges, new IterableHelper.Map<Long, Edge>() {
			@Override
			public Edge select(Long element) {
				return store.getEdge(element);
			}
		});
	}

	@Override
	public Iterable<Edge> getInEdges() {
		return IterableHelper.select(inedges, new IterableHelper.Map<Long, Edge>() {
			@Override
			public Edge select(Long element) {
				return store.getEdge(element);
			}
		});
	}

	@Override
	public Iterable<Edge> getEdges() {
		return IterableHelper.select(
				IterableHelper.concat(outedges, inedges), 
				new IterableHelper.Map<Long, Edge>() {
			@Override
			public Edge select(Long element) {
				return store.getEdge(element);
			}
		});
	}
	
	@Override
	public byte[] getLabel(String name) {
		return labels.get(name);
	}

}
