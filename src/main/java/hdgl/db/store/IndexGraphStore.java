package hdgl.db.store;

public interface IndexGraphStore extends GraphStore {

	public Iterable<Long> findVertexByLabelInRange(String label, byte[] min, byte[] max);
	
	public Iterable<Long> findEdgeByLabelInRange(String label, byte[] min, byte[] max);
	
	public long findVertexByLabelValue(String label, byte[] val);
	
	public long findEdgeByLabelValue(String label, byte[] val);
	
	public void prepareIndex();
	
}
