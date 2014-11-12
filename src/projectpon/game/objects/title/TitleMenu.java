package projectpon.game.objects.title;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

import projectpon.engine.GameEngine;
import projectpon.engine.GameFont;
import projectpon.engine.GameObject;
import projectpon.engine.GameSound;
import projectpon.engine.GameNetwork;
import projectpon.engine.exceptions.NetworkException;
import projectpon.engine.exceptions.NetworkLockException;
import projectpon.game.Configuration;
import projectpon.game.SessionConfiguration;
import projectpon.game.dialogs.NewGameDialog;
import projectpon.game.objects.ingame.Player;
import projectpon.game.scenes.PongScene;
import projectpon.game.scenes.ShadowPongScene;

public class TitleMenu extends GameObject {
	private boolean activated = false;
	
	private boolean pressToActivateVisible = true;
	
	private Font pressToActivateFont = GameFont.getFont("advocut").
			deriveFont(Font.PLAIN, 64);
	private Font menuFont = GameFont.getFont("advocut").
			deriveFont(Font.PLAIN, 24);
	
	private int menuCenterX, menuCenterY;
	
	private static final int MENU_WIDTH = 500;
	private static final int MENU_HEIGHT = 300;
	private static final int MENU_BORDER = 8;
	
	private static final int PRESS_TO_ACTIVATE_TICKS_INIT = 10;
	private int pressToActivateTicks = PRESS_TO_ACTIVATE_TICKS_INIT;
	
	private Rectangle[] choiceRect = new Rectangle[7];
	private Color[] choiceColor = new Color[7];
	private int selectedOption = 0;
	private String[] choiceText = {"Play against computer",
			"Local 2-players game",
			"Host a network game",
			"Connect to a network game",
			"Options",
			"Help",
			"Exit"};
	
	private static final int CHOICE_VS_1P = 0;
	private static final int CHOICE_VS_2P = 1;
	private static final int CHOICE_SERVER = 2;
	private static final int CHOICE_CLIENT = 3;
	private static final int CHOICE_OPTIONS = 4;
	private static final int CHOICE_HELP = 5;
	private static final int CHOICE_EXIT = 6;
	
	private boolean flag = false;
	
	public TitleMenu(boolean preActivate) {
		super();
		
		this.z = -20;
		this.visible = true;
		this.activated = preActivate;
		
		for (int i = 0; i < choiceColor.length; i++) {
			choiceColor[i] = Color.BLACK;
		}
	}
	
	public TitleMenu() {
		this(true);
	}
	
	@Override
	public void eventOnCreate() {
		menuCenterX = scene.getWidth() / 2;
		menuCenterY = scene.getHeight() / 2 + 64;
	}
	
	@Override
	public void eventPreUpdate() {
		boolean triggered = false;
		
		switch (Configuration.get("inputPrimaryPlayer", "type")) {
		case "mouse":
			triggered = input.isMouseReleased(
					Configuration.getInt("inputPrimaryPlayer", "mbLaunch"));
			
			if (activated) {
				selectedOption = -1;
				for (int i = 0; i < choiceRect.length; i++) {
					if (choiceRect[i] == null) {
						continue;
					}
					if (choiceRect[i].contains(input.getMouseCoordinate())) {
						selectedOption = i;
						break;
					}
				}
			}
			break;
			
		case "keyboard":
			triggered = input.isKeyReleased(
					Configuration.getInt("inputPrimaryPlayer", "keyLaunch"));
			
			if (activated) {
				if (input.isKeyPressed(
						Configuration.getInt("inputPrimaryPlayer", "keyUp"))) {
					selectedOption--;
				}
				if (input.isKeyPressed(
						Configuration.getInt("inputPrimaryPlayer", "keyDown"))) {
					selectedOption++;
				}
				
				if (selectedOption < 0) {
					selectedOption = choiceRect.length - 1;
				}
				if (selectedOption >= choiceRect.length) {
					selectedOption = 0;
				}
			}
			break;
		}
		
		if (activated) {
			for (int i = 0; i < choiceColor.length; i++) {
				if (i == selectedOption) {
					choiceColor[i] = Color.RED;
				} else {
					choiceColor[i] = Color.BLACK;
				}
			}
		}
		
		if (triggered) {
			if (!activated) {
				activated = true;
				GameSound.playSound("beep");
			} else {
				action(selectedOption);
			}
		}
	}
	
