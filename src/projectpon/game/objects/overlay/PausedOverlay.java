package projectpon.game.objects.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import projectpon.engine.GameFont;
import projectpon.game.Configuration;

public class PausedOverlay extends Overlay {
	private Font largeFont = GameFont.getFont("advocut").
			deriveFont(Font.BOLD, 64);
	private Font choiceFont = GameFont.getFont("advocut").
			deriveFont(Font.PLAIN, 24);
	
	private static final int RETURN_TO_GAME = 0;
	private static final int RETURN_TO_TITLE = 1;
	
	private Rectangle[] choiceRect = new Rectangle[2];
	private Color[] choiceColor = {Color.BLACK, Color.BLACK};
	private int selectedOption = 0;
	
	public PausedOverlay() {
		super(300, 180);
		
		this.visible = false;
	}
	
	@Override
	public void eventPreUpdate() {
		if (pscene.controller.isUserPaused()) {
			this.visible = true;
			
			boolean select = false;
			
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
				select = input.isMousePressed(
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
				
				select = input.isKeyPressed(
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
	
	@Override
	public void eventPostUpdate() {
		if (pscene.controller.isUserPaused()) {
			this.visible = true;
		} else {
			this.visible = false;
		}
	}
	
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
