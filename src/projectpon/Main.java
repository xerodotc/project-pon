package projectpon;

import java.util.Arrays;

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
				GameSound.loadMusic("title", "res/bgm/title.ogg");
				GameSound.loadMusic("ingame", "res/bgm/ingame.ogg");
				GameSound.loadSound("baap", "res/sfx/pong_baap.wav");
				GameSound.loadSound("beep", "res/sfx/pong_beep.wav");
				GameSound.loadSound("boop", "res/sfx/pong_boop.wav");
				GameImage.loadImage("item-expand", "res/img/item_expand.png");
				GameImage.loadImage("item-shrink", "res/img/item_shrink.png");
				GameImage.loadImage("item-wall", "res/img/item_wall.png");
				GameImage.loadImage("item-sticky", "res/img/item_sticky.png");
				GameImage.loadImage("item-blind", "res/img/item_blind.png");
				GameImage.loadImage("item-invert", "res/img/item_invert.png");
				GameImage.loadImage("help", "res/img/help.png");
				GameImage.loadImage("about", "res/img/about.png");
				Configuration.load();
				Configuration.validate(true);
				Configuration.save();
				GameSound.setGlobalEnabled(
						Configuration.getBoolean("soundOptions", "globalEnabled"));
				GameSound.setGlobalVolume(
						Configuration.getInt("soundOptions", "globalVolume"));
				GameSound.setMusicEnabled(
						Configuration.getBoolean("soundOptions", "musicEnabled"));
				GameSound.setMusicVolume(
						Configuration.getInt("soundOptions", "musicVolume"));
				GameSound.setSoundsEnabled(
						Configuration.getBoolean("soundOptions", "soundsEnabled"));
				GameSound.setSoundsVolume(
						Configuration.getInt("soundOptions", "soundsVolume"));
				SessionConfiguration.minimumWinScore = 
						Configuration.getInt("lastSession", "minPoints");
				SessionConfiguration.maximumWinScore = 
						Configuration.getInt("lastSession", "maxPoints");
				SessionConfiguration.minimumWinDiff = 
						Configuration.getInt("lastSession", "diffPoints");
			}
		};
		
		GameEngine.setResolution(800, 600);
		if (Arrays.asList(args).contains("--debug")) {
			GameEngine.setDebugOn();
		}
		GameEngine.start(new Loader(entryScene, load), 50, "Project Pon");
	}
}
