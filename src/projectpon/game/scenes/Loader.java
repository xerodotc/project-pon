/**
 * Loader.java
 * 
 * "Now loading..." scene (also load the rest of resources)
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import projectpon.engine.*;

public class Loader extends GameScene {
	
	private GameScene entryScene = null; // the scene to be displayed next
	private Runnable load; // loader runnable

	/**
	 * Initialize resource loader
	 * 
	 * @param entry		Entry scene
	 * @param load		Loader function
	 */
	public Loader(GameScene entry, Runnable load) {
		super();
		entryScene = entry;
		this.load = load;
	}

	/**
	 * Show "NOW LOADING..." text and load the resources
	 */
	@Override
	public void initialize() {
		GameEngine.disableExitOnClose(); // disable exit on close while loading
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
