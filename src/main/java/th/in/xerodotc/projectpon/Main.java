/**
 * Main.java
 * 
 * A main class for launching the game.
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon;

import java.util.Arrays;
import java.util.List;

import th.in.xerodotc.projectpon.engine.*;
import th.in.xerodotc.projectpon.game.Configuration;
import th.in.xerodotc.projectpon.game.SessionConfiguration;
import th.in.xerodotc.projectpon.game.objects.ingame.Player;
import th.in.xerodotc.projectpon.game.scenes.Loader;
import th.in.xerodotc.projectpon.game.scenes.ShadowPongScene;
import th.in.xerodotc.projectpon.game.scenes.TitleScene;

public class Main {
	/**
	 * Main method
	 * 
	 * @param args		Command line options
	 */
	public static void main(String[] args) {
		// Initialize title scene
		GameScene entryScene = new TitleScene(true);
		
		/*
		 * Create a runnable for loading resources and initial
		 * configuration
		 */
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
		
		// Set game resolution
		GameEngine.setResolution(800, 600);
		
		/*
		 * Read and process command line options
		 * 
		 * AVAILABLE OPTIONS
		 * 		--debug
		 * 			Display debug informations
		 * 		
		 * 		--save-replay FILE
		 * 			Save latest game session replay to FILE
		 * 
		 * 		--load-replay FILE
		 * 			Launch the game and load replay from FILE
		 */
		List<String> argsList = Arrays.asList(args);
		if (argsList.contains("--debug")) {
			GameEngine.setDebugOn();
		}
		int iSR = argsList.indexOf("--save-replay");
		int iLR = argsList.indexOf("--load-replay");
		if (iSR > -1) {
			int j = iSR + 1;
			if (j < args.length) {
				SessionConfiguration.saveReplayFile = args[j];
			}
		} else if (iLR > -1) {
			int j = iLR + 1;
			if (j < args.length) {
				ShadowPongScene pscene = new ShadowPongScene();
				pscene.setLeftPlayer(Player.PLAYER_REPLAY, false);
				pscene.setRightPlayer(Player.PLAYER_REPLAY, false);
				((ShadowPongScene) pscene).setReplayFile(args[j]);
				entryScene = pscene;
			}
		}
		
		// Start the game engine!
		GameEngine.start(new Loader(entryScene, load), 50, "Project Pon");
	}
}
