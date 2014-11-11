package projectpon.game.objects.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import projectpon.engine.GameFont;
import projectpon.game.Configuration;

public class ConnectionLostOverlay extends Overlay {
	private Font font = GameFont.getFont("advocut").
			deriveFont(Font.PLAIN, 32);
	
	public ConnectionLostOverlay() {
		super(256, 48);
	}
	
	@Override
	public void eventPreUpdate() {
		boolean dismiss = false;
		switch (Configuration.get("inputPrimaryPlayer", "type")) {
		case "mouse":
			dismiss = input.isMousePressed(
					Configuration.getInt("inputPrimaryPlayer", "mbLaunch"));
			break;
			
		case "keyboard":
			dismiss = input.isKeyPressed(
					Configuration.getInt("inputPrimaryPlayer", "keyLaunch"));
			break;
		}
		
		if (dismiss) {
			pscene.goToTitle();
		}
	}
	
	@Override
	public void draw(Graphics2D canvas) {
		super.draw(canvas);
		canvas.setColor(Color.RED);
		drawCenteredString(canvas, font, "Connection lost!", height / 2, VALIGN_MIDDLE);
	}

}
