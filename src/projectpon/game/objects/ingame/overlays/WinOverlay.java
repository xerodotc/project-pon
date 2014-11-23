/**
 * WinOverlay.java
 * 
 * A class for winner notification overlay
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.ingame.overlays;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import projectpon.engine.GameFont;
import projectpon.game.Configuration;

public class WinOverlay extends Overlay {
	private Font font = GameFont.getFont("advocut").
			deriveFont(Font.PLAIN, 64); // font
	private String text; // text
	
	/**
	 * Initialize
	 * 
	 * @param text	A string to display
	 */
	public WinOverlay(String text) {
		super(512, 96);
		this.text = text;
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
	 * Draw an overlay
	 */
	@Override
	public void draw(Graphics2D canvas) {
		super.draw(canvas);
		canvas.setColor(Color.BLACK);
		drawCenteredString(canvas, font, text, height / 2, VALIGN_MIDDLE);
	}

}
