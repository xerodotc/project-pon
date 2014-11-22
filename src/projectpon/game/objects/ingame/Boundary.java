/**
 * Boundary.java
 * 
 * A class for drawing field boundary
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.ingame;

import java.awt.Color;
import java.awt.Graphics2D;

import projectpon.engine.GameObject;
import projectpon.game.scenes.PongScene;

public class Boundary extends GameObject {
	protected PongScene pscene; // the PongScene
	
	public static final int HBORDER_THICKNESS = 16; // horizontal border thickness
	
	/**
	 * Initialize the boundary
	 */
	public Boundary() {
		this.visible = true;
		this.z = 100; // send to back
	}
	
	/**
	 * Assign the PongScene
	 */
	@Override
	public void eventOnCreate() {
		if (scene instanceof PongScene) {
			pscene = (PongScene) scene;
		}
	}
	
	/**
	 * Draw the boundary
	 */
	@Override
	public void draw(Graphics2D canvas) {
		canvas.setColor(Color.GRAY);
		// top boundary
		canvas.fillRect(0, pscene.getTopBoundary() - 16,
				pscene.getWidth(), 16);
		// bottom boundary
		canvas.fillRect(0, pscene.getBottomBoundary(),
				pscene.getWidth(), 16);
		// center separator
		canvas.drawLine(pscene.getWidth() / 2, pscene.getTopBoundary(),
				pscene.getWidth() / 2, pscene.getBottomBoundary());
	}
}
