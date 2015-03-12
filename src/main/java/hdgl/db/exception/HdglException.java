package hdgl.db.exception;

public class HdglException extends Error {

	private static final long serialVersionUID = 3517899790899292772L;

	public HdglException() {
		super();
	}

	public HdglException(String message, Throwable cause) {
		super(message, cause);
	}

	public HdglException(String message) {
		super(message);
	}

	public HdglException(Throwable cause) {
		super(cause);
	}

}
