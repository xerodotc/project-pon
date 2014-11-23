/**
 * ServerController.java
 * 
 * A class for server session game controller
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.ingame.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import projectpon.engine.GameEngine;
import projectpon.engine.GameInput;
import projectpon.engine.GameNetwork;

public class ServerController extends LocalController {
	private Thread outputData; // output stream thread
	private Thread inputData; // input stream thread
	private GameInput remoteInput; // remote input
	private Socket socket; // socket
	
	// sound queue for sending to client
	private Queue<String> soundsQueue = new ArrayDeque<String>();
	
	private String remoteInputType = ""; // remote input type (mouse/keyboard)
	private int remoteKeySpeed; // remote keyboard speed
	
	private Boolean connectionLost = false; // connection lost flag
	
	/**
	 * Initialize and set socket timeout
	 * 
	 * @param socket	The socket for this game
	 */
	public ServerController(Socket socket) {
		super();
		try {
			socket.setSoTimeout(GameNetwork.DEFAULT_TIMEOUT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		remoteInput = new GameInput();
		GameInput.networkInputs.put(socket.hashCode(), remoteInput);
		this.socket = socket;
		this.networked = true;
		this.ready = false;
	}
	
	/**
	 * Initialize the output and input thread
	 */
	@Override
	public void eventOnCreate() {
		super.eventOnCreate();
		
		// set client player to be controlled by networked input
		pscene.playerRight.setInput(remoteInput);
		
		/*
		 * Send game informations to client
		 */
		outputData = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					while (true) {
						Map<String,Object> container = new HashMap<String,Object>();
						
						// Data about left player
						container.put("paddleLeftX", 
								pscene.playerLeft.getCoordinate().x);
						container.put("paddleLeftY", 
								pscene.playerLeft.getCoordinate().y);
						container.put("paddleLeftSize", 
								pscene.playerLeft.getSize());
						container.put("paddleLeftStatuses",
								pscene.playerLeft.getAllStatuses());
						container.put("paddleLeftStatusesTimer",
								pscene.playerLeft.getAllTimers());
						
						// Data about right player
						container.put("paddleRightX", 
								pscene.playerRight.getCoordinate().x);
						container.put("paddleRightY", 
								pscene.playerRight.getCoordinate().y);
						container.put("paddleRightSize", 
								pscene.playerRight.getSize());
						container.put("paddleRightStatuses",
								pscene.playerRight.getAllStatuses());
						container.put("paddleRightStatusesTimer",
								pscene.playerRight.getAllTimers());
						
						// Data about ball
						container.put("ballX", 
								pscene.ball.getCoordinate().x);
						container.put("ballY", 
								pscene.ball.getCoordinate().y);
						
						// Scores
						container.put("scoreLeft", pscene.playerLeft.score);
						container.put("scoreRight", pscene.playerRight.score);
						
						// On field items
						container.put("fieldItems", pscene.getShadowItems());
						
						// Win status
						if (winner != null) {
							container.put("winStatus", (pscene.myPlayer == winner) ?
									"lose" : "win");
						}
						
						// Sounds
						if (!soundsQueue.isEmpty()) {
							container.put("sound", soundsQueue.remove());
						}
						
						// SEND!
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
		 * Receive input data from client
		 */
		inputData = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
					while (true) {
						try {
							Map<String,Object> container = (Map<String,Object>) in.readObject();
							
							remoteInputType = (String) container.get("inputType");
							boolean inputLaunch;
							remoteInput.mouseOnScreen = true;
							
							switch (remoteInputType) {
							case "mouse":
								int inputX = (int) container.get("inputX");
								int inputY = (int) container.get("inputY");
								inputLaunch = (boolean) container.get("inputLaunch");
							
								remoteInput.setMouseCoordinate(inputX, inputY);
							
								if (inputLaunch != remoteInput.isMouseDown(MouseEvent.BUTTON1)) {
									if (inputLaunch) {
										remoteInput.setMousePressed(MouseEvent.BUTTON1);
									} else {
										remoteInput.setMouseReleased(MouseEvent.BUTTON1);
									}
								}
								break;
								
							case "keyboard":
								remoteKeySpeed = (int) container.get("inputSpeed");
								boolean inputUp = (boolean) container.get("inputUp");
								boolean inputDown = (boolean) container.get("inputDown");
								inputLaunch = (boolean) container.get("inputLaunch");
								
								if (inputLaunch != remoteInput.isKeyDown(KeyEvent.VK_SPACE)) {
									if (inputLaunch) {
										remoteInput.setKeyPressed(KeyEvent.VK_SPACE);
									} else {
										remoteInput.setKeyReleased(KeyEvent.VK_SPACE);
									}
								}
								
								if (inputUp != remoteInput.isKeyDown(KeyEvent.VK_UP)) {
									if (inputUp) {
										remoteInput.setKeyPressed(KeyEvent.VK_UP);
									} else {
										remoteInput.setKeyReleased(KeyEvent.VK_UP);
									}
								}
								
								if (inputDown != remoteInput.isKeyDown(KeyEvent.VK_DOWN)) {
									if (inputDown) {
										remoteInput.setKeyPressed(KeyEvent.VK_DOWN);
									} else {
										remoteInput.setKeyReleased(KeyEvent.VK_DOWN);
									}
								}
								break;
							}
							
							if (GameEngine.isDebugOn()) {
								System.out.println(container);
							}
						} catch (NumberFormatException e1) {
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
							System.out.println("clientResponse : " + inStr);
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
	 * Check winner and check connection lost
	 */
	@Override
	public void eventPreUpdate() {
		super.eventPreUpdate();
		if (connectionLost && winner == null) {
			ready = false;
			pscene.notifyConnectionLost();
		}
	}
	
	/**
	 * Play the sound and add to queue for client
	 */
	@Override
	public void playSound(String sound) {
		super.playSound(sound);
		soundsQueue.add(sound);
	}
	
	/**
	 * Is client using mouse?
	 * 
	 * @return True if client using mouse
	 */
	public boolean isRemoteUseMouse() {
		return (remoteInputType.equalsIgnoreCase("mouse"));
	}
	
	/**
	 * Get client keyboard speed
	 * 
	 * @return Client keyboard speed
	 */
	public int getRemoteKeySpeed() {
		return (remoteInputType.equalsIgnoreCase("keyboard")) ? remoteKeySpeed : 0;
	}
}
