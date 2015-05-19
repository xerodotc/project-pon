/**
 * ShadowBall.java
 * 
 * A class for a ball object without internal logic
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game.objects.ingame.shadow;

import th.in.xerodotc.projectpon.game.objects.ingame.Ball;

public class ShadowBall extends Ball {
	
	/**
	 * Initialize the ball
	 */
	public ShadowBall() {
		super();
		this.visible = false;
	}
	
	/**
	 * Toggle visibility
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
	 * Set ball visibility
	 * 
	 * @param visible	Visibility
	 */
	public void setVisibility(boolean visible) {
		this.visible = visible;
	}
}
