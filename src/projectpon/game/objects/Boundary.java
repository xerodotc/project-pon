package projectpon.game.objects;

import java.awt.Color;
import java.awt.Graphics2D;

import projectpon.engine.GameObject;
import projectpon.game.scenes.PongScene;

public class Boundary extends GameObject {
	protected PongScene pscene;
	
	public static final int HBORDER_THICKNESS = 16;
	
	public Boundary() {
		this.visible = true;
		this.z = 100;
	}
	
	@Override
	public void eventOnCreate() {
		if (scene instanceof PongScene) {
			pscene = (PongScene) scene;
		}
	}
	
	@Override
	public void draw(Graphics2D canvas) {
		canvas.setColor(Color.GRAY);
		canvas.fillRect(0, pscene.getTopBoundary() - 16,
				pscene.getWidth(), 16);
		canvas.fillRect(0, pscene.getBottomBoundary(),
				pscene.getWidth(), 16);
		canvas.drawLine(pscene.getWidth() / 2, pscene.getTopBoundary(),
				pscene.getWidth() / 2, pscene.getBottomBoundary());
	}
}
