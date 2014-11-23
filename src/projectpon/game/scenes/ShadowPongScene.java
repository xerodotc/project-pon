/**
 * ShadowPongScene.java
 * 
 * A scene for Pong game (without internal physics)
 */

package projectpon.game.scenes;

import java.util.List;
import java.util.Map;

import projectpon.engine.GameEngine;
import projectpon.engine.GameNetwork;
import projectpon.game.objects.ingame.Player;
import projectpon.game.objects.ingame.controllers.ClientController;
import projectpon.game.objects.ingame.controllers.ReplayController;
import projectpon.game.objects.ingame.shadow.ShadowBall;
import projectpon.game.objects.ingame.shadow.ShadowItems;
import projectpon.game.objects.ingame.shadow.ShadowPlayer;
import projectpon.game.objects.ingame.shadow.ShadowWall;

public class ShadowPongScene extends PongScene {
	
	private List< Map<String,Integer> > shadowItems; // list of on-field items
	private boolean useReplay = false; // use replay?
	private String replayFile; // replay file to be loaded from

	/**
	 * Constructor
	 */
	public ShadowPongScene() {
		super();
	}
	
	/**
	 * Initialize the scene
	 */
	@Override
	public void initialize() {
		if (!useSocket && !useReplay) {
			System.err.println("Error");
			GameEngine.exit();
			return;
		}
		
		ball = new ShadowBall();
		if (useSocket) {
			// as client
			controller = new ClientController(GameNetwork.getSocket());
		} else if (useReplay) {
			// as replay viewer
			controller = new ReplayController(replayFile);
		}
		wallLeft = new ShadowWall(LEFT_WALL_INIT_X,
				WALL_INIT_Y, Player.SIDE_LEFT);
		wallRight = new ShadowWall(RIGHT_WALL_INIT_X,
				WALL_INIT_Y, Player.SIDE_RIGHT);
		
		super.initialize();
		this.objectAdd(new ShadowItems());
	}
	
	/**
	 * Setup left player
	 */
	public void setLeftPlayer() {
		this.setLeftPlayer(Player.PLAYER_SHADOW);
	}
	
	/**
	 * Setup left player
	 * 
	 * @param type	Player type
	 */
	@Override
	public void setLeftPlayer(int type) {
		this.setLeftPlayer(type, false);
	}
	
	/**
	 * Setup left player
	 * 
	 * @param type		Player type
	 * @param my		Is my player?
	 */
	@Override
	public void setLeftPlayer(int type, boolean my) {
		if (playerLeft == null) {
			playerLeft = new ShadowPlayer(LEFT_PADDLE_INIT_X, PADDLE_INIT_Y,
					Player.SIDE_LEFT);
		}
		super.setLeftPlayer(type, my);
	}
	
	/**
	 * Setup right player
	 */
	public void setRightPlayer() {
		this.setRightPlayer(Player.PLAYER_SHADOW);
	}
	
	/**
	 * Setup right player
	 * 
	 * @param type		Player type
	 */
	@Override
	public void setRightPlayer(int type) {
		this.setRightPlayer(type, false);
	}
	
	/**
	 * Setup right player
	 * 
	 * @param type		Player type
	 * @param my		Is my player?
	 */
	@Override
	public void setRightPlayer(int type, boolean my) {
		if (playerRight == null) {
			playerRight = new ShadowPlayer(RIGHT_PADDLE_INIT_X, PADDLE_INIT_Y,
					Player.SIDE_RIGHT);
		}
		super.setRightPlayer(type, my);
	}
	
	/**
	 * Setup replay file to be loaded
	 * 
	 * @param file		File name
	 */
	public void setReplayFile(String file) {
		useReplay = true;
		replayFile = file;
	}
	
	/**
	 * Set on-field items
	 * 
	 * @param sil		On-field items list
	 */
	public void setShadowItems(List< Map<String,Integer> > sil) {
		shadowItems = sil;
	}
	
	/**
	 * Get on-field items
	 * 
	 * @return On-field items list
	 */
	@Override
	public List< Map<String,Integer> > getShadowItems() {
		return shadowItems;
	}
}
