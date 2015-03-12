package hdgl.db.store.impl.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.WeakHashMap;

import org.apache.hadoop.conf.Configuration;

import hdgl.db.exception.HdglException;
import hdgl.db.graph.Edge;
import hdgl.db.graph.Entity;
import hdgl.db.graph.Vertex;
import hdgl.db.store.GraphStore;
import hdgl.db.store.IndexGraphStore;
import hdgl.db.store.impl.hdfs.HdfsGraphStore;

public class MemoryCacheGraphStore extends HdfsGraphStore implements IndexGraphStore {

	static final int CACHE_SIZE = 512;
	
	WeakHashMap<Long, Entity> cache = new WeakHashMap<Long, Entity>();
	
	public MemoryCacheGraphStore(Configuration conf) throws IOException {
		super(conf);
	}


	@Override
	public InputStream getVertexData(long id) throws IOException {
		return super.getVertexData(id);
	}

	@Override
	public InputStream getEdgeData(long id) throws IOException {
		return super.getEdgeData(id);
	}

	@Override
	public Vertex parseVertex(long id) throws IOException {
		if(cache.containsKey(id)){
			return (Vertex) cache.get(id);
		}else{
			Vertex v = super.parseVertex(id);
			cache.put(id, v);
			return v;
		}
	}

	@Override
	public Edge parseEdge(long id) throws IOException {
		if(cache.containsKey(id)){
			return (Edge) cache.get(id);
		}else{
			Edge e = super.parseEdge(id);
			cache.put(id, e);
			return e;
		}
	}

	@Override
	public String[] bestPlacesForVertex(long entityId) throws IOException {
		return super.bestPlacesForVertex(entityId);
	}

	@Override
	public String[] bestPlacesForEdge(long entityId) throws IOException {
		return super.bestPlacesForEdge(entityId);
	}

	@Override
	public long getVertexCount() throws IOException {
		return super.getVertexCount();
	}

	@Override
	public long getVertexCountPerBlock() throws IOException {
		return super.getVertexCountPerBlock();
	}

	@Override
	public long getEdgeCount() throws IOException {
		return super.getEdgeCount();
	}

	@Override
	public long getEdgeCountPerBlock() throws IOException {
		return super.getEdgeCountPerBlock();
	}

	@Override
	public void close() {
		super.close();
	}

	@Override
	public Iterable<Long> findVertexByLabelInRange(String label, byte[] min,
			byte[] max) {
		throw new HdglException("no index on "+label);
	}

	@Override
	public Iterable<Long> findEdgeByLabelInRange(String label, byte[] min,
			byte[] max) {
		throw new HdglException("no index on "+label);
	}

	@Override
	public long findVertexByLabelValue(String label, byte[] val) {
		throw new HdglException("no index on "+label);
	}

	@Override
	public long findEdgeByLabelValue(String label, byte[] val) {
		throw new HdglException("no index on "+label);
	}

	@Override
	public void prepareIndex() {
		
	}

}
