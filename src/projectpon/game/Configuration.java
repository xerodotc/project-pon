/**
 * Configuration.java
 * 
 * A class for managing persistent game configurations
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import org.ini4j.*;

public final class Configuration {
	private static Ini ini = null;
	private static Ini defaultIni = new Ini();
	private static String iniFileName = "config.ini";
	
	static {
		/*
		 * Default settings for input configuration
		 */
		defaultIni.add("inputPrimaryPlayer", "type", "mouse");
		defaultIni.add("inputPrimaryPlayer", "keyUp", "" + KeyEvent.VK_UP);
		defaultIni.add("inputPrimaryPlayer", "keyDown", "" + KeyEvent.VK_DOWN);
		defaultIni.add("inputPrimaryPlayer", "keyLaunch", "" + KeyEvent.VK_SPACE);
		defaultIni.add("inputPrimaryPlayer", "keySpeed", "10");
		defaultIni.add("inputPrimaryPlayer", "mbLaunch", "" + MouseEvent.BUTTON1);
		defaultIni.add("inputSecondaryPlayer", "type", "keyboard");
		defaultIni.add("inputSecondaryPlayer", "keyUp", "" + KeyEvent.VK_UP);
		defaultIni.add("inputSecondaryPlayer", "keyDown", "" + KeyEvent.VK_DOWN);
		defaultIni.add("inputSecondaryPlayer", "keyLaunch", "" + KeyEvent.VK_SPACE);
		defaultIni.add("inputSecondaryPlayer", "keySpeed", "10");
		defaultIni.add("inputSecondaryPlayer", "mbLaunch", "" + MouseEvent.BUTTON1);
		
		/*
		 * Default settings for computer player
		 */
		defaultIni.add("aiOptions", "speed", "15");
		defaultIni.add("aiOptions", "cheat", "false");
		
		/*
		 * Default settings for network configuration
		 */
		defaultIni.add("networkOptions", "localPort", "10215");
		defaultIni.add("networkOptions", "lastRemoteServerAddr", "");
		defaultIni.add("networkOptions", "lastRemoteServerPort", "10215");
		
		/*
		 * Default settings for sound
		 */
		defaultIni.add("soundOptions", "globalEnabled", "true");
		defaultIni.add("soundOptions", "globalVolume", "50");
		defaultIni.add("soundOptions", "musicEnabled", "true");
		defaultIni.add("soundOptions", "musicVolume", "100");
		defaultIni.add("soundOptions", "soundsEnabled", "true");
		defaultIni.add("soundOptions", "soundsVolume", "50");
		
		/*
		 * Default settings for initial session configuration
		 */
		defaultIni.add("lastSession", "minPoints", "5");
		defaultIni.add("lastSession", "maxPoints", "9");
		defaultIni.add("lastSession", "diffPoints", "2");
	}
	
	/**
	 * Is configuration file loaded
	 * 
	 * @return	True if loaded
	 */
	public static boolean isLoaded() {
		return ini != null;
	}
	
	/**
	 * Load the configurations from default file
	 */
	public static void load() {
		load(iniFileName);
	}
	
	/**
	 * Load the configuration from specified file
	 * If not available, load default configuration
	 * 
	 * @param iniFileName		Configuration file name
	 */
	public static void load(String iniFileName) {
		try {
			ini = new Ini(new File(iniFileName));
		} catch (IOException e) {
			try {
				defaultIni.store(new File(iniFileName));
				ini = new Ini(new File(iniFileName));
			} catch (IOException e1) {
				e1.printStackTrace();
				ini = defaultIni;
			}
		}
	}
	
	/**
	 * Save the configuration
	 */
	public static void save() {
		try {
			if (ini != defaultIni) {
				ini.store();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the value of specified key
	 * 
	 * @param section		Configuration section
	 * @param key			Configuration key
	 * @return	Value as string
	 */
	public static String get(String section, String key) {
		if (ini.get(section, key) == null) {
			if (defaultIni.get(section, key) != null) {
				ini.add(section, key, defaultIni.get(section, key));
			}
		}
		return ini.get(section, key);
	}
	
	/**
	 * Get the value of specified key as integer
	 * 
	 * @param section		Configuration section
	 * @param key			Configuration key
	 * @return	Value as integer
	 */
	public static int getInt(String section, String key) {
		try {
			return Integer.parseInt(get(section, key));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Get the value of specified key as boolean
	 * 
	 * @param section		Configuration section
	 * @param key			Configuration key
	 * @return	Value as boolean
	 */
	public static boolean getBoolean(String section, String key) {
		return Boolean.parseBoolean(get(section, key));
	}
	
	/**
	 * Set the value for specified key
	 * 
	 * @param section		Configuration section
	 * @param key			Configuration key
	 * @param value			Configuration value
	 */
	public static void set(String section, String key, String value) {
		ini.put(section, key, value);
	}
	
	/**
	 * Set the value for specified key
	 * 
	 * @param section		Configuration section
	 * @param key			Configuration key
	 * @param value			Configuration value
	 */
	public static void set(String section, String key, int value) {
		set(section, key, Integer.toString(value));
	}
	
	/**
	 * Set the value for specified key
	 * 
	 * @param section		Configuration section
	 * @param key			Configuration key
	 * @param value			Configuration value
	 */
	public static void set(String section, String key, boolean value) {
		set(section, key, Boolean.toString(value));
	}
	
	/**
	 * Validate the configuration
	 * 
	 * @return True if configuration is valid
	 */
	public static boolean validate() {
		return validate(false);
	}
	
	/**
	 * Validate the configuration
	 * 
	 * @param	resetOnInvalid		Automatically reset value on invalid
	 * @return True if configuration is valid
	 */
	public static boolean validate(boolean resetOnInvalid) {
		boolean valid = true;
		
		if (!get("inputPrimaryPlayer", "type").equals("mouse") &&
				!get("inputPrimaryPlayer", "type").equals("keyboard")) {
			valid = false;
			if (resetOnInvalid) {
				reset("inputPrimaryPlayer", "type");
			}
		}
		
		if (isOutOfBound("inputPrimaryPlayer", "keySpeed")) {
			valid = false;
			if (resetOnInvalid) {
				reset("inputPrimaryPlayer", "keySpeed");
			}
		}
		
		if (!get("inputSecondaryPlayer", "type").equals("mouse") &&
				!get("inputSecondaryPlayer", "type").equals("keyboard")) {
			valid = false;
			if (resetOnInvalid) {
				reset("inputSecondaryPlayer", "type");
			}
		}
		
		if (isOutOfBound("inputSecondaryPlayer", "keySpeed")) {
			valid = false;
			if (resetOnInvalid) {
				reset("inputSecondaryPlayer", "keySpeed");
			}
		}
		
		if (isOutOfBound("aiOptions", "speed")) {
			valid = false;
			if (resetOnInvalid) {
				reset("aiOptions", "speed");
			}
		}
		
		if (isOutOfBound("networkOptions", "localPort")) {
			valid = false;
			if (resetOnInvalid) {
				reset("networkOptions", "localPort");
			}
		}
		
		if (isOutOfBound("networkOptions", "lastRemoteServerPort")) {
			valid = false;
			if (resetOnInvalid) {
				reset("networkOptions", "lastRemoteServerPort");
			}
		}
		
		if (isOutOfBound("soundOptions", "globalVolume")) {
			valid = false;
			if (resetOnInvalid) {
				reset("soundOptions", "globalVolume");
			}
		}
		
		if (isOutOfBound("soundOptions", "musicVolume")) {
			valid = false;
			if (resetOnInvalid) {
				reset("soundOptions", "musicVolume");
			}
		}
		
		if (isOutOfBound("soundOptions", "soundsVolume")) {
			valid = false;
			if (resetOnInvalid) {
				reset("soundOptions", "soundsVolume");
			}
		}
		
		if (isOutOfBound("lastSession", "minPoints") || 
				isOutOfBound("lastSession", "maxPoints") ||
				isOutOfBound("lastSession", "diffPoints")) {
			valid = false;
			if (resetOnInvalid) {
				reset("lastSession", "minPoints");
				reset("lastSession", "maxPoints");
				reset("lastSession", "diffPoints");
			}
		}
		
		int minPoints = getInt("lastSession", "minPoints");
		int maxPoints = getInt("lastSession", "maxPoints");
		int diffPoints = getInt("lastSession", "minPoints");
		
		boolean validCondition1 = (minPoints <= maxPoints);
		boolean validCondition2 = (diffPoints <= minPoints);
		
		if (!(validCondition1 && validCondition2)) {
			valid = false;
			if (resetOnInvalid) {
				reset("lastSession", "minPoints");
				reset("lastSession", "maxPoints");
				reset("lastSession", "diffPoints");
			}
		}
		
		return valid;
	}
	
	/**
	 * Get the lower bound of specified configuration key
	 * 
	 * @param section		Configuration section
	 * @param key			Configuration key
	 * @return Lower bound of specified configuration key
	 */
	public static int getMinBound(String section, String key) {
		switch (section) {
		case "inputPrimaryPlayer":
		case "inputSecondaryPlayer":
			switch (key) {
			case "keySpeed":
				return 1;
			}
		
		case "aiOptions":
			switch (key) {
			case "speed":
				return 5;
			}
			break;
			
		case "networkOptions":
			switch (key) {
			case "localPort":
			case "lastRemoteServerPort":
				return 1024; // port below 1024 required root access
			}
			
		case "soundOptions":
			switch (key) {
			case "globalVolume":
			case "musicVolume":
			case "soundsVolume":
				return 0;
			}
			
		case "lastSession":
			switch (key) {
			case "minPoints":
			case "maxPoints":
			case "diffPoints":
				return 1;
			}
		}
		
		return 0;
	}
	
	/**
	 * Get the upper bound of specified configuration key
	 * 
	 * @param section		Configuration section
	 * @param key			Configuration key
	 * @return Upper bound of specified configuration key
	 */
	public static int getMaxBound(String section, String key) {
		switch (section) {
		case "inputPrimaryPlayer":
		case "inputSecondaryPlayer":
			switch (key) {
			case "keySpeed":
				return 50;
			}
		
		case "aiOptions":
			switch (key) {
			case "speed":
				return 25;
			}
			break;
			
		case "networkOptions":
			switch (key) {
			case "localPort":
			case "lastRemoteServerPort":
				return 65535;
			}
			
		case "soundOptions":
			switch (key) {
			case "globalVolume":
			case "musicVolume":
			case "soundsVolume":
				return 100;
			}
			
		case "lastSession":
			switch (key) {
			case "minPoints":
			case "maxPoints":
			case "diffPoints":
				return 50;
			}
		}
		
		return 0;
	}
	
	/**
	 * Check if current configuration is out of bound for specified key
	 * 
	 * @param section		Configuration section
	 * @param key			Configuration key
	 * @return True if out of bound
	 */
	public static boolean isOutOfBound(String section, String key) {
		return (getInt(section, key) < getMinBound(section, key) ||
				getInt(section, key) > getMaxBound(section, key));
	}
	
	/**
	 * Reset specified configuration key to default value
	 * 
	 * @param section		Configuration section
	 * @param key			Configuration key
	 */
	public static void reset(String section, String key) {
		set(section, key, defaultIni.get(section, key));
	}
}
