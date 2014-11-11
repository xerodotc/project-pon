package projectpon.game.scenes;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import projectpon.engine.GameEngine;
import projectpon.engine.GameScene;
import projectpon.game.SessionConfiguration;
import projectpon.game.objects.*;
import projectpon.game.objects.overlay.ConnectionLostOverlay;
import projectpon.game.objects.overlay.PausedOverlay;
import projectpon.game.objects.overlay.WinOverlay;

public class PongScene extends GameScene {
	public Paddle playerLeft;
	public Paddle playerRight;
	public Paddle myPlayer = null;
	public Paddle starting = null;
	protected Wall wallLeft;
	protected Wall wallRight;
	public Ball ball;
	public Controller controller;
	
	protected Socket remote = null;
	
	public static final int TOP_BOUNDARY = 128;
	public static final int BOTTOM_BOUNDARY = 128;
	public static final int FIELDITEMS_LIMIT = 16;
	
	protected final int LEFT_PADDLE_INIT_X;
	protected final int RIGHT_PADDLE_INIT_X;
	protected final int PADDLE_INIT_Y;
	protected final int LEFT_WALL_INIT_X;
	protected final int RIGHT_WALL_INIT_X;
	protected final int WALL_INIT_Y;
	
	protected final int ITEM_SPAWN_MIN_X;
	protected final int ITEM_SPAWN_MIN_Y;
	protected final int ITEM_SPAWN_MAX_X;
	protected final int ITEM_SPAWN_MAX_Y;
	
	private Random rand = new Random();
	
	private List<Item> fieldItems = new ArrayList<Item>();
	
	public PongScene()  {
		super();
		
		int width = this.getWidth();
		int height = this.getHeight();
		
		LEFT_PADDLE_INIT_X = Paddle.PADDLE_WIDTH * 4;
		RIGHT_PADDLE_INIT_X = width - Paddle.PADDLE_WIDTH * 4;
		PADDLE_INIT_Y = WALL_INIT_Y = height / 2;
		LEFT_WALL_INIT_X = Wall.WALL_WIDTH;
		RIGHT_WALL_INIT_X = width - Wall.WALL_WIDTH;
		
		ITEM_SPAWN_MIN_X = width / 2 - width / 4;
		ITEM_SPAWN_MAX_X = width / 2 + width / 4;
		ITEM_SPAWN_MIN_Y = TOP_BOUNDARY + Item.ITEMBOX_SIZE;
		ITEM_SPAWN_MAX_Y = height - BOTTOM_BOUNDARY - Item.ITEMBOX_SIZE;
	}

	@Override
	public void initialize() {
		if (playerLeft == null || playerRight == null) {
			System.err.println("Error");
			goToTitle();
			return;
		}
		
		if (ball == null) {
			ball = new Ball();
		}
		
		if (controller == null) {
			if (remote != null) {
				controller = new ServerController(remote);
			} else {
				controller = new LocalController();
			}
		}
		
		starting = playerLeft; // always left first
		
		if (remote != null) {
			GameEngine.disableExitOnClose(); // prevent leaver
		}
		
		if (wallLeft == null) {
			wallLeft = new Wall(LEFT_WALL_INIT_X,
					WALL_INIT_Y, Paddle.SIDE_LEFT);
		}
		if (wallRight == null) {
			wallRight = new Wall(RIGHT_WALL_INIT_X,
					WALL_INIT_Y, Paddle.SIDE_RIGHT);
		}
		
		this.objectAdd(controller);
		this.objectAdd(new Boundary());
		this.objectAdd(new HUD());
		this.objectAdd(wallLeft);
		this.objectAdd(wallRight);
		this.objectAdd(playerLeft);
		this.objectAdd(playerRight);
		this.objectAdd(ball);
		
		if (remote == null) {
			this.objectAdd(new PausedOverlay());
		}
	}
	
