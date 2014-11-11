package projectpon.game.objects.ingame.shadow;

import projectpon.game.objects.ingame.Wall;

public class ShadowWall extends Wall {
	public ShadowWall(int x, int y, int side) {
		super(x, y, side);
	}
	
	@Override
	public void eventPostUpdate() {
		super.eventPreUpdate();
	}
}
