package projectpon.engine.exceptions;

import java.io.IOException;

public class NetworkException extends Exception {
	private static final long serialVersionUID = 5326560223406632826L;

	public NetworkException(IOException e) {
		super(e.getMessage());
	}
}
