package projectpon.engine.exceptions;

public class NetworkLockException extends Exception {
	private static final long serialVersionUID = 4889934463863247751L;

	public NetworkLockException(String msg) {
		super(msg);
	}
}
