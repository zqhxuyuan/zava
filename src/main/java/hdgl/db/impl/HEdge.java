package hdgl.db.impl;

import hdgl.db.graph.Edge;
import hdgl.db.graph.LabelValue;
import hdgl.db.graph.Vertex;

public class HEdge implements Edge {
	
	long id;
	String type;
	Vertex start;
	Vertex end;
	
	public HEdge(long id, String type, Vertex start, Vertex end){
		this.id = id;
		this.type = type;
		this.start = start;
		this.end = end;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public void addLabel(String key, byte[] value)
	{
		
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertex getInVertex() {
		return start;
	}

	@Override
	public Vertex getOutVertex() {
		return end;
	}

	@Override
	public Vertex getOtherVertex(Vertex one) {
		if(one==start){
			return end;
		}else if(one == end){
			return start;
		}else{
			return null;
		}
	}

	@Override
	public byte[] getLabel(String name) {
		// TODO Auto-generated method stub
		return null;
	}
}
