package hdgl.db.store.impl.hdfs.mapreduce;

import java.io.IOException;

import hdgl.db.exception.HdglException;
import hdgl.db.graph.Edge;
import hdgl.db.graph.LabelValue;
import hdgl.db.graph.Vertex;
import hdgl.db.store.GraphStore;

public class HPseudoEdge implements Edge {

	long id;
	long start, end;
	GraphStore store;
	Edge actualEdge;
	
	public HPseudoEdge(long id, long start, long end, GraphStore store) {
		super();
		this.id = id;
		this.start = start;
		this.end = end;
		this.store = store;
	}
	
	void loadActual(){
		if(actualEdge==null){
			try {
				actualEdge = store.parseEdge(id);
			} catch (IOException e) {
				throw new HdglException(e);
			}
		}
	}
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getType() {
		loadActual();
		return actualEdge.getType();
	}

	@Override
	public Iterable<LabelValue> getLabels() {
		loadActual();
		return actualEdge.getLabels();
	}

	@Override
	public byte[] getLabel(String name) {
		loadActual();
		return actualEdge.getLabel(name);
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
	
	
	
}
