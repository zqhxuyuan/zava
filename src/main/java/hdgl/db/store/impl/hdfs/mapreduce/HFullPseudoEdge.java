package hdgl.db.store.impl.hdfs.mapreduce;

import java.io.IOException;

import hdgl.db.exception.HdglException;
import hdgl.db.graph.Edge;
import hdgl.db.graph.LabelValue;
import hdgl.db.graph.Vertex;
import hdgl.db.store.GraphStore;

public class HFullPseudoEdge implements Edge {

	long id;
	GraphStore store;
	Edge actualEdge;
	
	public HFullPseudoEdge(long id, GraphStore store) {
		super();
		this.id = id;
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
		loadActual();
		return actualEdge.getInVertex();
	}

	@Override
	public Vertex getOutVertex() {
		loadActual();
		return actualEdge.getOutVertex();
	}

	@Override
	public Vertex getOtherVertex(Vertex one) {
		loadActual();
		return actualEdge.getOtherVertex(one);
	}
	
	
	
}
