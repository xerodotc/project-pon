package projectpon.game.objects.ingame.controllers;

import java.awt.event.KeyEvent;

import projectpon.engine.GameEngine;
import projectpon.engine.GameSound;
import projectpon.game.objects.ingame.Item;
import projectpon.game.objects.ingame.Player;

public class LocalController extends Controller {

	protected Player winner = null;
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
		
		if (GameEngine.isDebugOn()) {
			debugKeyListener();
		} else {
			//debugKeyListener();
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
	
	private void debugKeyListener() {
		if (input.isKeyReleased(KeyEvent.VK_Q)) {
			pscene.playerLeft.setStatus(Player.STATUS_STICKY);
		}
		if (input.isKeyReleased(KeyEvent.VK_W)) {
			pscene.playerRight.setStatus(Player.STATUS_STICKY);
		}
		if (input.isKeyReleased(KeyEvent.VK_A)) {
			pscene.playerLeft.setStatus(Player.STATUS_BLIND);
		}
		if (input.isKeyReleased(KeyEvent.VK_S)) {
			pscene.playerRight.setStatus(Player.STATUS_BLIND);
		}
		if (input.isKeyReleased(KeyEvent.VK_Z)) {
			pscene.playerLeft.setStatus(Player.STATUS_INVERT);
		}
		if (input.isKeyReleased(KeyEvent.VK_X)) {
			pscene.playerRight.setStatus(Player.STATUS_INVERT);
		}
		if (input.isKeyReleased(KeyEvent.VK_1)) {
			pscene.playerLeft.setStatus(Player.STATUS_WALL);
		}
		if (input.isKeyReleased(KeyEvent.VK_2)) {
			pscene.playerRight.setStatus(Player.STATUS_WALL);
		}
		if (input.isKeyReleased(KeyEvent.VK_3)) {
			pscene.playerLeft.shrink();
		}
		if (input.isKeyReleased(KeyEvent.VK_4)) {
			pscene.playerRight.shrink();
		}
		if (input.isKeyReleased(KeyEvent.VK_E)) {
			pscene.playerLeft.expand();
		}
		if (input.isKeyReleased(KeyEvent.VK_R)) {
			pscene.playerRight.expand();
		}
		if (input.isKeyReleased(KeyEvent.VK_I)) {
			pscene.spawnItem();
		}
		if (input.isKeyReleased(KeyEvent.VK_D)) {
			pscene.spawnItem(Item.ITEM_EXPAND);
		}
		if (input.isKeyReleased(KeyEvent.VK_F)) {
			pscene.spawnItem(Item.ITEM_WALL);
		}
		if (input.isKeyReleased(KeyEvent.VK_G)) {
			pscene.spawnItem(Item.ITEM_STICKY);
		}
		if (input.isKeyReleased(KeyEvent.VK_C)) {
			pscene.spawnItem(Item.ITEM_SHRINK);
		}
		if (input.isKeyReleased(KeyEvent.VK_V)) {
			pscene.spawnItem(Item.ITEM_BLIND);
		}
		if (input.isKeyReleased(KeyEvent.VK_B)) {
			pscene.spawnItem(Item.ITEM_INVERT);
		}
	}
	
	@Override
	public void win(Player winner) {
		this.winner = winner;
	}
	
	@Override
	public void playSound(String sound) {
		GameSound.playSound(sound);
	}

}
