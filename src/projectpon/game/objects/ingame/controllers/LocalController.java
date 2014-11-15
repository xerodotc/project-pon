package projectpon.game.objects.ingame.controllers;

import java.awt.event.KeyEvent;

import projectpon.engine.GameEngine;
import projectpon.engine.GameSound;
import projectpon.game.SessionConfiguration;
import projectpon.game.objects.ingame.Player;

public class LocalController extends Controller {

	protected boolean client = false;
	protected boolean networked = false;
	protected int spawnItemTick = 0;
	protected static final int SPAWNITEM_SECONDS = 10;
	
	public LocalController() {
		ready = true;
		spawnItemTick = SPAWNITEM_SECONDS * GameEngine.getUpdatesPerSec();
	}
	
	@Override
	public void eventPreUpdate() {
		if (winner != null) {
			ready = false;
			pscene.winGame(winner);
		}
		
		if (SessionConfiguration.cheatsEnabled &&
				!networked && pscene.myPlayer != null) {
			cheatKeysListener();
		}
		
		if (!networked) {
			paused = nextPaused;
			if (input.isKeyReleased(KeyEvent.VK_ESCAPE)) {
				togglePause();
			}
		}
		
		if (!isPaused()) {
			if (spawnItemTick <= 0) {
				pscene.spawnItem();
			}
			spawnItemTick--;
			if (spawnItemTick < 0) {
				spawnItemTick = SPAWNITEM_SECONDS * GameEngine.getUpdatesPerSec();
			}
		}
	}
	
	private void cheatKeysListener() {
		if (input.isKeyPressed(KeyEvent.VK_I)) {
			pscene.spawnItem();
		}
		if (input.isKeyPressed(KeyEvent.VK_S)) {
			pscene.myPlayer.setStatus(Player.STATUS_STICKY);
		}
		if (input.isKeyPressed(KeyEvent.VK_U)) {
			pscene.myPlayer.unblind();
		}
		if (input.isKeyPressed(KeyEvent.VK_W)) {
			pscene.myPlayer.setStatus(Player.STATUS_WALL);
		}
		if (input.isKeyPressed(KeyEvent.VK_1)) {
			pscene.myPlayer.shrink();
		}
		if (input.isKeyPressed(KeyEvent.VK_2)) {
			pscene.myPlayer.expand();
		}
	}
	
	@Override
	public void win(Player winner) {
		this.winner = winner;
	}
	
	@Override
	public void playSound(String sound) {
		GameSound.playSound(sound);
		if (saveReplay) {
			replaySoundQueue.add(sound);
		}
	}
}
