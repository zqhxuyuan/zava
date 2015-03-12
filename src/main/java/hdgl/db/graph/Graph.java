package hdgl.db.graph;

public interface Graph {

	Iterable<Path> query(String queryRegex);
	
	public MutableGraph beginModify();
	
}
