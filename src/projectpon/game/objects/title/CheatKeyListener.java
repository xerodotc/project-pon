package projectpon.game.objects.title;

import java.awt.event.KeyEvent;

import projectpon.engine.GameObject;
import projectpon.engine.GameSound;
import projectpon.game.SessionConfiguration;
import projectpon.game.scenes.TitleScene;

public class CheatKeyListener extends GameObject {
	private int state = 0;
	private int[] cheatKeySequence = {KeyEvent.VK_UP, KeyEvent.VK_UP,
			KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
			KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_2, KeyEvent.VK_1,
			KeyEvent.VK_1, KeyEvent.VK_0, KeyEvent.VK_2, KeyEvent.VK_1, KeyEvent.VK_5};
	
	public CheatKeyListener() {
		super();
	}
	
	@Override
	public void eventPreUpdate() {
		if (((TitleScene) scene).menu.isActivated()) {
			this.destroy();
			return;
		}
		
		if (state >= cheatKeySequence.length) {
			return;
		}
		
		if (input.isKeyPressed(cheatKeySequence[state])) {
			state++;
		} else if (input.isAnyKeyPressed()) {
			state = 0;
		}
		
		if (state >= cheatKeySequence.length) {
			SessionConfiguration.cheatsActivated = true;
			GameSound.playSound("baap");
		}
	}
}
