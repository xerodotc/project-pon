package projectpon.engine;

import java.awt.EventQueue;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

final class GameWindowUtil {
	private GameWindowUtil() {
	}
	
	public static void windowClosed(WindowEvent arg0, GameWindow parentWindow, GameDialog childDialog) {
		if (parentWindow != null) {
			parentWindow.childWindowClosed();
		}
		if (childDialog != null) {
			childDialog.dispose();
		}
	}
	
	public static void windowOpened(WindowEvent arg0, GameWindow parentWindow) {
		if (parentWindow != null) {
			parentWindow.childWindowOpened();
		}
	}
	
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
