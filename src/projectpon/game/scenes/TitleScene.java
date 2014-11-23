/**
 * TitleScene.java
 * 
 * Title scene
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.scenes;

import projectpon.engine.GameEngine;
import projectpon.engine.GameScene;
import projectpon.engine.GameSound;
import projectpon.game.objects.title.*;

public class TitleScene extends GameScene {
	
	private boolean menuPreActivate = false; // is menu pre-activated
	public TitleMenu menu; // the menu object

	/**
	 * Default constructor
	 */
	public TitleScene() {
		this(false);
	}
	
	/**
	 * Setup title scene
	 * 
	 * @param firstRun		Is called for first time of execution
	 */
	public TitleScene(boolean firstRun) {
		menuPreActivate = !firstRun;
	}

	/**
	 * Initialize and add necessary objects
	 */
	@Override
	public void initialize() {
		this.objectAdd(new TitleParticles());
		this.objectAdd(new TitleLogo());
		menu = new TitleMenu(menuPreActivate);
		this.objectAdd(menu);
		this.objectAdd(new CheatKeyListener());
		GameEngine.enableExitOnClose();
		GameSound.playMusic("title");
	}
}
