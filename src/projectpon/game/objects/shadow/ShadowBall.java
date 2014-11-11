package projectpon.game.objects.shadow;

import projectpon.game.objects.Ball;

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