	public void exit() {
		if (remote != null) {
			try {
				remote.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void spawnBall() {
		ball = new Ball();
		this.objectAdd(ball);
	}
	
	public void addScore(Paddle winner) {
		Paddle other = playerLeft;
		if (winner == playerLeft) {
			other = playerRight;
		}
		
		winner.score++;
		
		/*
		 * Relay win event to controller
		 * Making last frame being rendered
		 */
		if (winner.score >= SessionConfiguration.maximumWinScore) {
			controller.win(winner);
		} else if (winner.score >= SessionConfiguration.minimumWinScore) {
			if ((winner.score - other.score) > 1) {
				controller.win(winner);
			}
		}
	}
	
	public void winGame(Paddle winner) {
		if (myPlayer == null) {
			if (winner == playerLeft) {
				this.objectAdd(new WinOverlay("Player 1 win!"));
			} else if (winner == playerRight) {
				this.objectAdd(new WinOverlay("Player 2 win!"));
			}
		} else {
			if (winner == myPlayer) {
				this.objectAdd(new WinOverlay("You win!"));
			} else {
				this.objectAdd(new WinOverlay("You lose!"));
			}
		}
	}
	
	public void notifyConnectionLost() {
		this.objectAdd(new ConnectionLostOverlay());
	}
	
	public void setLeftPlayer(int type, boolean my) {
		if (playerLeft == null) {
			playerLeft = new Paddle(LEFT_PADDLE_INIT_X,
					PADDLE_INIT_Y, Paddle.PADDLE_INITSIZE,
					Paddle.SIDE_LEFT);
		}
		playerLeft.setPlayerType(type);
		if (my) {
			myPlayer = playerLeft;
		}
	}
	
	public void setLeftPlayer(int type) {
		setLeftPlayer(type, false);
	}
	
	public void setRightPlayer(int type, boolean my) {
		if (playerRight == null) {
			playerRight = new Paddle(RIGHT_PADDLE_INIT_X,
					PADDLE_INIT_Y, Paddle.PADDLE_INITSIZE, 
					Paddle.SIDE_RIGHT);
		}
		playerRight.setPlayerType(type);
		if (my) {
			myPlayer = playerRight;
		}
	}
	
	public void setRightPlayer(int type) {
		setRightPlayer(type, false);
	}
	
	public void setSocket(Socket socket) {
		this.remote = socket;
	}
	
	public void goToTitle() {
		GameEngine.setScene(new TitleScene());
	}
	
	public int getTopBoundary() {
		return TOP_BOUNDARY;
	}
	
	public int getBottomBoundary() {
		return this.getHeight() - BOTTOM_BOUNDARY;
	}
	
	public void addItem(Item i) {
		this.fieldItems.add(i);
	}
	
	public void removeItem(Item i) {
		this.fieldItems.remove(i);
	}
	
	public void spawnItem() {
		int type;
		if (myPlayer == null) {
			type = rand.nextInt(Item.ITEM_BLIND);
		} else {
			type = rand.nextInt(Item.ITEM_BLIND + 1);
		}
		
		spawnItem(type);
	}
	
	public void spawnItem(int type) {
		if (fieldItems.size() >= FIELDITEMS_LIMIT) {
			return;
		}
		
		int x = rand.nextInt(ITEM_SPAWN_MAX_X - ITEM_SPAWN_MIN_X + 1) + 
				ITEM_SPAWN_MIN_X;
		int y = rand.nextInt(ITEM_SPAWN_MAX_Y - ITEM_SPAWN_MIN_Y + 1) + 
				ITEM_SPAWN_MIN_Y;
		
		this.objectAdd(new Item(x, y, type));
	}
	
	public List<Item> getFieldItems() {
		return fieldItems;
	}
	
	public List< Map<String,Integer> > getShadowItems() {
		List< Map<String,Integer> > is = new ArrayList< Map<String,Integer> >();
		for (Item i : fieldItems) {
			Map<String,Integer> imap = new HashMap<String,Integer>();
			imap.put("x", i.getCoordinate().x);
			imap.put("y", i.getCoordinate().y);
			imap.put("type", i.getType());
			is.add(imap);
		}
		
		return is;
	}
}
