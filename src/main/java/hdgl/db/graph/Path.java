package hdgl.db.graph;

public interface Path {

	public int getLength();
	public Vertex getVertex(int index);
	public Edge getEdge(int index);
	public Iterable<Entity> getEntities();
	public Iterable<Vertex> getVertices();
	public Iterable<Edge> getEdges();
	
}
