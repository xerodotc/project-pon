package projectpon.game.windows;

import javax.swing.JPanel;

import projectpon.engine.GameEngine;
import projectpon.engine.GameWindow;
import projectpon.game.scenes.PongScene;
import projectpon.game.windows.panels.SessionConfigPanel;

public class NewGameWindow extends GameWindow {
	private static final long serialVersionUID = 8244738114862197464L;
	private boolean isServer = false;
	private PongScene scene = null;
	
	private JPanel panel;
	
	public NewGameWindow(PongScene pscene) {
		this(pscene, false);
	}
	
	public NewGameWindow(PongScene pscene, boolean isServer) {
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
			/**
			 * TODO: Server option panel next to session options
			 */
		}
	}
}
