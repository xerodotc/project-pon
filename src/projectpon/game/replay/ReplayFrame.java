package projectpon.game.replay;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ReplayFrame implements Serializable {
	private static final long serialVersionUID = -1419324687955795924L;
	
	public int playerLeftX;
	public int playerLeftY;
	public int playerLeftSize;
	public Map<Integer,Boolean> playerLeftStatuses;
	public Map<Integer,Integer> playerLeftStatusesTimer;
	public Point2D playerRightCoordinate;
	public int playerRightX;
	public int playerRightY;
	public int playerRightSize;
	public Map<Integer,Boolean> playerRightStatuses;
	public Map<Integer,Integer> playerRightStatusesTimer;
	public int ballX;
	public int ballY;
	public int scoreLeft;
	public int scoreRight;
	public List< Map<String,Integer> > fieldItems;
	public String winner;
	public String sound;
}
