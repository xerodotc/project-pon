/**
 * TitleMenu.java
 * 
 * A class for title menu
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.title;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import projectpon.engine.GameEngine;
import projectpon.engine.GameFont;
import projectpon.engine.GameObject;
import projectpon.engine.GameSound;
import projectpon.game.SessionConfiguration;
import projectpon.game.dialogs.ConnectDialog;
import projectpon.game.dialogs.NewGameDialog;
import projectpon.game.dialogs.OptionsDialog;
import projectpon.game.objects.ingame.Player;
import projectpon.game.scenes.AboutScene;
import projectpon.game.scenes.HelpScene;
import projectpon.game.scenes.PongScene;
import projectpon.game.scenes.ShadowPongScene;

public class TitleMenu extends GameObject {
	private boolean activated = false; // is the menu activated
	
	// is "press left mouse button" text visible
	private boolean pressToActivateVisible = true; 
	
	// "press left mouse button" text font
	private Font pressToActivateFont = GameFont.getFont("advocut").
			deriveFont(Font.PLAIN, 56);
	// choices font
	private Font menuFont = GameFont.getFont("advocut").
			deriveFont(Font.PLAIN, 24);
	
	private int menuCenterX, menuCenterY; // menu center
	
	private static final int MENU_WIDTH = 500; // menu width
	private static final int MENU_HEIGHT = 340; // menu height
	private static final int MENU_BORDER = 8; // menu border
	
	// "press left mouse button" visibility toggle initial ticks
	private static final int PRESS_TO_ACTIVATE_TICKS_INIT = 10;
	// "press left mouse button" visibility toggle ticks
	private int pressToActivateTicks = PRESS_TO_ACTIVATE_TICKS_INIT;
	
	// choice rectangle bounds
	private Rectangle[] choiceRect = new Rectangle[8];
	// choice current color
	private Color[] choiceColor = new Color[8];
	// current choice
	private int selectedOption = 0;
	// choice texts
	private String[] choiceText = {"Play against computer",
			"Local 2-players game",
			"Host a network game",
			"Connect to a network game",
			"Options",
			"Help",
			"About",
			"Exit"};
	
	/*
	 * Define choice ID
	 */
	private static final int CHOICE_VS_1P = 0;
	private static final int CHOICE_VS_2P = 1;
	private static final int CHOICE_SERVER = 2;
	private static final int CHOICE_CLIENT = 3;
	private static final int CHOICE_OPTIONS = 4;
	private static final int CHOICE_HELP = 5;
	private static final int CHOICE_ABOUT = 6;
	private static final int CHOICE_EXIT = 7;
	
	/**
	 * Initialize the menu
	 * 
	 * @param preActivate	Is the menu should be already activated?
	 */
	public TitleMenu(boolean preActivate) {
		super();
		
		this.z = -20;
		this.visible = true;
		this.activated = preActivate;
		
		// initialize menu color
		for (int i = 0; i < choiceColor.length; i++) {
			choiceColor[i] = Color.BLACK;
		}
	}
	
	/**
	 * Default constructor
	 */
	public TitleMenu() {
		this(true);
	}
	
	/**
	 * Determine menu center
	 */
	@Override
	public void eventOnCreate() {
		menuCenterX = scene.getWidth() / 2;
		menuCenterY = scene.getHeight() / 2 + 64;
	}
	
	/**
	 * Listen for user input
	 */
	@Override
	public void eventPreUpdate() {
		boolean triggered = input.isMouseReleased(MouseEvent.BUTTON1);
		
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
			
			for (int i = 0; i < choiceColor.length; i++) {
				if (i == selectedOption) {
					choiceColor[i] = Color.RED;
				} else {
					choiceColor[i] = Color.BLACK;
				}
			}
		}
		
		/*
		 * If menu not activated and "triggered"
		 * activate the menu
		 * Else if activated and "triggered"
		 * perform selected action
		 */
		if (triggered) {
			if (!activated) {
				activated = true;
				GameSound.playSound("beep");
			} else {
				action(selectedOption);
			}
		}
	}
	
	/**
	 * Draw the menu
	 */
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
				String text = "PRESS LEFT MOUSE BUTTON";
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
	}
	
	/**
	 * Perform selected choice action
	 * 
	 * @param choice	Selected choice
	 */
	private void action(int choice) {
		final PongScene pscene;
		
		switch (choice) {
		// Player against computer
		case CHOICE_VS_1P:
			pscene = new PongScene();
			pscene.setLeftPlayer(Player.PLAYER_LOCAL, true);
			pscene.setRightPlayer(Player.PLAYER_AI, false);
			if (SessionConfiguration.saveReplayFile != null) {
				pscene.setSaveReplay(SessionConfiguration.saveReplayFile);
			}
			GameEngine.launchDialog(new NewGameDialog(pscene));
			break;
			
		// Local 2-players game
		case CHOICE_VS_2P:
			pscene = new PongScene();
			pscene.setLeftPlayer(Player.PLAYER_LOCAL);
			pscene.setRightPlayer(Player.PLAYER_LOCAL);
			if (SessionConfiguration.saveReplayFile != null) {
				pscene.setSaveReplay(SessionConfiguration.saveReplayFile);
			}
			GameEngine.launchDialog(new NewGameDialog(pscene));
			break;
			
		// Host a network game
		case CHOICE_SERVER:
			pscene = new PongScene();
			pscene.setLeftPlayer(Player.PLAYER_LOCAL, true);
			pscene.setRightPlayer(Player.PLAYER_REMOTE, false);
			if (SessionConfiguration.saveReplayFile != null) {
				pscene.setSaveReplay(SessionConfiguration.saveReplayFile);
			}
			GameEngine.launchDialog(new NewGameDialog(pscene, true));
			break;
			
		// Connect to a network game
		case CHOICE_CLIENT:
			pscene = new ShadowPongScene();
			pscene.setLeftPlayer(Player.PLAYER_SHADOW, false);
			pscene.setRightPlayer(Player.PLAYER_SHADOW, true);
			if (SessionConfiguration.saveReplayFile != null) {
				pscene.setSaveReplay(SessionConfiguration.saveReplayFile);
			}
			GameEngine.launchDialog(new ConnectDialog(pscene));
			break;
			
		// Options
		case CHOICE_OPTIONS:
			GameEngine.launchDialog(new OptionsDialog());
			break;
		
		// Help
		case CHOICE_HELP:
			GameEngine.setScene(new HelpScene());
			break;
		
		// About
		case CHOICE_ABOUT:
			GameEngine.setScene(new AboutScene());
			break;
		
		// Exit
		case CHOICE_EXIT:
			GameEngine.exit();
			break;
		}
	}
	
	/**
	 * Is menu activated?
	 * 
	 * @return	True if menu is activated
	 */
	public boolean isActivated() {
		return activated;
	}
}
