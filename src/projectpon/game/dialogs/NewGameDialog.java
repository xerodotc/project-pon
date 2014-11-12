package projectpon.game.dialogs;

import javax.swing.JPanel;

import projectpon.engine.GameDialog;
import projectpon.engine.GameEngine;
import projectpon.game.dialogs.panels.ServerConfigPanel;
import projectpon.game.dialogs.panels.SessionConfigPanel;
import projectpon.game.scenes.PongScene;

public class NewGameDialog extends GameDialog {
	private static final long serialVersionUID = 8244738114862197464L;
	private boolean isServer = false;
	private PongScene scene = null;
	
	private JPanel panel;
	
	public NewGameDialog(PongScene pscene) {
		this(pscene, false);
	}
	
	public NewGameDialog(PongScene pscene, boolean isServer) {
		super("New Game");
		this.isServer = isServer;
		this.scene = pscene;
		panel = new SessionConfigPanel(this);
		this.add(panel);
		this.pack();
	}
	
	public boolean isServer() {
		return isServer;
	}
	
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
}
