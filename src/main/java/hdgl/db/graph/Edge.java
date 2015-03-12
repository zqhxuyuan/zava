package hdgl.db.graph;

public interface Edge extends Entity {

	Vertex getInVertex();
	Vertex getOutVertex();
	Vertex getOtherVertex(Vertex one);

}
