/**
 * ClientController.java
 * 
 * A class for client session game controller
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game.objects.ingame.controllers;

import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.in.xerodotc.projectpon.engine.GameEngine;
import th.in.xerodotc.projectpon.engine.GameInput;
import th.in.xerodotc.projectpon.engine.GameNetwork;
import th.in.xerodotc.projectpon.engine.GameSound;
import th.in.xerodotc.projectpon.game.Configuration;
import th.in.xerodotc.projectpon.game.objects.ingame.shadow.*;
import th.in.xerodotc.projectpon.game.scenes.ShadowPongScene;

public class ClientController extends LocalController {
	private Thread outputData; // output stream thread
	private Thread inputData; // input stream thread
	private Socket socket; // socket
	
	// flag indicate that is last frame before winning has been drawn
	private boolean flag = false;
	private boolean connectionLost = false; // connection lost flag
	
	/**
	 * Initialize and set socket timeout
	 * 
	 * @param socket		The socket for this game
	 */
	public ClientController(Socket socket) {
		super();
		try {
			socket.setSoTimeout(GameNetwork.DEFAULT_TIMEOUT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.socket = socket;
		this.client = true;
		this.visible = true; // force trigger draw event
		this.networked = true;
		this.ready = false;
	}
	
	/**
	 * Initialize the output and input thread
	 */
	@Override
	public void eventOnCreate() {
		super.eventOnCreate();
		
		/*
		 * Send input data to server
		 */
		outputData = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					while (true) {
						Map<String,Object> container = new HashMap<String,Object>();
						boolean inputLaunch;
						switch (Configuration.get("inputPrimaryPlayer", "type")) {
						case "mouse":
							container.put("inputType", "mouse");
							int inputX = GameInput.localInput.getMouseX();
							int inputY = GameInput.localInput.getMouseY();
							inputLaunch = GameInput.localInput.isMouseDown(
									Configuration.getInt("inputPrimaryPlayer", "mbLaunch"));
							container.put("inputX", inputX);
							container.put("inputY", inputY);
							container.put("inputLaunch", inputLaunch);
							break;
							
						case "keyboard":
							container.put("inputType", "keyboard");
							boolean inputUp = GameInput.localInput.isKeyDown(
									Configuration.getInt("inputPrimaryPlayer", "keyUp"));
							boolean inputDown = GameInput.localInput.isKeyDown(
									Configuration.getInt("inputPrimaryPlayer", "keyDown"));
							inputLaunch = GameInput.localInput.isKeyDown(
									Configuration.getInt("inputPrimaryPlayer", "keyLaunch"));
							int inputSpeed = Configuration.getInt("inputPrimaryPlayer", "keySpeed");
							container.put("inputUp", inputUp);
							container.put("inputDown", inputDown);
							container.put("inputLaunch", inputLaunch);
							container.put("inputSpeed", inputSpeed);
							break;
						}
						
						out.writeObject(container);
						out.flush();
						Thread.sleep(Math.round(1000.0 /
								GameEngine.getUpdatesPerSec())); // prevent flooding too much input
					}
				} catch (IOException e) {
					connectionLost = true;
					if (winner == null) {
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		/*
		 * Receive game information from server
		 */
		inputData = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
					ShadowBall sball = (ShadowBall) pscene.ball;
					ShadowPlayer splayerLeft = (ShadowPlayer) pscene.playerLeft;
					ShadowPlayer splayerRight = (ShadowPlayer) pscene.playerRight;
					sball.setVisibility(true);
					while (true) {
						try {
							Map<String,Object> container = (Map<String,Object>) in.readObject();
							
							// Data about left player
							int paddleLeftX = (int) container.get("paddleLeftX");
							int paddleLeftY = (int) container.get("paddleLeftY");
							int paddleLeftSize = (int) container.get("paddleLeftSize");
							Map<Integer,Boolean> paddleLeftStatuses = 
									(Map<Integer,Boolean>) container.get("paddleLeftStatuses");
							Map<Integer,Integer> paddleLeftStatusesTimer = 
									(Map<Integer,Integer>) container.get("paddleLeftStatusesTimer");
							splayerLeft.setCoordinate(paddleLeftX, paddleLeftY);
							splayerLeft.resize(paddleLeftSize);
							if (paddleLeftStatuses != null && paddleLeftStatusesTimer != null) {
								splayerLeft.setAllStatusesFlag(paddleLeftStatuses);
								splayerLeft.setAllTimers(paddleLeftStatusesTimer);
							}
							
							// Data about right player
							int paddleRightX = (int) container.get("paddleRightX");
							int paddleRightY = (int) container.get("paddleRightY");
							int paddleRightSize = (int) container.get("paddleRightSize");
							Map<Integer,Boolean> paddleRightStatuses = 
									(Map<Integer,Boolean>) container.get("paddleRightStatuses");
							Map<Integer,Integer> paddleRightStatusesTimer = 
									(Map<Integer,Integer>) container.get("paddleRightStatusesTimer");
							splayerRight.setCoordinate(paddleRightX, paddleRightY);
							splayerRight.resize(paddleRightSize);
							if (paddleRightStatuses != null && paddleRightStatusesTimer != null) {
								splayerRight.setAllStatusesFlag(paddleRightStatuses);
								splayerRight.setAllTimers(paddleRightStatusesTimer);
							}
							
							// Data about ball
							int ballX = (int) container.get("ballX");
							int ballY = (int) container.get("ballY");
							sball.setCoordinate(ballX, ballY);
							
							// Scores
							int scoreLeft = (int) container.get("scoreLeft");
							int scoreRight = (int) container.get("scoreRight");
							splayerLeft.score = scoreLeft;
							splayerRight.score = scoreRight;
							
							// On field items
							List< Map<String,Integer> > shadowItems = (List< Map<String,Integer> >) container.get("fieldItems");
							((ShadowPongScene) pscene).setShadowItems(shadowItems);
							
							// Win status
							String winStatus = (String) container.get("winStatus");
							if (winStatus != null && winner == null) {
								if (winStatus.equalsIgnoreCase("win")) {
									win(pscene.myPlayer);
								} else if (winStatus.equalsIgnoreCase("lose")) {
									if (pscene.myPlayer == pscene.playerLeft) {
										win(pscene.playerRight);
									} else {
										win(pscene.playerLeft);
									}
								}
							}
							
							// Sound
							String sound = (String) container.get("sound");
							if (sound != null) {
								GameSound.playSound(sound);
								if (saveReplay) {
									replaySoundQueue.add(sound);
								}
							}
							
							if (GameEngine.isDebugOn()) {
								System.out.println(container);
							}
							
						} catch (ClassCastException e1) {
							e1.printStackTrace();
						}
						Thread.sleep(1);
					}
				} catch (IOException e) {
					connectionLost = true;
					if (winner == null) {
						e.printStackTrace();
					}
				} catch (InterruptedException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					/*
					 * Wait for client and server to ready before
					 * flooding data
					 */
					
					OutputStream out = socket.getOutputStream();
					out.write("READY".getBytes());
					out.flush();
					
					while (true) {
						byte[] inBytes = new byte[256];
						InputStream in;
							in = socket.getInputStream();
						in.read(inBytes);
						String inStr = new String(inBytes).trim();
						if (GameEngine.isDebugOn()) {
							System.out.println("serverResponse : " + inStr);
						}
						if (inStr.equals("READY")) {
							ready = true;
							break;
						}
						Thread.sleep(1);
					}
					
					outputData.start();
					inputData.start();
				} catch (IOException e) {
					connectionLost = true;
					if (winner == null) {
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * If winner is determined or connection lost
	 * notify the player
	 */
	@Override
	public void eventPreUpdate() {
		if (flag && winner != null) {
			ready = false;
			pscene.winGame(winner);
		} else if (connectionLost && winner == null) {
			ready = false;
			pscene.notifyConnectionLost();
		}
	}
	
	/**
	 * Flag if last frame after winner determined has been drawn
	 */
	@Override
	public void draw(Graphics2D canvas) {
		super.draw(canvas);
		if (winner != null) {
			flag = true;
		}
	}
	
	/**
	 * Hide inherited code...
	 */
	@Override
	public void playSound(String sound) {
		// do nothing
	}
	
}
