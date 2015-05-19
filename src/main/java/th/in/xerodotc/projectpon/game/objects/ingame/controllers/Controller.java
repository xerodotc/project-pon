/**
 * Controller.java
 * 
 * An abstract class for game controller
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game.objects.ingame.controllers;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.Queue;

import th.in.xerodotc.projectpon.engine.GameObject;
import th.in.xerodotc.projectpon.game.objects.ingame.Player;
import th.in.xerodotc.projectpon.game.replay.ReplayData;
import th.in.xerodotc.projectpon.game.replay.ReplayFrame;
import th.in.xerodotc.projectpon.game.scenes.PongScene;

public abstract class Controller extends GameObject {
	protected Player winner = null; // store winning player
	
	protected PongScene pscene = null; // store PongScene
	protected boolean ready = false; // is the game ready?
	protected boolean paused = false; // is the game paused?
	protected boolean nextPaused = false; // is the game to be paused?
	
	protected boolean saveReplay = false; // save the replay?
	protected ReplayData replayData; // object for storing replay data
	// is the replay has be stopped recording?
	protected boolean replayFinish = false;
	protected Queue<String> replaySoundQueue; // sound queue for replay
	private String replayFileName = null; // replay file name
	
	/**
	 * Assign PongScene
	 */
	@Override
	public void eventOnCreate() {
		if (scene instanceof PongScene) {
			pscene = (PongScene) scene;
		}
	}
	
	/**
	 * Trigger win event
	 * 
	 * @param winner	The winning player
	 */
	public void win(Player winner) {
		// do nothing, to be implemented in subclasses
	}
	
	/**
	 * Is the game ready?
	 * 
	 * @return True if the game is ready
	 */
	public boolean isReady() {
		return ready;
	}
	
	/**
	 * Is the game paused (or not ready)?
	 * 
	 * @return True if the game is paused
	 */
	public boolean isPaused() {
		return paused || !ready;
	}
	
	/**
	 * Is the game paused by user?
	 * 
	 * @return True if the game has been paused by user
	 */
	public boolean isUserPaused() {
		return paused;
	}
	
	/**
	 * Unpause the game
	 */
	public void unpause() {
		nextPaused = false;
	}
	
	/**
	 * Pause the game
	 */
	public void pause() {
		nextPaused = true;
	}
	
	/**
	 * Toggle pause status
	 */
	public void togglePause() {
		nextPaused = !paused;
	}
	
	/**
	 * Play sound
	 * 
	 * @param sound		Sound name
	 */
	public void playSound(String sound) {
		// to be implemented in subclasses
	}
	
	/**
	 * Set file to be saved for replay
	 * 
	 * @param file	File name
	 */
	public void setSaveReplay(String file) {
		this.visible = true;
		saveReplay = true;
		replayData = new ReplayData();
		replayData.version = 1;
		replaySoundQueue = new ArrayDeque<String>();
		replayFileName = file;
	}
	
	/**
	 * Some replay recording code are here...
	 */
	@Override
	public void draw(Graphics2D canvas) {
		if (!saveReplay || replayFinish) {
			return;
		}
		
		ReplayFrame frame = new ReplayFrame(); // create new replay frame
		
		/*
		 * Save everything in to replay frame
		 */
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
		
		replayData.appendFrame(frame); // add frame to replay data
		
		if (replayFinish) {
			try {
				ObjectOutputStream out = new ObjectOutputStream(
						new FileOutputStream(new File(replayFileName)));
				out.writeObject(replayData); // save replay to file
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
