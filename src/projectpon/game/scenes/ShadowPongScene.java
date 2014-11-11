package projectpon.game.scenes;

import java.util.List;
import java.util.Map;

import projectpon.engine.GameEngine;
import projectpon.engine.exceptions.NetworkException;
import projectpon.engine.net.NetworkManager;
import projectpon.game.objects.ClientController;
import projectpon.game.objects.Paddle;
import projectpon.game.objects.shadow.ShadowBall;
import projectpon.game.objects.shadow.ShadowItems;
import projectpon.game.objects.shadow.ShadowPaddle;
import projectpon.game.objects.shadow.ShadowWall;

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
		controller = new ClientController(NetworkManager.getSocket());
		wallLeft = new ShadowWall(LEFT_WALL_INIT_X,
				WALL_INIT_Y, Paddle.SIDE_LEFT);
		wallRight = new ShadowWall(RIGHT_WALL_INIT_X,
				WALL_INIT_Y, Paddle.SIDE_RIGHT);
		
		super.initialize();
		this.objectAdd(new ShadowItems());
	}
	
	public void setLeftPlayer() {
		this.setLeftPlayer(Paddle.PLAYER_SHADOW);
	}
	
	@Override
	public void setLeftPlayer(int type) {
		this.setLeftPlayer(type, false);
	}
	
	@Override
	public void setLeftPlayer(int type, boolean my) {
		if (playerLeft == null) {
			playerLeft = new ShadowPaddle(LEFT_PADDLE_INIT_X, PADDLE_INIT_Y,
					Paddle.SIDE_LEFT);
		}
		super.setLeftPlayer(type, my);
	}
	
	public void setRightPlayer() {
		this.setRightPlayer(Paddle.PLAYER_SHADOW);
	}
	
	@Override
	public void setRightPlayer(int type) {
		this.setRightPlayer(type, true);
	}
	
	@Override
	public void setRightPlayer(int type, boolean my) {
		if (playerRight == null) {
			playerRight = new ShadowPaddle(RIGHT_PADDLE_INIT_X, PADDLE_INIT_Y,
					Paddle.SIDE_RIGHT);
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
