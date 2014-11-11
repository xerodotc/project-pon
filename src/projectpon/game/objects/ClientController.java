package projectpon.game.objects;

import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import projectpon.engine.GameEngine;
import projectpon.engine.GameInput;
import projectpon.engine.GameSound;
import projectpon.game.Configuration;
import projectpon.game.objects.shadow.*;
import projectpon.game.scenes.ShadowPongScene;

public class ClientController extends LocalController {
	private Thread outputData;
	private Thread inputData;
	private Socket socket;
	
	private boolean flag = false;
	private boolean connectionLost = false;
	
	public ClientController(Socket socket) {
		super();
		this.socket = socket;
		this.client = true;
		this.visible = true; // force trigger draw event
		this.networked = true;
		this.ready = false;
	}
	
	@Override
	public void eventOnCreate() {
		super.eventOnCreate();
		
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
		
		inputData = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
					ShadowBall sball = (ShadowBall) pscene.ball;
					ShadowPaddle splayerLeft = (ShadowPaddle) pscene.playerLeft;
					ShadowPaddle splayerRight = (ShadowPaddle) pscene.playerRight;
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
	
	@Override
	public void draw(Graphics2D canvas) {
		if (winner != null) {
			flag = true;
		}
	}
	
	@Override
	public void playSound(String sound) {
		// do nothing
	}
	
}
