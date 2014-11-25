/**
 * ReplayController.java
 * 
 * A class for replayed game session controller
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.ingame.controllers;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import projectpon.engine.GameEngine;
import projectpon.engine.GameSound;
import projectpon.game.objects.ingame.shadow.ShadowBall;
import projectpon.game.objects.ingame.shadow.ShadowPlayer;
import projectpon.game.replay.ReplayData;
import projectpon.game.replay.ReplayFrame;
import projectpon.game.scenes.ShadowPongScene;

public class ReplayController extends LocalController {
	private ReplayData replayData; // loaded replay data
	
	/**
	 * Load replay data from file
	 * 
	 * @param file		File name
	 */
	public ReplayController(String file) {
		super();
		
		try {
			ObjectInputStream in = new ObjectInputStream(
					new FileInputStream(new File(file)));
			replayData = (ReplayData) in.readObject();
			in.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			GameEngine.exit();
		}
	}
	
	/**
	 * Control objects' position according to replay file
	 */
	@Override
	public void eventPreUpdate() {
		if (replayData.isEOF()) {
			return;
		}
		
		paused = nextPaused;
		if (input.isKeyReleased(KeyEvent.VK_ESCAPE)) {
			togglePause();
		}
		
		if (isPaused()) {
			return;
		}
		
		ReplayFrame frame = replayData.popNextFrame(); // get next frame
		
		ShadowPongScene spscene = (ShadowPongScene) pscene;
		ShadowPlayer sPlayerLeft = (ShadowPlayer) spscene.playerLeft;
		ShadowPlayer sPlayerRight = (ShadowPlayer) spscene.playerRight;
		ShadowBall sBall = (ShadowBall) spscene.ball;
		
		sPlayerLeft.setCoordinate(frame.playerLeftX, frame.playerLeftY);
		sPlayerLeft.resize(frame.playerLeftSize);
		sPlayerLeft.setAllStatusesFlag(frame.playerLeftStatuses);
		sPlayerLeft.setAllTimers(frame.playerLeftStatusesTimer);
		
		sPlayerRight.setCoordinate(frame.playerRightX, frame.playerRightY);
		sPlayerRight.resize(frame.playerRightSize);
		sPlayerRight.setAllStatusesFlag(frame.playerRightStatuses);
		sPlayerRight.setAllTimers(frame.playerRightStatusesTimer);
		
		sBall.setVisibility(true);
		sBall.setCoordinate(frame.ballX, frame.ballY);
		
		sPlayerLeft.score = frame.scoreLeft;
		sPlayerRight.score = frame.scoreRight;
		
		spscene.setShadowItems(frame.fieldItems);
		
		if (frame.sound != null) {
			GameSound.playSound(frame.sound); // play the sound
			if (saveReplay) {
				// its okay if you want to save replay from replay...
				replaySoundQueue.add(frame.sound);
			}
		}
		
		if (frame.winner != null) {
			switch (frame.winner) {
			case "left":
				pscene.winGame(pscene.playerLeft);
				break;
				
			case "right":
				pscene.winGame(pscene.playerRight);
				break;
			}
		}
	}
}
