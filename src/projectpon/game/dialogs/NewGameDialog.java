/**
 * NewGameDialog.java
 * 
 * A dialog for starting a new game (also host a network game)
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.dialogs;

import java.awt.event.WindowEvent;

import javax.swing.JPanel;

import projectpon.engine.GameDialog;
import projectpon.engine.GameEngine;
import projectpon.engine.GameNetwork;
import projectpon.engine.exceptions.NetworkException;
import projectpon.game.dialogs.panels.ServerConfigPanel;
import projectpon.game.dialogs.panels.SessionConfigPanel;
import projectpon.game.scenes.PongScene;

public class NewGameDialog extends GameDialog {
	private static final long serialVersionUID = 8244738114862197464L;
	
	private boolean isServer = false; // start as server?
	private PongScene scene = null; // PongScene to be switched to
	
	private JPanel panel; // the JPanel
	
	/**
	 * Setup the whole dialog (not as server)
	 * 
	 * @param pscene	The PongScene
	 */
	public NewGameDialog(PongScene pscene) {
		this(pscene, false);
	}
	
	/**
	 * Setup the whole dialog
	 * 
	 * @param pscene	The PongScene
	 * @param isServer	Start as server?
	 */
	public NewGameDialog(PongScene pscene, boolean isServer) {
		super("New Game");
		this.isServer = isServer;
		this.scene = pscene;
		panel = new SessionConfigPanel(this);
		this.add(panel);
		this.pack();
	}
	
	/**
	 * Is to be started as server
	 * 
	 * @return	True or false
	 */
	public boolean isServer() {
		return isServer;
	}
	
	/**
	 * Perform next action (start the game or switch to server control panel)
	 */
	public void next() {
		if (!isServer) {
			GameEngine.setScene(scene);
			this.dispose();
		} else {
			this.remove(panel);
			panel = new ServerConfigPanel(this, scene);
			this.add(panel);
			this.pack();
		}
	}
	
	/**
	 * Stop the server on window closed
	 */
	@Override
	public void windowClosed(WindowEvent arg0) {
		if (isServer && GameNetwork.Server.isStarted()) {
			try {
				GameNetwork.Server.stop();
			} catch (NetworkException e) {
				e.printStackTrace();
			}
		}
		super.windowClosed(arg0);
	}
}
