/**
 * CheatKeyListener.java
 * 
 * A class for listening cheat key sequences
 */
package th.in.xerodotc.projectpon.game.objects.title;

import java.awt.event.KeyEvent;

import th.in.xerodotc.projectpon.engine.GameObject;
import th.in.xerodotc.projectpon.engine.GameSound;
import th.in.xerodotc.projectpon.game.SessionConfiguration;
import th.in.xerodotc.projectpon.game.scenes.TitleScene;

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
		} else if (cheatKeySequence[state] >= KeyEvent.VK_0 &&
				cheatKeySequence[state] <= KeyEvent.VK_9 &&
				input.isKeyPressed(cheatKeySequence[state] -
						KeyEvent.VK_0 + KeyEvent.VK_NUMPAD0)) {
			// allow numpad
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
