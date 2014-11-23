/**
 * CheatKeyListener.java
 * 
 * A class for listening cheat key sequences
 */
package projectpon.game.objects.title;

import java.awt.event.KeyEvent;

import projectpon.engine.GameObject;
import projectpon.engine.GameSound;
import projectpon.game.SessionConfiguration;
import projectpon.game.scenes.TitleScene;

public class CheatKeyListener extends GameObject {
	private int state = 0; // current state of sequence
	
	// the cheat keys sequence
	private int[] cheatKeySequence = {KeyEvent.VK_UP, KeyEvent.VK_UP,
			KeyEvent.VK_DOWN, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
			KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_2, KeyEvent.VK_1,
			KeyEvent.VK_1, KeyEvent.VK_0, KeyEvent.VK_2, KeyEvent.VK_1, KeyEvent.VK_5};
	
	/**
	 * Initialize
	 */
	public CheatKeyListener() {
		super();
	}
	
	/**
	 * Listen for cheat keys sequence
	 */
	@Override
	public void eventPreUpdate() {
		// if menu was activated, disable this
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
			// sequence broken
			state = 0;
		}
		
		// sequence complete, unhide cheat menu
		if (state >= cheatKeySequence.length) {
			SessionConfiguration.cheatsActivated = true;
			GameSound.playSound("baap");
		}
	}
}
