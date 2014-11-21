/**
 * GameImage.java
 * 
 * A class for managing in-game images
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.engine;

import java.util.HashMap;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public final class GameImage {
	// images list map as BufferedImage
	private static Map<String,BufferedImage> imagesList = new HashMap<String,BufferedImage>();
	
	/**
	 * Prevent instance initialization
	 */
	private GameImage() {
	}
	
	/**
	 * Initialize images system
	 */
	public static void init() {
		// hmmm... nothing to initialize
	}
	
	/**
	 * Load image from file
	 * 
	 * @param imageName		Image name to be identified
	 * @param resPath		Image resource path
	 */
	public static void loadImage(String imageName, String resPath) {
		while (resPath.substring(0, 1).equals("/")) {
			resPath = resPath.substring(1);
		}
		try {
			// Check for internal resource first then external resource
			URL url = GameImage.class.getClassLoader().getResource(resPath);
			if (url == null) {
				url = new File(resPath).toURI().toURL();
			}
			imagesList.put(imageName, ImageIO.read(url));
			if (GameEngine.isDebugOn()) {
				System.out.println("imageResourceLoaded: " + imageName + " : " + resPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get loaded image
	 * 
	 * @param imageName		Image name
	 * @return	BufferedImage for specified image
	 */
	public static BufferedImage getImage(String imageName) {
		return imagesList.get(imageName);
	}
}
