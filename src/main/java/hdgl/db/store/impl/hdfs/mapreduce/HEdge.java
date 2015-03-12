package hdgl.db.store.impl.hdfs.mapreduce;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import hdgl.db.exception.HdglException;
import hdgl.db.graph.Edge;
import hdgl.db.graph.LabelValue;
import hdgl.db.graph.Vertex;
import hdgl.db.store.GraphStore;
import hdgl.util.IterableHelper;

public class HEdge implements Edge {
	
	long id;
	String type;
	long start;
	long end;
	Map<String, byte[]> labelsMap = new HashMap<String, byte[]>();
	GraphStore store;
	
	public HEdge(long id, String type, long start, long end, GraphStore store){
		assert id<0;
		assert start>0;
		assert end>0;
		
		this.id = id;
		this.type = type;
		this.start = start;
		this.end = end;
		this.store = store;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public void addLabel(String key, byte[] value)
	{
		labelsMap.put(key, value);
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
		return  IterableHelper.select(labelsMap.entrySet(), new IterableHelper.Map<Map.Entry<String,byte[]>, LabelValue>() {

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
	public Vertex getInVertex() {
		return new HPseudoVertex(start, store);
	}

	@Override
	public Vertex getOutVertex() {
		return new HPseudoVertex(end, store);
	}

	@Override
	public Vertex getOtherVertex(Vertex one) {
		if(one.getId()==start){
			return getOutVertex();
		}else if(one.getId() == end){
			return getInVertex();
		}else{
			throw new HdglException("vertex(id="+one.getId()+") not found");
		}
	}

	@Override
	public byte[] getLabel(String name) {
		return labelsMap.get(name);
	}
}
