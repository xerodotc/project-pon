/**
 * AboutScene.java
 * 
 * A scene for showing "about" page
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game.scenes;

import java.awt.Graphics2D;

import th.in.xerodotc.projectpon.engine.GameEngine;
import th.in.xerodotc.projectpon.engine.GameImage;
import th.in.xerodotc.projectpon.engine.GameObject;
import th.in.xerodotc.projectpon.engine.GameScene;

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
