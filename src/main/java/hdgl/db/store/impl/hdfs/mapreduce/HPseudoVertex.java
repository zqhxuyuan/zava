package hdgl.db.store.impl.hdfs.mapreduce;

import java.io.IOException;

import hdgl.db.exception.HdglException;
import hdgl.db.graph.Edge;
import hdgl.db.graph.LabelValue;
import hdgl.db.store.GraphStore;

public class HPseudoVertex implements hdgl.db.graph.Vertex{

	long id;
	GraphStore store;
	hdgl.db.graph.Vertex actualVertex;
	
	
	public HPseudoVertex(long id, GraphStore store) {
		super();
		this.id = id;
		this.store = store;
	}
	
	hdgl.db.graph.Vertex actual(){
		if(actualVertex==null){
			try {
				actualVertex = store.parseVertex(id);
			} catch (IOException e) {
				throw new HdglException(e);
			}
		}
		return actualVertex;
	}
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getType() {
		return actual().getType();
	}

	@Override
	public Iterable<LabelValue> getLabels() {
		return actual().getLabels();
	}

	@Override
	public byte[] getLabel(String name) {
		return actual().getLabel(name);
	}

	@Override
	public Iterable<Edge> getOutEdges() {
		return actual().getOutEdges();
	}

	@Override
	public Iterable<Edge> getInEdges() {
		return actual().getInEdges();
	}

	@Override
	public Iterable<Edge> getEdges() {
		return actual().getEdges();
	}
	
}
