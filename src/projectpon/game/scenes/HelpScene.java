package projectpon.game.scenes;

import java.awt.Graphics2D;

import projectpon.engine.GameEngine;
import projectpon.engine.GameImage;
import projectpon.engine.GameObject;
import projectpon.engine.GameScene;
import projectpon.game.Configuration;

public class HelpScene extends GameScene {

	@Override
	public void initialize() {
		this.objectAdd(new GameObject() {
			@Override
			public void eventPreUpdate() {
				this.visible = true;
				
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
				canvas.drawImage(GameImage.getImage("help"), null, 0, 0);
			}
		});
	}

}
