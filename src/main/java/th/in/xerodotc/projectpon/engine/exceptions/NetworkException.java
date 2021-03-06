/**
 * NetworkException.java
 * 
 * A wrapper for network related exception
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.engine.exceptions;

import java.io.IOException;

public class NetworkException extends Exception {
	private static final long serialVersionUID = 5326560223406632826L;

	/**
	 * NetworkException constructor
	 * 
	 * @param e		IOException
	 */
	public NetworkException(IOException e) {
		super(e.getMessage());
	}
}
