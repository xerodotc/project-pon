package projectpon.engine;

import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;

public abstract class GameDialog extends JDialog implements WindowListener, GameWindow {
	protected GameWindow parentWindow = null;
	protected GameDialog childDialog = null;
	
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
	
	public GameDialog(Window owner, String title, int defaultCloseOperation) {
		super(owner, Dialog.ModalityType.APPLICATION_MODAL);
		this.setTitle(title);
		this.setResizable(false);
		this.addWindowListener(this);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(defaultCloseOperation);
	}
	
	public final void setParentWindow(GameWindow w) {
		this.parentWindow = w;
		GameWindowUtil.setParentWindow(this, w);
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {
		return;
	}
	
	@Override
	public void windowClosed(WindowEvent arg0) {
		GameWindowUtil.windowClosed(arg0, parentWindow, childDialog);
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		return;
	}
	
	@Override
	public void windowOpened(WindowEvent arg0) {
		GameWindowUtil.windowOpened(arg0, parentWindow);
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
	}
	
	public void childWindowClosed() {
		this.childDialog = null;
	}
	
	public final void launchChildWindow(GameWindow w) {
		if (w instanceof GameDialog) {
			this.childDialog = (GameDialog) w;
			GameWindowUtil.launchChildWindow(this, w);
		}
	}
	
	public final void launchChildDialog(GameDialog w) {
		launchChildWindow(w);
	}
}
