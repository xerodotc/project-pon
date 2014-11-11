package projectpon.game.objects.title;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

import projectpon.engine.GameEngine;
import projectpon.engine.GameFont;
import projectpon.engine.GameObject;
import projectpon.engine.GameSound;
import projectpon.engine.GameWindow;
import projectpon.game.Configuration;
import projectpon.game.objects.Paddle;
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
			triggered = input.isMousePressed(
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
			triggered = input.isKeyPressed(
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
		
		try {
			PongScene pscene;
			Socket remote = null;
			
			switch (choice) {
			case CHOICE_VS_1P:
				pscene = new PongScene();
				pscene.setLeftPlayer(Paddle.PLAYER_LOCAL, true);
				pscene.setRightPlayer(Paddle.PLAYER_AI, false);
				GameEngine.setScene(pscene);
				break;
				
			case CHOICE_VS_2P:
				pscene = new PongScene();
				pscene.setLeftPlayer(Paddle.PLAYER_LOCAL);
				pscene.setRightPlayer(Paddle.PLAYER_LOCAL);
				GameEngine.setScene(pscene);
				break;
				
			case CHOICE_SERVER:
				ServerSocket server = new ServerSocket(10215);
				remote = server.accept();
				server.close();
				pscene = new PongScene();
				pscene.setSocket(remote);
				pscene.setLeftPlayer(Paddle.PLAYER_LOCAL, true);
				pscene.setRightPlayer(Paddle.PLAYER_REMOTE, false);
				GameEngine.setScene(pscene);
				break;
				
			case CHOICE_CLIENT:
				String host = JOptionPane.showInputDialog(null, "Host address", "127.0.0.1");
				try {
					remote = new Socket(host, 10215);
					pscene = new ShadowPongScene();
					pscene.setSocket(remote);
					pscene.setLeftPlayer(Paddle.PLAYER_SHADOW, false);
					pscene.setRightPlayer(Paddle.PLAYER_SHADOW, true);
					GameEngine.setScene(pscene);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "Can't connect to server!",
							"Error", JOptionPane.ERROR_MESSAGE);
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
		} catch (IOException e) {
			e.printStackTrace();
			GameEngine.exit();
		}
	}
}
