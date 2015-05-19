/**
 * GameDialog.java
 * 
 * An abstract class for all in-game dialog
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.engine;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;

public abstract class GameDialog extends JDialog implements WindowListener, GameWindow {
	protected GameWindow parentWindow = null; // parent window (dialog called)
	protected GameDialog childDialog = null; // child dialog (if available)
	
	/**
	 * Some default constructors
	 */
	public GameDialog() {
		this(GameEngine.getFrame());
	}
	
	public GameDialog(Window owner) {
		this(owner, "");
	}
	
	public GameDialog(String title) {
		this(GameEngine.getFrame(), title);
	}
	
	public GameDialog(Window owner, String title) {
		this(owner, title, DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Constructors
	 * 
	 * @param owner		Dialog owner
	 * @param title		Dialog title
	 * @param defaultCloseOperation		Default close operation
	 */
	public GameDialog(Window owner, String title, int defaultCloseOperation) {
		super(owner, Dialog.ModalityType.APPLICATION_MODAL);
		this.setTitle(title);
		this.setResizable(false);
		this.addWindowListener(this);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(defaultCloseOperation);
	}
	
	/**
	 * Set parent window for this dialog
	 * 
	 * @param w		A parent window object
	 */
	public final void setParentWindow(GameWindow w) {
		this.parentWindow = w;
		GameWindowUtil.setParentWindow(this, w);
	}
	
	/**
	 * Window activated listener
	 */
	@Override
	public void windowActivated(WindowEvent arg0) {
		return;
	}
	
	/**
	 * Window closed listener
	 */
	@Override
	public void windowClosed(WindowEvent arg0) {
		GameWindowUtil.windowClosed(arg0, parentWindow, childDialog);
	}

	/**
	 * Window closing listener
	 */
	@Override
	public void windowClosing(WindowEvent arg0) {
		return;
	}
	
	/**
	 * Window opened listener
	 */
	@Override
	public void windowOpened(WindowEvent arg0) {
		GameWindowUtil.windowOpened(arg0, parentWindow);
	}

	/**
	 * Window deactivated listener
	 */
	@Override
	public void windowDeactivated(WindowEvent arg0) {
		return;
	}

	/**
	 * Window deiconified listener
	 */
	@Override
	public void windowDeiconified(WindowEvent arg0) {
		return;
	}

	/**
	 * Window iconified listener
	 */
	@Override
	public void windowIconified(WindowEvent arg0) {
		return;
	}
	
	/**
	 * Code to be called on child window opened
	 */
	public void childWindowOpened() {
		return;
	}
	
	/**
	 * Code to be called on child window closed
	 */
	public void childWindowClosed() {
		this.childDialog = null;
	}
	
	/**
	 * Launch child window/dialog
	 * 
	 * @param w		Child window object
	 */
	public final void launchChildWindow(GameWindow w) {
		if (w instanceof GameDialog) {
			this.childDialog = (GameDialog) w;
			GameWindowUtil.launchChildWindow(this, w);
		}
	}
	
	/**
	 * Launch child dialog
	 * 
	 * @param w		Child dialog object
	 */
	public final void launchChildDialog(GameDialog w) {
		launchChildWindow(w);
	}
}