	@Override
	public void draw(Graphics2D canvas) {
		if (!activated) {
			if (pressToActivateTicks <= 0) {
				pressToActivateVisible = !pressToActivateVisible;
				pressToActivateTicks = PRESS_TO_ACTIVATE_TICKS_INIT + 1;
			}
			pressToActivateTicks--;
			
			if (pressToActivateVisible) {
				canvas.setFont(pressToActivateFont);
				canvas.setColor(Color.WHITE);
				String text = "PRESS LAUNCH BUTTON";
				FontMetrics metrics = canvas.getFontMetrics();
				int width = metrics.stringWidth(text);
				int height = metrics.getHeight();
				canvas.drawString(text, menuCenterX - width / 2,
						menuCenterY + height / 2);
			}
			return;
		}
		
		canvas.setColor(Color.GRAY);
		canvas.fillRect(menuCenterX - MENU_WIDTH / 2 - MENU_BORDER,
				menuCenterY - MENU_HEIGHT / 2 - MENU_BORDER,
				MENU_WIDTH + MENU_BORDER * 2,
				MENU_HEIGHT + MENU_BORDER * 2);
		canvas.setColor(Color.WHITE);
		canvas.fillRect(menuCenterX - MENU_WIDTH / 2,
				menuCenterY - MENU_HEIGHT / 2,
				MENU_WIDTH, MENU_HEIGHT);
		
		int startingY = menuCenterY - MENU_HEIGHT / 2 + 24;
		canvas.setFont(menuFont);
		FontMetrics metrics = canvas.getFontMetrics();
		for (int i = 0; i < choiceText.length; i++) {
			canvas.setColor(choiceColor[i]);
			Rectangle2D bound = metrics.getStringBounds(choiceText[i], canvas);
			int x = menuCenterX - (int) Math.round(bound.getWidth()) / 2;
			int y = startingY;
			startingY += bound.getHeight() + 16;
			canvas.drawString(choiceText[i], x, y + Math.round(bound.getHeight()));
			choiceRect[i] = new Rectangle(x, y,
					(int) Math.round(bound.getWidth()),
					(int) Math.round(bound.getHeight()));
		}
		
		if (GameEngine.isDebugOn() && !flag) {
			Rectangle rect = choiceRect[0];
			System.out.println(rect.x + " " + rect.y + " " + 
					(rect.x + rect.width) + " " + (rect.y + rect.width));
			flag = true;
		}
	}
	
