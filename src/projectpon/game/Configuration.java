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
		defaultIni.add("inputPrimaryPlayer", "keyUp", "");
		defaultIni.add("inputPrimaryPlayer", "keyDown", "");
		defaultIni.add("inputPrimaryPlayer", "keyLaunch", "");
		defaultIni.add("inputPrimaryPlayer", "keySpeed", "10");
		defaultIni.add("inputPrimaryPlayer", "mbLaunch", "" + MouseEvent.BUTTON1);
		defaultIni.add("inputSecondaryPlayer", "type", "keyboard");
		defaultIni.add("inputSecondaryPlayer", "keyUp", "" + KeyEvent.VK_UP);
		defaultIni.add("inputSecondaryPlayer", "keyDown", "" + KeyEvent.VK_DOWN);
		defaultIni.add("inputSecondaryPlayer", "keyLaunch", "" + KeyEvent.VK_SPACE);
		defaultIni.add("inputSecondaryPlayer", "keySpeed", "10");
		defaultIni.add("inputSecondaryPlayer", "mbLaunch", "");
		
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
		defaultIni.add("soundOptions", "soundsVolume", "100");
	}
	
	public static boolean isLoaded() {
		return ini != null;
	}
	
	public static void load() {
		load(iniFileName);
	}
	
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
	
	public static void save() {
		try {
			if (ini != defaultIni) {
				ini.store();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String get(String section, String key) {
		if (ini.get(section, key) == null) {
			if (defaultIni.get(section, key) != null) {
				ini.add(section, key, defaultIni.get(section, key));
			}
		}
		return ini.get(section, key);
	}
	
	public static int getInt(String section, String key) {
		try {
			return Integer.parseInt(get(section, key));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static boolean getBoolean(String section, String key) {
		return Boolean.parseBoolean(get(section, key));
	}
	
	public static void set(String section, String key, String value) {
		ini.put(section, key, value);
	}
	
	public static void set(String section, String key, int value) {
		set(section, key, Integer.toString(value));
	}
	
	public static void set(String section, String key, boolean value) {
		set(section, key, Boolean.toString(value));
	}
	
	public static boolean validate() {
		return validate(false);
	}
	
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
		
		return valid;
	}
	
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
		}
		
		return 0;
	}
	
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
		}
		
		return 0;
	}
	
	public static boolean isOutOfBound(String section, String key) {
		return (getInt(section, key) < getMinBound(section, key) ||
				getInt(section, key) > getMaxBound(section, key));
	}
	
	public static void reset(String section, String key) {
		set(section, key, defaultIni.get(section, key));
	}
}
