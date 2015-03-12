package hdgl.db.graph;

import hdgl.db.task.AsyncResult;

public interface MutableGraph {

	public AsyncResult<Boolean> commit();
	public AsyncResult<Boolean> abort();
	
	public long createVertex(String type);
	public long createEdge(String type, long start, long end);
	
	public void setLabel(long entity, String name, byte[] value);
	
	public void deleteEntity(Entity e);
	public void deleteLabel(Entity e, String name);
	
}
