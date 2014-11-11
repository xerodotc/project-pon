package projectpon;

import projectpon.engine.*;
import projectpon.game.Configuration;
import projectpon.game.SessionConfiguration;
import projectpon.game.scenes.Loader;
import projectpon.game.scenes.TitleScene;

public class Main {
	public static void main(String[] args) {
		GameScene entryScene = new TitleScene(true);
		Runnable load = new Runnable() {
			@Override
			public void run() {
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
				GameSound.setGlobalEnabled(
						Configuration.getBoolean("soundOptions", "globalEnabled"));
				GameSound.setGlobalVolume(
						Configuration.getInt("soundOptions", "globalVolume"));
				GameSound.setMusicEnabled(
						Configuration.getBoolean("soundOptions", "globalEnabled"));
				GameSound.setMusicVolume(
						Configuration.getInt("soundOptions", "globalVolume"));
				GameSound.setSoundsEnabled(
						Configuration.getBoolean("soundOptions", "globalEnabled"));
				GameSound.setSoundsVolume(
						Configuration.getInt("soundOptions", "globalVolume"));
				SessionConfiguration.minimumWinScore = 
						Configuration.getInt("lastSession", "minPoints");
				SessionConfiguration.maximumWinScore = 
						Configuration.getInt("lastSession", "maxPoints");
				SessionConfiguration.minimumWinDiff = 
						Configuration.getInt("lastSession", "diffPoints");
			}
		};
		
		GameEngine.setResolution(800, 600);
		GameEngine.setDebugOn();
		GameEngine.start(new Loader(entryScene, load), 50, "Project Pon");
	}
}
