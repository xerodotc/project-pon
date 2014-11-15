package projectpon.game.objects.ingame.controllers;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.Queue;

import projectpon.engine.GameObject;
import projectpon.game.objects.ingame.Player;
import projectpon.game.replay.ReplayData;
import projectpon.game.replay.ReplayFrame;
import projectpon.game.scenes.PongScene;

public abstract class Controller extends GameObject {
	protected Player winner = null;
	
	protected PongScene pscene = null;
	protected boolean ready = false;
	protected boolean paused = false;
	protected boolean nextPaused = false;
	
	protected boolean saveReplay = false;
	protected ReplayData replayData;
	protected boolean replayFinish = false;
	protected Queue<String> replaySoundQueue;
	
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
	
	public void setSaveReplay(String file) {
		this.visible = true;
		saveReplay = true;
		replayData = new ReplayData();
		replayData.version = 1;
		replaySoundQueue = new ArrayDeque<String>();
	}
	
	@Override
	public void draw(Graphics2D canvas) {
		if (!saveReplay || replayFinish) {
			return;
		}
		
		ReplayFrame frame = new ReplayFrame();
		
		frame.playerLeftX = pscene.playerLeft.getCoordinate().x;
		frame.playerLeftY = pscene.playerLeft.getCoordinate().y;
		frame.playerLeftSize = pscene.playerLeft.getSize();
		frame.playerLeftStatuses = pscene.playerLeft.getAllStatuses();
		frame.playerLeftStatusesTimer = pscene.playerLeft.getAllTimers();
		
		frame.playerRightX = pscene.playerRight.getCoordinate().x;
		frame.playerRightY = pscene.playerRight.getCoordinate().y;
		frame.playerRightSize = pscene.playerRight.getSize();
		frame.playerRightStatuses = pscene.playerRight.getAllStatuses();
		frame.playerRightStatusesTimer = pscene.playerRight.getAllTimers();
		
		frame.ballX = pscene.ball.getCoordinate().x;
		frame.ballY = pscene.ball.getCoordinate().y;
		
		frame.scoreLeft = pscene.playerLeft.score;
		frame.scoreRight = pscene.playerRight.score;
		
		if (!replaySoundQueue.isEmpty()) {
			frame.sound = replaySoundQueue.remove();
		} else {
			frame.sound = null;
		}
		
		frame.fieldItems = pscene.getShadowItems();
		
		if (winner == pscene.playerLeft) {
			frame.winner = "left";
			replayFinish = true;
		} else if (winner == pscene.playerRight) {
			frame.winner = "right";
			replayFinish = true;
		} else {
			frame.winner = null;
		}
		
		replayData.appendFrame(frame);
		
		if (replayFinish) {
			try {
				ObjectOutputStream out = new ObjectOutputStream(
						new FileOutputStream(new File("replay.dat")));
				out.writeObject(replayData);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
