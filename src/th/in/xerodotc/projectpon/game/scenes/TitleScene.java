/**
 * TitleScene.java
 * 
 * Title scene
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game.scenes;

import th.in.xerodotc.projectpon.engine.GameEngine;
import th.in.xerodotc.projectpon.engine.GameScene;
import th.in.xerodotc.projectpon.engine.GameSound;
import th.in.xerodotc.projectpon.game.objects.title.*;

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
