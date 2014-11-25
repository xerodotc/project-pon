/**
 * AboutScene.java
 * 
 * A scene for showing "about" page
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.scenes;

import java.awt.Graphics2D;

import projectpon.engine.GameEngine;
import projectpon.engine.GameImage;
import projectpon.engine.GameObject;
import projectpon.engine.GameScene;

public class AboutScene extends GameScene {

	@Override
	public void initialize() {
		this.objectAdd(new GameObject() {
			@Override
			public void eventPreUpdate() {
				this.visible = true;
				
				// dismiss and return to title, if button pressed
				if (input.isAnyMouseReleased() || input.isAnyKeyReleased()) {
					GameEngine.setScene(new TitleScene());
				}
			}
			
			@Override
			public void draw(Graphics2D canvas) {
				// draw "about" image
				canvas.drawImage(GameImage.getImage("about"), null, 0, 0);
			}
		});
	}

}
