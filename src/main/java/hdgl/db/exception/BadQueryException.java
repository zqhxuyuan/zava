package hdgl.db.exception;

public class BadQueryException extends Exception {

	private static final long serialVersionUID = -5420030215291516510L;
	public String query;

	public BadQueryException(String query, Exception inner) {
		super("Bad query expression: " + query, inner);
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
}