	private void action(int choice) {
		/**
		 * TODO: Actual menu action, options, help
		 */

		final PongScene pscene;
		
		switch (choice) {
		case CHOICE_VS_1P:
			pscene = new PongScene();
			pscene.setLeftPlayer(Player.PLAYER_LOCAL, true);
			pscene.setRightPlayer(Player.PLAYER_AI, false);
			GameEngine.launchDialog(new NewGameDialog(pscene));
			break;
			
		case CHOICE_VS_2P:
			pscene = new PongScene();
			pscene.setLeftPlayer(Player.PLAYER_LOCAL);
			pscene.setRightPlayer(Player.PLAYER_LOCAL);
			GameEngine.launchDialog(new NewGameDialog(pscene));
			break;
			
		case CHOICE_SERVER:
			pscene = new PongScene();
			pscene.setLeftPlayer(Player.PLAYER_LOCAL, true);
			pscene.setRightPlayer(Player.PLAYER_REMOTE, false);
			GameEngine.pause();
			try {
				GameNetwork.Server.addOnAcceptedListener(
						new GameNetwork.Server.AcceptListener() {
							@Override
							public void onAccepted(Socket remote) {
								try {
									OutputStream out = remote.getOutputStream();
									InputStream in = remote.getInputStream();
									
									out.write(
											String.format("%d %d %d",
													SessionConfiguration.minimumWinScore,
													SessionConfiguration.maximumWinScore,
													SessionConfiguration.minimumWinDiff).getBytes());
									out.flush();
									
									byte[] buffer = new byte[256];
									in.read(buffer);
									String response = new String(buffer).trim();
									if (Boolean.parseBoolean(response)) {
										GameNetwork.setSocket(remote);
										try {
											GameNetwork.Server.stop();
										} catch (NetworkException e1) {
											e1.printStackTrace();
										}
										GameEngine.setScene(pscene);
										GameEngine.unpause();
									} else {
										remote.close();
										GameNetwork.Server.acceptClient();
									}
								} catch (IOException e) {
									e.printStackTrace();
									GameNetwork.Server.acceptClient();
								}
							}
						});
				
				GameNetwork.Server.start(10215);
				GameNetwork.Server.acceptClient();
			} catch (NetworkException | NetworkLockException e) {
				e.printStackTrace();
			}
			break;
			
		case CHOICE_CLIENT:
			pscene = new ShadowPongScene();
			pscene.setLeftPlayer(Player.PLAYER_SHADOW, false);
			pscene.setRightPlayer(Player.PLAYER_SHADOW, true);
			GameEngine.pause();
			String host = JOptionPane.showInputDialog(null, "Host address", "127.0.0.1");
			if (host == null) {
				GameEngine.unpause();
				return;
			}
			
			try {
				GameNetwork.Client.addOnConnectedListener(
						new GameNetwork.Client.ConnectListener() {
							@Override
							public void onConnected(Socket remote) {
								try {
									InputStream in = remote.getInputStream();
									OutputStream out = remote.getOutputStream();
									
									byte[] buffer = new byte[256];
									in.read(buffer);
									String data = new String(buffer).trim();
									String[] score = data.split(" ");
									
									if (score.length < 2) {
										out.write("false".getBytes());
										throw new Exception();
									}
									
									String conditionsString = "Game conditions\n";
									conditionsString += "-------------------------\n";
									conditionsString += "Minimum winning score: " + score[0] + "\n";
									conditionsString += "Maximum winning score: " + score[1] + "\n";
									conditionsString += "Required score difference: " + score[2] + "\n";
									conditionsString += "-------------------------\n";
								
									int confirm = JOptionPane.showConfirmDialog(
											null, conditionsString + "\nAccept this server's conditions?", "Confirmation",
											JOptionPane.OK_CANCEL_OPTION);
									
									switch (confirm) {
									case JOptionPane.OK_OPTION:
										out.write("true".getBytes());
										GameNetwork.setSocket(remote);
										GameEngine.setScene(pscene);
										GameEngine.unpause();
										break;
										
									case JOptionPane.CANCEL_OPTION:
										try {
											GameEngine.unpause();
											out.write("false".getBytes());
											GameNetwork.Client.disconnect();
										} catch (NetworkException | IOException e) {
											e.printStackTrace();
										}
										break;
									}
								} catch (Exception e1) {
									JOptionPane.showMessageDialog(null, "Can't connect to server!",
											"Error", JOptionPane.ERROR_MESSAGE);
									GameEngine.unpause();
								}
							}
						});
				GameNetwork.Client.connect(host, 10215);
			} catch (NetworkException e) {
				JOptionPane.showMessageDialog(null, "Can't connect to server!",
						"Error", JOptionPane.ERROR_MESSAGE);
				GameEngine.unpause();
			} catch (NetworkLockException e) {
				e.printStackTrace();
			}
			break;
			
		case CHOICE_OPTIONS:
			break;
			
		case CHOICE_HELP:
			break;
		
		case CHOICE_EXIT:
			GameEngine.exit();
			break;
		}
	}
}
