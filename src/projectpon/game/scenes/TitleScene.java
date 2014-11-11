package projectpon.game.scenes;

import projectpon.engine.GameEngine;
import projectpon.engine.GameScene;
import projectpon.game.objects.title.*;

public class TitleScene extends GameScene {
	
	private boolean menuPreActivate = false;

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
		this.objectAdd(new TitleMenu(menuPreActivate));
		GameEngine.enableExitOnClose();
	}
}
