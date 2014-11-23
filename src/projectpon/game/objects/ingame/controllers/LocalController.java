/**
 * LocalController.java
 * 
 * A class for local game session controller
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.ingame.controllers;

import java.awt.event.KeyEvent;

import projectpon.engine.GameEngine;
import projectpon.engine.GameSound;
import projectpon.game.SessionConfiguration;
import projectpon.game.objects.ingame.Player;

public class LocalController extends Controller {

	protected boolean client = false; // client mode? (to be extended)
	protected boolean networked = false; // networked? (to be extended)
	protected int spawnItemTick = 0; // item spawning tick
	protected static final int SPAWNITEM_SECONDS = 10; // item spawn per seconds
	
	/**
	 * Initialize the controller
	 */
	public LocalController() {
		ready = true;
		spawnItemTick = SPAWNITEM_SECONDS * GameEngine.getUpdatesPerSec();
	}
	
	/**
	 * Check winner, check cheat hotkeys, spawn item
	 */
	@Override
	public void eventPreUpdate() {
		if (winner != null) {
			ready = false;
			pscene.winGame(winner);
		}
		
		/*
		 * Check cheat hotkeys
		 */
		if (SessionConfiguration.cheatsEnabled &&
				!networked && pscene.myPlayer != null) {
			cheatKeysListener();
		}
		
		/*
		 * Pause the game if ESC key is pressed (except for network game)
		 */
		if (!networked) {
			paused = nextPaused;
			if (input.isKeyReleased(KeyEvent.VK_ESCAPE)) {
				togglePause();
			}
		}
		
		/*
		 * Spawn the item
		 */
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
	
	/**
	 * Cheat hotkeys function
	 */
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
	
	/**
	 * Set winner
	 */
	@Override
	public void win(Player winner) {
		this.winner = winner;
	}
	
	/**
	 * Play the sound and also add to replay sound queue if enabled
	 */
	@Override
	public void playSound(String sound) {
		GameSound.playSound(sound);
		if (saveReplay) {
			replaySoundQueue.add(sound);
		}
	}
}
