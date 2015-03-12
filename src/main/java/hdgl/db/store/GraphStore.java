package hdgl.db.store;

import java.io.IOException;
import java.io.InputStream;

public interface GraphStore {

	public InputStream getVertexData(long id) throws IOException;
	
	public InputStream getEdgeData(long id) throws IOException;
	
	public hdgl.db.graph.Vertex getVertex(long id) throws IOException;	
	
	public hdgl.db.graph.Edge getEdge(long id) throws IOException;
	
	public hdgl.db.graph.Vertex parseVertex(long id) throws IOException;	
	
	public hdgl.db.graph.Edge parseEdge(long id) throws IOException;
	
	public String[] bestPlacesForVertex(long entityId) throws IOException ;
	
	public String[] bestPlacesForEdge(long entityId) throws IOException ;
	
	public long getVertexCount() throws IOException;
	
	public long getVertexCountPerBlock() throws IOException;
	
	public long getEdgeCount() throws IOException;
	
	public long getEdgeCountPerBlock() throws IOException;
	
	public void close();
}
