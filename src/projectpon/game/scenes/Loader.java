package projectpon.game.scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import projectpon.engine.*;
import projectpon.game.Configuration;

public class Loader extends GameScene {
	
	private GameScene entryScene = null;
	private Runnable load;

	public Loader(GameScene entry, Runnable load) {
		super();
		entryScene = entry;
		this.load = load;
	}

	@Override
	public void initialize() {
		GameEngine.disableExitOnClose();
		GameFont.loadFont("advocut", "res/fonts/advocut.ttf");
		this.objectAdd(new GameObject() {
			private boolean drawed = false;
			private Font font = GameFont.getFont("advocut").
					deriveFont(Font.PLAIN, 64);
			
			@Override
			public void eventPostUpdate() {
				this.visible = true;
				if (drawed) {
					load.run();
					GameEngine.enableExitOnClose();
					GameEngine.setScene(entryScene);
				}
			}
			
			@Override
			public void draw(Graphics2D canvas) {
				canvas.setColor(Color.WHITE);
				canvas.setFont(font);
				String text = "NOW LOADING...";
				int textWidth = canvas.getFontMetrics().
						stringWidth(text);
				canvas.drawString(text, scene.getWidth() - 16 - textWidth,
						scene.getHeight() - 16);
				drawed = true;
			}
		});
	}
}
