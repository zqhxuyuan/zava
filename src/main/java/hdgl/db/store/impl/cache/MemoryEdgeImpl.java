package hdgl.db.store.impl.cache;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import hdgl.db.exception.HdglException;
import hdgl.db.graph.Edge;
import hdgl.db.graph.LabelValue;
import hdgl.db.graph.Vertex;
import hdgl.util.IterableHelper;

import org.apache.hadoop.io.Writable;

public class MemoryEdgeImpl implements Edge, Writable {

	long id;
	String type;
	long start;
	long end;
	Map<String, byte[] > labels = new HashMap<String, byte[]>();
	MemoryGraphStore store;
	
	public MemoryEdgeImpl(MemoryGraphStore store){
		this.store = store;
	}
	
	public MemoryEdgeImpl(long id, String type, long start, long end,
			Map<String, byte[]> labels, MemoryGraphStore store) {
		super();
		this.id = id;
		this.type = type;
		this.start = start;
		this.end = end;
		this.labels = labels;
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
//		out.writeLong(e.getId());
		id = in.readLong();
//		out.writeUTF(e.getType());
		type = in.readUTF();
//		out.writeLong(e.getOutVertex().getId());
		end = in.readLong();
//		out.writeLong(e.getInVertex().getId());
		start = in.readLong();
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
		out.writeLong(getId());
		out.writeUTF(getType());
		out.writeLong(end);
		out.writeLong(start);
		Iterable<LabelValue> labels = getLabels();
		out.writeInt(IterableHelper.count(labels));
		for(LabelValue l:labels){
			out.writeUTF(l.getName());
			out.writeInt(l.getValue().length);
			out.write(l.getValue());
		}
	}

	@Override
	public Vertex getInVertex() {
		return store.getVertex(start);
	}

	@Override
	public Vertex getOutVertex() {
		return store.getVertex(end);
	}

	@Override
	public Vertex getOtherVertex(Vertex one) {
		if(one.getId()==start){
			return getOutVertex();
		}else if(one.getId()==end){
			return getInVertex();
		}else{
			throw new HdglException("Cannot found vertex");
		}
	}

	@Override
	public byte[] getLabel(String name) {
		return labels.get(name);
	}

}
