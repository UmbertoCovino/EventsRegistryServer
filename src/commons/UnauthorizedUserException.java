package commons;

public class UnauthorizedUserException extends Exception {
	private static final long serialVersionUID = -2161808073357292179L;

	public UnauthorizedUserException(String msg) {
		super(msg);
	}
}
