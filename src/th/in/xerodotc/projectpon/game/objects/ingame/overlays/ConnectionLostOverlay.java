/**
 * ConnectionLostOverlay.java
 * 
 * An overlay UI for connection lost notification
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */
package th.in.xerodotc.projectpon.game.objects.ingame.overlays;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import th.in.xerodotc.projectpon.engine.GameFont;

public class ConnectionLostOverlay extends Overlay {
	private Font font = GameFont.getFont("advocut").
			deriveFont(Font.PLAIN, 32); // font for overlay
	
	/**
	 * Setup overlay
	 */
	public ConnectionLostOverlay() {
		super(256, 48);
	}
	
	/**
	 * Listen for user input and dismiss
	 */
	@Override
	public void eventPreUpdate() {
		boolean dismiss = input.isAnyMouseReleased() || input.isAnyKeyReleased();
		
		if (dismiss) {
			pscene.goToTitle();
		}
	}
	
	/**
	 * Draw the overlay
	 */
	@Override
	public void draw(Graphics2D canvas) {
		super.draw(canvas);
		canvas.setColor(Color.RED);
		drawCenteredString(canvas, font, "Connection lost!", height / 2, VALIGN_MIDDLE);
	}

}
