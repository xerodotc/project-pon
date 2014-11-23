/**
 * PausedOverlay.java
 * 
 * A class for pause menu overlay
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.ingame.overlays;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import projectpon.engine.GameFont;
import projectpon.game.Configuration;

public class PausedOverlay extends Overlay {
	private Font largeFont = GameFont.getFont("advocut").
			deriveFont(Font.BOLD, 64); // title font
	private Font choiceFont = GameFont.getFont("advocut").
			deriveFont(Font.PLAIN, 24); // choice font
	
	/*
	 * Define menu ID
	 */
	private static final int RETURN_TO_GAME = 0;
	private static final int RETURN_TO_TITLE = 1;
	
	// choice rectangle bound
	private Rectangle[] choiceRect = new Rectangle[2];
	// choice current text color
	private Color[] choiceColor = {Color.BLACK, Color.BLACK};
	private int selectedOption = 0; // selected choice
	
	/**
	 * Initialize
	 */
	public PausedOverlay() {
		super(300, 180);
		
		this.visible = false;
	}
	
	/**
	 * Check for pausing status and show pause menu
	 */
	@Override
	public void eventPreUpdate() {
		if (pscene.controller.isUserPaused()) {
			this.visible = true;
			
			boolean select = false;
			
			/*
			 * Listen for user input and choice selection
			 */
			switch (Configuration.get("inputPrimaryPlayer", "type")) {
			case "mouse":
				selectedOption = -1;
				if (choiceRect[RETURN_TO_GAME] != null && choiceRect[RETURN_TO_GAME].
						contains(input.getMouseCoordinate())) {
					selectedOption = RETURN_TO_GAME;
				} else if (choiceRect[RETURN_TO_GAME] != null && choiceRect[RETURN_TO_TITLE].
						contains(input.getMouseCoordinate())) {
					selectedOption = RETURN_TO_TITLE;
				}
				select = input.isMouseReleased(
						Configuration.getInt("inputPrimaryPlayer", "mbLaunch"));
				break;
				
			case "keyboard":
				if (input.isKeyPressed(
						Configuration.getInt("inputPrimaryPlayer", "keyUp"))) {
					selectedOption--;
				}
				if (input.isKeyPressed(
						Configuration.getInt("inputPrimaryPlayer", "keyDown"))) {
					selectedOption++;
				}
				
				if (selectedOption < 0) {
					selectedOption = choiceRect.length - 1;
				}
				if (selectedOption >= choiceRect.length) {
					selectedOption = 0;
				}
				
				select = input.isKeyReleased(
						Configuration.getInt("inputPrimaryPlayer", "keyLaunch"));
				break;
			}
			
			for (int i = 0; i < choiceColor.length; i++) {
				if (i == selectedOption) {
					choiceColor[i] = Color.RED;
				} else {
					choiceColor[i] = Color.BLACK;
				}
			}
			
			if (select) {
				// choice actions
				switch (selectedOption) {
				case RETURN_TO_GAME:
					pscene.controller.unpause();
					break;
					
				case RETURN_TO_TITLE:
					pscene.goToTitle();
					break;
				}
			}
		} else {
			this.visible = false;
		}
	}
	
	/**
	 * Hide or unhide the menu
	 */
	@Override
	public void eventPostUpdate() {
		if (pscene.controller.isUserPaused()) {
			this.visible = true;
		} else {
			this.visible = false;
		}
	}
	
	/**
	 * Draw the menu
	 */
	@Override
	public void draw(Graphics2D canvas) {
		super.draw(canvas);
		canvas.setColor(Color.BLACK);
		drawCenteredString(canvas, largeFont, "PAUSED", 24, VALIGN_TOP);
		
		canvas.setColor(choiceColor[RETURN_TO_GAME]);
		choiceRect[RETURN_TO_GAME] = drawCenteredString(canvas, choiceFont, "Return to game", height - 16 - 32, VALIGN_BOTTOM);
		canvas.setColor(choiceColor[RETURN_TO_TITLE]);
		choiceRect[RETURN_TO_TITLE] = drawCenteredString(canvas, choiceFont, "Return to title", height - 16, VALIGN_BOTTOM);
	}
}
