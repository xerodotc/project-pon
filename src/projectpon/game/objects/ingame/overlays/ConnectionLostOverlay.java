/**
 * ConnectionLostOverlay.java
 * 
 * An overlay UI for connection lost notification
 * 
 * @author Visatouch Deeying [5631083121]
 */
package projectpon.game.objects.ingame.overlays;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import projectpon.engine.GameFont;
import projectpon.game.Configuration;

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
		boolean dismiss = false;
		switch (Configuration.get("inputPrimaryPlayer", "type")) {
		case "mouse":
			dismiss = input.isMouseReleased(
					Configuration.getInt("inputPrimaryPlayer", "mbLaunch"));
			break;
			
		case "keyboard":
			dismiss = input.isKeyReleased(
					Configuration.getInt("inputPrimaryPlayer", "keyLaunch"));
			break;
		}
		
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
