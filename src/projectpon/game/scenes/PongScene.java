package projectpon.game.scenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import projectpon.engine.GameEngine;
import projectpon.engine.GameScene;
import projectpon.engine.GameNetwork;
import projectpon.engine.GameSound;
import projectpon.engine.exceptions.NetworkException;
import projectpon.game.SessionConfiguration;
import projectpon.game.objects.ingame.Ball;
import projectpon.game.objects.ingame.Boundary;
import projectpon.game.objects.ingame.HUD;
import projectpon.game.objects.ingame.Item;
import projectpon.game.objects.ingame.Player;
import projectpon.game.objects.ingame.Wall;
import projectpon.game.objects.ingame.controllers.Controller;
import projectpon.game.objects.ingame.controllers.LocalController;
import projectpon.game.objects.ingame.controllers.ServerController;
import projectpon.game.objects.ingame.overlays.ConnectionLostOverlay;
import projectpon.game.objects.ingame.overlays.PausedOverlay;
import projectpon.game.objects.ingame.overlays.WinOverlay;

public class PongScene extends GameScene {
	public Player playerLeft;
	public Player playerRight;
	public Player myPlayer = null;
	public Player starting = null;
	protected Wall wallLeft;
	protected Wall wallRight;
	public Ball ball;
	public Controller controller;
	
	protected boolean useSocket = false;
	
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
	
	private boolean saveReplay = false;
	private String saveReplayFile;
	
	private Random rand = new Random();
	
	private List<Item> fieldItems = new ArrayList<Item>();
	
	public PongScene()  {
		super();
		
		int width = this.getWidth();
		int height = this.getHeight();
		
		LEFT_PADDLE_INIT_X = Player.PADDLE_WIDTH * 4;
		RIGHT_PADDLE_INIT_X = width - Player.PADDLE_WIDTH * 4;
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
		
		if (useSocket && GameNetwork.getSocket() == null) {
			System.err.println("Error");
			goToTitle();
			return;
		}
		
		if (ball == null) {
			ball = new Ball();
		}
		
		if (controller == null) {
			if (useSocket) {
				controller = new ServerController(GameNetwork.getSocket());
			} else {
				controller = new LocalController();
			}
		}
		
		if (saveReplay) {
			controller.setSaveReplay(saveReplayFile);
		}
		
		starting = playerLeft; // always left first
		
		if (useSocket) {
			GameEngine.disableExitOnClose(); // prevent leaver
		}
		
		if (wallLeft == null) {
			wallLeft = new Wall(LEFT_WALL_INIT_X,
					WALL_INIT_Y, Player.SIDE_LEFT);
		}
		if (wallRight == null) {
			wallRight = new Wall(RIGHT_WALL_INIT_X,
					WALL_INIT_Y, Player.SIDE_RIGHT);
		}
		
		this.objectAdd(controller);
		this.objectAdd(new Boundary());
		this.objectAdd(new HUD());
		this.objectAdd(wallLeft);
		this.objectAdd(wallRight);
		this.objectAdd(playerLeft);
		this.objectAdd(playerRight);
		this.objectAdd(ball);
		
		if (!useSocket) {
			this.objectAdd(new PausedOverlay());
		}
		
		GameSound.playMusic("ingame");
	}
	
	public void exit() {
		if (useSocket) {
			try {
				GameNetwork.clearSocket();
			} catch (NetworkException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void spawnBall() {
		ball = new Ball();
		this.objectAdd(ball);
	}
	
	public void addScore(Player winner) {
		Player other = playerLeft;
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
			if ((winner.score - other.score) >= SessionConfiguration.minimumWinDiff) {
				controller.win(winner);
			}
		}
	}
	
	public void winGame(Player winner) {
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
	
	public void setSaveReplay(String file) {
		saveReplay = true;
		saveReplayFile = file;
	}
	
	public void notifyConnectionLost() {
		this.objectAdd(new ConnectionLostOverlay());
	}
	
	public void setLeftPlayer(int type, boolean my) {
		if (playerLeft == null) {
			playerLeft = new Player(LEFT_PADDLE_INIT_X,
					PADDLE_INIT_Y, Player.PADDLE_INITSIZE,
					Player.SIDE_LEFT);
		}
		playerLeft.setPlayerType(type);
		if (my) {
			myPlayer = playerLeft;
		}
		
		if (type >= Player.PLAYER_REMOTE) {
			useSocket = true;
		}
	}
	
	public void setLeftPlayer(int type) {
		setLeftPlayer(type, false);
	}
	
	public void setRightPlayer(int type, boolean my) {
		if (playerRight == null) {
			playerRight = new Player(RIGHT_PADDLE_INIT_X,
					PADDLE_INIT_Y, Player.PADDLE_INITSIZE, 
					Player.SIDE_RIGHT);
		}
		playerRight.setPlayerType(type);
		if (my) {
			myPlayer = playerRight;
		}
		
		if (type >= Player.PLAYER_REMOTE) {
			useSocket = true;
		}
	}
	
	public void setRightPlayer(int type) {
		setRightPlayer(type, false);
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
