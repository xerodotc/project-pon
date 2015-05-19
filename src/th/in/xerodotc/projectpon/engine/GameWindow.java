/**
 * GameWindow.java
 * 
 * An interface for all game window (including dialog and main game window)
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.engine;

public interface GameWindow {
	public abstract void setParentWindow(GameWindow w);
	public abstract void childWindowOpened();
	public abstract void childWindowClosed();
	public abstract void launchChildWindow(GameWindow w);
}
