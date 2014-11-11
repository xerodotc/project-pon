package projectpon.game.objects.ingame.shadow;

import projectpon.game.objects.ingame.Ball;

public class ShadowBall extends Ball {
	public ShadowBall() {
		super();
		this.visible = false;
	}
	
	@Override
	public void eventPreUpdate() {
		setVisibility();
	}
	
	@Override
	public void eventPostUpdate() {
		// do nothing
	}
	
	public void setVisibility(boolean visible) {
		this.visible = visible;
	}
}
