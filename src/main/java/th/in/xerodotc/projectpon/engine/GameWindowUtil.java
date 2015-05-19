/**
 * GameWindowUtil.java
 * 
 * Utilities for common codes between GameDialog and main game window
 * (because Java doesn't allow multiple inheritance)
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.engine;

import java.awt.EventQueue;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public final class GameWindowUtil {
	/**
	 * Prevent instance initialization
	 */
	private GameWindowUtil() {
	}
	
	/**
	 * Common on-window closed event
	 * 
	 * @param arg0		WindowEvent
	 * @param parentWindow		A parent window
	 * @param childDialog		A child dialog
	 */
	public static void windowClosed(WindowEvent arg0, GameWindow parentWindow, GameDialog childDialog) {
		if (parentWindow != null) {
			parentWindow.childWindowClosed();
		}
		if (childDialog != null) {
			childDialog.dispose();
		}
	}
	
	/**
	 * Common on-window opened event
	 * 
	 * @param arg0		WindowEvent
	 * @param parentWindow		A parent window
	 */
	public static void windowOpened(WindowEvent arg0, GameWindow parentWindow) {
		if (parentWindow != null) {
			parentWindow.childWindowOpened();
		}
	}
	
	/**
	 * Common launch child window method
	 * 
	 * @param o		A window launcher
	 * @param w		Window to be launched
	 */
	public static void launchChildWindow(GameWindow o, final GameWindow w) {
		if (w instanceof GameDialog) {
			w.setParentWindow(o);
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					((GameDialog) w).setVisible(true);
				}
			});
		}
	}
	
	/**
	 * Common set parent window method
	 * 
	 * @param o		Window to be set parent window
	 * @param w		Parent window
	 */
	public static void setParentWindow(GameWindow o, GameWindow w) {
		if (o instanceof GameDialog) {
			GameDialog d = (GameDialog) o;
			if (w instanceof JFrame) {
				d.setLocationRelativeTo((JFrame) w);
			} else if (w instanceof GameDialog) {
				d.setLocationRelativeTo((GameDialog) w);
			}
		}
	}
}
