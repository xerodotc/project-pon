package projectpon.game.scenes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import projectpon.engine.*;
import projectpon.engine.exceptions.InvalidWindowSize;
import projectpon.game.Configuration;

public class Loader extends GameScene {
	
	private GameScene entryScene = null;

	public static void load() {
		GameSound.loadSound("baap", "res/sfx/pong_baap.wav");
		GameSound.loadSound("beep", "res/sfx/pong_beep.wav");
		GameSound.loadSound("boop", "res/sfx/pong_boop.wav");
		GameImage.loadImage("item-expand", "res/img/item_expand.png");
		GameImage.loadImage("item-shrink", "res/img/item_shrink.png");
		GameImage.loadImage("item-wall", "res/img/item_wall.png");
		GameImage.loadImage("item-sticky", "res/img/item_sticky.png");
		GameImage.loadImage("item-blind", "res/img/item_blind.png");
		GameImage.loadImage("item-invert", "res/img/item_invert.png");
		Configuration.load();
		Configuration.validate(true);
		Configuration.save();
		GameSound.setGlobalEnabled(Configuration.getBoolean("soundOptions", "globalEnabled"));
		GameSound.setGlobalVolume(Configuration.getInt("soundOptions", "globalVolume"));
	}
	
	public Loader(int width, int height, GameScene entry) throws InvalidWindowSize {
		super(width, height);
		entryScene = entry;
	}

	public Loader(GameScene entry) throws InvalidWindowSize {
		super();
		entryScene = entry;
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
					load();
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
