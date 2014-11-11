package projectpon.game.objects.shadow;

import java.util.Map;

import projectpon.game.objects.Paddle;

public class ShadowPaddle extends Paddle {

	public ShadowPaddle(int x, int y, int side) {
		super(x, y, 0, side);
		this.setPlayerType(PLAYER_SHADOW);
	}

	@Override
	public void eventPreUpdate() {
		setVisibility();
	}
	
	@Override
	public void eventPostUpdate() {
		// do nothing
	}
	
	public void setCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setVisibility(boolean visible) {
		this.visible = visible;
	}
	
	public void setAllTimers(Map<Integer,Integer> t) {
		Integer sticky = t.get(STATUS_STICKY);
		Integer blind = t.get(STATUS_BLIND);
		Integer invert = t.get(STATUS_INVERT);
		
		if (sticky != null) {
			statusTimer[STATUS_STICKY] = sticky;
		}
		
		if (blind != null) {
			statusTimer[STATUS_BLIND] = blind;
		}
		
		if (invert != null) {
			statusTimer[STATUS_INVERT] = invert;
		}
	}
	
	public void setAllStatusesFlag(Map<Integer,Boolean> s) {
		Boolean wall = s.get(STATUS_WALL);
		Boolean sticky = s.get(STATUS_STICKY);
		Boolean blind = s.get(STATUS_BLIND);
		Boolean invert = s.get(STATUS_INVERT);
		
		if (wall != null) {
			this.wall = wall;
		}
		
		if (sticky != null) {
			this.sticky = sticky;
		}
		
		if (blind != null) {
			this.blind = blind;
		}
		
		if (invert != null) {
			this.invert = (invert) ? -1 : 1;
		}
	}
}
