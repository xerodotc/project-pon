package projectpon.game.replay;

import java.io.Serializable;
import java.util.LinkedList;

public class ReplayData implements Serializable {
	private static final long serialVersionUID = 8766324754137468875L;
	
	public int version;
	private LinkedList<ReplayFrame> frames;
	
	public void appendFrame(ReplayFrame frame) {
		if (frames == null) {
			frames = new LinkedList<ReplayFrame>();
		}
		frames.addLast(frame);
	}
	
	public ReplayFrame popNextFrame() {
		return frames.removeFirst();
	}
	
	public boolean isEOF() {
		return frames.isEmpty();
	}
}
