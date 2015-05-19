/**
 * SessionConfiguration.java
 * 
 * A class for storing per-session game settings
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game;

public final class SessionConfiguration {
	public static int minimumWinScore = 5; // minimum winning score
	public static int maximumWinScore = 9; // maximum winning score
	public static int minimumWinDiff = 2; // required score difference
	
	public static boolean cheatsActivated = false; // is cheat key sequences entered
	public static boolean cheatsEnabled = false; // is cheat hotkeys enabled
	
	public static String saveReplayFile = null; // replay file to be saved if not null
}
