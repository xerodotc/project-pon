package projectpon.game.scenes;

import projectpon.engine.GameEngine;
import projectpon.engine.GameScene;
import projectpon.engine.exceptions.InvalidWindowSize;
import projectpon.game.objects.title.*;

public class TitleScene extends GameScene {
	
	private boolean menuPreActivate = false;

	public TitleScene() throws InvalidWindowSize {
		this(false);
	}
	
	public TitleScene(boolean firstRun) throws InvalidWindowSize {
		this(GameEngine.windowWidth, GameEngine.windowHeight, firstRun);
	}
	
	public TitleScene(int width, int height, boolean firstRun) throws InvalidWindowSize {
		super(width, height);
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
