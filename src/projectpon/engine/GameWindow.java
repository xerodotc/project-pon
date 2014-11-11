package projectpon.engine;

import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public abstract class GameWindow extends JFrame implements WindowListener {
	protected GameWindow parentWindow = null;
	protected GameWindow childWindow = null;
	
	public GameWindow() {
		this("");
	}
	
	public GameWindow(String title) {
		this(title, DISPOSE_ON_CLOSE);
	}
	
	public GameWindow(String title, int defaultCloseOperation) {
		super(title);
		this.setResizable(false);
		this.addWindowListener(this);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(defaultCloseOperation);
	}
	
	public final void setParentWindow(GameWindow w) {
		this.parentWindow = w;
		this.setLocationRelativeTo(this.parentWindow);
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {
		if (childWindow != null) {
			childWindow.toFront();
			childWindow.requestFocus();
		}
	}
	
	@Override
	public void windowClosed(WindowEvent arg0) {
		if (parentWindow != null) {
			parentWindow.childWindowClosed();
		}
		if (childWindow != null) {
			childWindow.dispose();
		}
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		return;
	}
	
	@Override
	public void windowOpened(WindowEvent arg0) {
		if (parentWindow != null) {
			parentWindow.childWindowOpened();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		return;
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		return;
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		return;
	}
	
	public void childWindowOpened() {
		this.setFocusable(false);
	}
	
	public void childWindowClosed() {
		this.setFocusable(true);
	}
	
	public final void launchChildWindow(GameWindow w) {
		this.childWindow = w;
		this.childWindow.setParentWindow(this);
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				childWindow.setVisible(true);
			}
		});
	}
}
