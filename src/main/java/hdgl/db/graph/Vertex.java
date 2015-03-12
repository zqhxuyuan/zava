package hdgl.db.graph;

public interface Vertex extends Entity{
	Iterable<Edge> getOutEdges();
	Iterable<Edge> getInEdges();
	Iterable<Edge> getEdges();
}
