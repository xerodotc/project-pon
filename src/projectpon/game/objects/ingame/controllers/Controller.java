package projectpon.game.objects.ingame.controllers;

import projectpon.engine.GameObject;
import projectpon.game.objects.ingame.Player;
import projectpon.game.scenes.PongScene;

public abstract class Controller extends GameObject {
	protected PongScene pscene = null;
	protected boolean ready = false;
	protected boolean paused = false;
	protected boolean nextPaused = false;
	
	@Override
	public void eventOnCreate() {
		if (scene instanceof PongScene) {
			pscene = (PongScene) scene;
		}
	}
	
	public void win(Player winner) {
		// do nothing
	}
	
	public boolean isReady() {
		return ready;
	}
	
	public boolean isPaused() {
		return paused || !ready;
	}
	
	public boolean isUserPaused() {
		return paused;
	}
	
	public void unpause() {
		nextPaused = false;
	}
	
	public void pause() {
		nextPaused = true;
	}
	
	public void togglePause() {
		nextPaused = !paused;
	}
	
	public void playSound(String sound) {
		// to be implemented in subclasses
	}
}
