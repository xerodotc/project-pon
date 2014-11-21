/**
 * GameFont.java
 * 
 * A class for managing in-game fonts
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.engine;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class GameFont {
	// fonts list map
	private static Map<String,Font> fontsList = new HashMap<String,Font>();
	
	/**
	 * Prevent instance initialization
	 */
	private GameFont() {
	}
	
	/**
	 * Initialize the fonts system
	 */
	public static void init() {
		// hmmm... nothing to initialize
	}
	
	/**
	 * Load font from the file
	 * 
	 * @param fontName		Font name to be identified
	 * @param resPath		Font resource path
	 */
	public static void loadFont(String fontName, String resPath) {
		while (resPath.substring(0, 1).equals("/")) {
			resPath = resPath.substring(1);
		}
		try {
			// Check for internal resource first then external resource
			InputStream istream = GameFont.class.getClassLoader().getResourceAsStream(resPath);
			if (istream == null) {
				istream = new FileInputStream(new File(resPath));
			}
			fontsList.put(fontName, Font.createFont(Font.TRUETYPE_FONT, istream));
			if (GameEngine.isDebugOn()) {
				System.out.println("fontResourceLoaded: " + fontName + " : " + resPath);
			}
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get loaded font
	 * 
	 * @param fontName		Font name
	 * @return	Font object for specified font
	 */
	public static Font getFont(String fontName) {
		return fontsList.get(fontName);
	}
}
