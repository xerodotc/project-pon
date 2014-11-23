/**
 * ShadowPlayer.java
 * 
 * A class for player/paddle object without internal logic
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.ingame.shadow;

import java.util.Map;

import projectpon.game.objects.ingame.Player;

public class ShadowPlayer extends Player {

	/**
	 * Initialize the player
	 * 
	 * @param x		x-position
	 * @param y		y-position
	 * @param side	Player's side
	 */
	public ShadowPlayer(int x, int y, int side) {
		super(x, y, 0, side);
		this.setPlayerType(PLAYER_SHADOW);
	}

	/**
	 * Set player visibility
	 */
	@Override
	public void eventPreUpdate() {
		setVisibility();
	}
	
	/**
	 * Hide inherited post-update code
	 */
	@Override
	public void eventPostUpdate() {
		// do nothing
	}
	
	/**
	 * Set player coordinate
	 * 
	 * @param x		x-position
	 * @param y		y-position
	 */
	public void setCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Set player visibility
	 * 
	 * @param visible	Visibility
	 */
	public void setVisibility(boolean visible) {
		this.visible = visible;
	}
	
	/**
	 * Set all status timers
	 * 
	 * @param t		A map of timers
	 */
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
	
	/**
	 * Set player all statuses flag
	 * 
	 * @param s		A map of statuses flag
	 */
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
