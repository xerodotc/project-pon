package projectpon.game.scenes;

import java.util.List;
import java.util.Map;

import projectpon.engine.GameEngine;
import projectpon.engine.GameNetwork;
import projectpon.game.objects.ingame.Player;
import projectpon.game.objects.ingame.controllers.ClientController;
import projectpon.game.objects.ingame.shadow.ShadowBall;
import projectpon.game.objects.ingame.shadow.ShadowItems;
import projectpon.game.objects.ingame.shadow.ShadowPlayer;
import projectpon.game.objects.ingame.shadow.ShadowWall;

public class ShadowPongScene extends PongScene {
	
	private List< Map<String,Integer> > shadowItems;

	public ShadowPongScene() {
		super();
	}
	
	@Override
	public void initialize() {
		if (!useSocket) {
			System.err.println("Error");
			GameEngine.exit();
			return;
		}
		
		ball = new ShadowBall();
		if (useSocket) {
			controller = new ClientController(GameNetwork.getSocket());
		}
		wallLeft = new ShadowWall(LEFT_WALL_INIT_X,
				WALL_INIT_Y, Player.SIDE_LEFT);
		wallRight = new ShadowWall(RIGHT_WALL_INIT_X,
				WALL_INIT_Y, Player.SIDE_RIGHT);
		
		super.initialize();
		this.objectAdd(new ShadowItems());
	}
	
	public void setLeftPlayer() {
		this.setLeftPlayer(Player.PLAYER_SHADOW);
	}
	
	@Override
	public void setLeftPlayer(int type) {
		this.setLeftPlayer(type, false);
	}
	
	@Override
	public void setLeftPlayer(int type, boolean my) {
		if (playerLeft == null) {
			playerLeft = new ShadowPlayer(LEFT_PADDLE_INIT_X, PADDLE_INIT_Y,
					Player.SIDE_LEFT);
		}
		super.setLeftPlayer(type, my);
	}
	
	public void setRightPlayer() {
		this.setRightPlayer(Player.PLAYER_SHADOW);
	}
	
	@Override
	public void setRightPlayer(int type) {
		this.setRightPlayer(type, false);
	}
	
	@Override
	public void setRightPlayer(int type, boolean my) {
		if (playerRight == null) {
			playerRight = new ShadowPlayer(RIGHT_PADDLE_INIT_X, PADDLE_INIT_Y,
					Player.SIDE_RIGHT);
		}
		super.setRightPlayer(type, my);
	}
	
	public void setShadowItems(List< Map<String,Integer> > sil) {
		shadowItems = sil;
	}
	
	@Override
	public List< Map<String,Integer> > getShadowItems() {
		return shadowItems;
	}
}
