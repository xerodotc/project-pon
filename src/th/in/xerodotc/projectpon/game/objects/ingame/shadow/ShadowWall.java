/**
 * ShadowWall.java
 * 
 * A class for wall object without internal logic
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */
package th.in.xerodotc.projectpon.game.objects.ingame.shadow;

import th.in.xerodotc.projectpon.game.objects.ingame.Wall;

public class ShadowWall extends Wall {
	/**
	 * Initialize
	 * 
	 * @param x		x-position
	 * @param y		y-position
	 * @param side	Wall's side
	 */
	public ShadowWall(int x, int y, int side) {
		super(x, y, side);
	}
	
	/**
	 * Hide inherited post-update code (and redirect to pre-update code)
	 */
	@Override
	public void eventPostUpdate() {
		super.eventPreUpdate();
	}
}
