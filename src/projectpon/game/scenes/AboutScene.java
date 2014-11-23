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
import projectpon.game.Configuration;

public class AboutScene extends GameScene {

	@Override
	public void initialize() {
		this.objectAdd(new GameObject() {
			@Override
			public void eventPreUpdate() {
				this.visible = true;
				
				// dismiss and return to title, if button pressed
				switch (Configuration.get("inputPrimaryPlayer", "type")) {
				case "mouse":
					if (input.isMouseReleased(
							Configuration.getInt("inputPrimaryPlayer", "mbLaunch"))) {
						GameEngine.setScene(new TitleScene());
					}
					break;
					
				case "keyboard":
					if (input.isKeyReleased(
							Configuration.getInt("inputPrimaryPlayer", "keyLaunch"))) {
						GameEngine.setScene(new TitleScene());
					}
					break;
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
