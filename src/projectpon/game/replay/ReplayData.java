/**
 * ReplayData.java
 * 
 * A class for storing the whole replay data
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.replay;

import java.io.Serializable;
import java.util.LinkedList;

public class ReplayData implements Serializable {
	private static final long serialVersionUID = 8766324754137468875L;
	
	public int version; // replay version
	private LinkedList<ReplayFrame> frames; // linked list of frames
	
	/**
	 * Append the replay frame
	 * 
	 * @param frame Replay frame
	 */
	public void appendFrame(ReplayFrame frame) {
		if (frames == null) {
			frames = new LinkedList<ReplayFrame>();
		}
		frames.addLast(frame);
	}
	
	/**
	 * Pop/read the current first frame
	 * 
	 * @return The replay frame
	 */
	public ReplayFrame popNextFrame() {
		return frames.removeFirst();
	}
	
	/**
	 * Is end of replay data
	 * 
	 * @return True if end of replay data
	 */
	public boolean isEOF() {
		return frames.isEmpty();
	}
}
