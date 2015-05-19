/**
 * GameSound.java
 * 
 * A class for managing in-game sounds
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.engine;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import kuusisto.tinysound.*;

public final class GameSound {
	// sounds list map
	private static Map<String, Sound> soundsList = new HashMap<String, Sound>();
	// music list map
	private static Map<String, Music> musicList = new HashMap<String, Music>();
	
	/*
	 * Current BGM informations
	 */
	private static Music currentBGM = null;
	private static String currentBGMName = null;
	
	/*
	 * Volume settings
	 */
	private static boolean globalEnabled = true;
	private static int globalVolume = 100;
	private static boolean musicEnabled = true;
	private static int musicVolume = 100;
	private static boolean soundsEnabled = true;
	private static int soundsVolume = 100;
	
	/**
	 * Prevent instance initialization
	 */
	private GameSound() {
	}
	
	/**
	 * Initialize sounds system
	 */
	public static void init() {
		TinySound.init();
	}
	
	/**
	 * Load sound from file
	 * 
	 * @param soundName		Sound name identifier
	 * @param resPath		Sound resource path
	 */
	public static void loadSound(String soundName, String resPath) {
		if (soundsList.containsKey(soundName)) {
			soundsList.get(soundName).unload();
		}
		// Check for internal resource first then external resource
		Sound sound = TinySound.loadSound(resPath);
		if (sound == null) {
			sound = TinySound.loadSound(new File(resPath));
		}
		soundsList.put(soundName, sound);
		if (GameEngine.isDebugOn()) {
			System.out.println("soundResourceLoaded: " + soundName + " : " + resPath);
		}
	}
	
	/**
	 * Play a sound
	 * 
	 * @param soundName		Sound name identifier
	 */
	public static void playSound(String soundName) {
		if (!globalEnabled || !soundsEnabled) {
			return;
		}
		
		Sound snd = soundsList.get(soundName);
		if (snd != null) {
			snd.play(soundsVolumeFactor());
			if (GameEngine.isDebugOn()) {
				System.out.println("soundPlayed: " + soundName);
			}
		}
	}
	
	/**
	 * Load music from file
	 * 
	 * @param musicName		Music name identifier
	 * @param resPath		Music resource path
	 */
	public static void loadMusic(String musicName, String resPath) {
		if (musicList.containsKey(musicName)) {
			musicList.get(musicName).unload();
		}
		// Check for internal resource first then external resource
		Music music = TinySound.loadMusic(resPath);
		if (music == null) {
			music = TinySound.loadMusic(new File(resPath));
		}
		musicList.put(musicName, music);
		if (GameEngine.isDebugOn()) {
			System.out.println("musicResourceLoaded: " + musicName + " : " + resPath);
		}
	}
	
	/**
	 * Play a music
	 * 
	 * @param musicName		Music name identifier
	 */
	public static void playMusic(String musicName) {
		if (!globalEnabled || !musicEnabled) {
			return;
		}
		
		// Don't replay the music, if it's the same BGM
		if (musicName.equals(currentBGMName)) {
			return;
		}
		
		Music bgm = musicList.get(musicName);
		if (bgm == null) {
			return;
		}
		
		if (currentBGM != null) {
			currentBGM.stop();
		}
		
		currentBGM = bgm;
		currentBGMName = musicName;
		currentBGM.play(true, musicVolumeFactor());
	}
	
	/**
	 * Set global sounds volume
	 * 
	 * @param volume	Volume
	 */
	public static void setGlobalVolume(int volume) {
		if (volume < 0) {
			volume = 0;
		}
		if (volume > 100) {
			volume = 100;
		}
		globalVolume = volume;
		if (currentBGM != null) {
			currentBGM.setVolume(musicVolumeFactor());
		}
	}
	
	/**
	 * Set global sounds enabled flag
	 * 
	 * @param enable	Enabled?
	 */
	public static void setGlobalEnabled(boolean enable) {
		globalEnabled = enable;
		if (!globalEnabled && currentBGM != null) {
			currentBGM.stop();
			currentBGM = null;
			currentBGMName = null;
		}
	}
	
	/**
	 * Get global volume factor
	 * 
	 * @return Global volume factor
	 */
	private static double globalVolumeFactor() {
		return globalVolume / 100.0;
	}
	
	/**
	 * Set music volume
	 * 
	 * @param volume	Volume
	 */
	public static void setMusicVolume(int volume) {
		if (volume < 0) {
			volume = 0;
		}
		if (volume > 100) {
			volume = 100;
		}
		musicVolume = volume;
		if (currentBGM != null) {
			currentBGM.setVolume(musicVolumeFactor());
		}
	}
	
	/**
	 * Set music enabled flag
	 * 
	 * @param enable	Enabled?
	 */
	public static void setMusicEnabled(boolean enable) {
		musicEnabled = enable;
		if (!musicEnabled && currentBGM != null) {
			currentBGM.stop();
			currentBGM = null;
			currentBGMName = null;
		}
	}
	
	/**
	 * Get music volume factor (also factored by global volume factor)
	 * 
	 * @return Music volume factor
	 */
	private static double musicVolumeFactor() {
		return globalVolumeFactor() * (musicVolume / 100.0);
	}
	
	/**
	 * Set sound effects volume
	 * 
	 * @param volume	Volume
	 */
	public static void setSoundsVolume(int volume) {
		if (volume < 0) {
			volume = 0;
		}
		if (volume > 100) {
			volume = 100;
		}
		soundsVolume = volume;
	}
	
	/**
	 * Set sound effects enabled flag
	 * 
	 * @param enable	Enabled?
	 */
	public static void setSoundsEnabled(boolean enable) {
		soundsEnabled = enable;
	}
	
	/**
	 * Get sound effects volume factor (also factored by global volume factor)
	 * 
	 * @return Sound effects volume factor
	 */
	private static double soundsVolumeFactor() {
		return globalVolumeFactor() * (soundsVolume / 100.0);
	}
	
	/**
	 * Is background music playing?
	 * 
	 * @return	True if background music is playing
	 */
	public static boolean isBGMPlaying() {
		return (currentBGM != null && currentBGM.playing());
	}
}
