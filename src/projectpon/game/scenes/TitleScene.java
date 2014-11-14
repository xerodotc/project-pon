package projectpon.game.scenes;

import projectpon.engine.GameEngine;
import projectpon.engine.GameScene;
import projectpon.engine.GameSound;
import projectpon.game.objects.title.*;

public class TitleScene extends GameScene {
	
	private boolean menuPreActivate = false;
	public TitleMenu menu;

	public TitleScene() {
		this(false);
	}
	
	public TitleScene(boolean firstRun) {
		menuPreActivate = !firstRun;
	}

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
