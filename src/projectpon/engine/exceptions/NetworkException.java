package projectpon.engine.exceptions;

import java.io.IOException;

public class NetworkException extends Exception {
	public NetworkException(IOException e) {
		super(e.getMessage());
	}
}
