/**
 * Player.java
 * 
 * A class for player/paddle objects
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game.objects.ingame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

import th.in.xerodotc.projectpon.engine.GameEngine;
import th.in.xerodotc.projectpon.engine.GameObject;
import th.in.xerodotc.projectpon.game.Configuration;
import th.in.xerodotc.projectpon.game.objects.ingame.controllers.ServerController;
import th.in.xerodotc.projectpon.game.scenes.PongScene;

public class Player extends GameObject {
	public static final int PADDLE_WIDTH = 8; // paddle width
	public static final int PADDLE_INITSIZE = 64; // paddle initial size
	// paddle size difference when expanding or shrinking
	public static final int PADDLE_SIZEDELTA = 16;
	
	// define player side
	public static final int SIDE_LEFT = -1;
	public static final int SIDE_RIGHT = 1;
	
	// define player type
	public static final int PLAYER_REPLAY = -1;
	public static final int PLAYER_LOCAL = 0;
	public static final int PLAYER_AI = 1;
	public static final int PLAYER_REMOTE = 2;
	public static final int PLAYER_SHADOW = 3;
	
	public static final int MIN_SIZE = 16; // minimum paddle size
	public static final int MAX_SIZE = 256; // maximum paddle size
	
	// define player status
	public static final int STATUS_WALL = -1;
	public static final int STATUS_STICKY = 0;
	public static final int STATUS_BLIND = 1;
	public static final int STATUS_INVERT = 2;
	
	// default status effects duration in seconds
	public static final int STATUS_SECONDS = 10;
	
	protected int paddleSize; // storing paddle size
	protected int paddleSide; // storing paddle side
	protected PongScene pscene = null; // the PongScene
	
	private boolean remoteControlled = false; // is this player controlled by remote
	private int aiLaunchTick = -1; // AI player launch delay
	private boolean aiControlled = false; // is this player controlled by computer
	
	public int score = 0; // the player score
	
	protected boolean sticky = false; // sticky status
	protected boolean blind = false; // blind status
	protected int invert = 1; // invert status
	protected boolean wall = false; // wall status
	
	protected int[] statusTimer = new int[3]; // each status timers
	
	private int colorAlternationTick = 0; // color alternation tick
	private Color colorAlternationCurrent = Color.GREEN; // current color
	
	/**
	 * Initialize player
	 * 
	 * @param x		x-position
	 * @param y		y-position
	 * @param size	Paddle size
	 * @param side	Side of paddle
	 */
	public Player(int x, int y, int size, int side) {
		super(x, y);
		
		this.z = 0;
		if (side != SIDE_LEFT && side != SIDE_RIGHT) {
			side = SIDE_LEFT;
		}
		this.paddleSide = side;
		this.paddleSize = size;
		// setup paddle anchor point
		this.anchorY = this.paddleSize / 2;
		if (side == SIDE_LEFT) {
			this.anchorX = PADDLE_WIDTH;
		} else {
			this.anchorX = 0;
		}
		// setup paddle collision box
		this.collisionRect.width = PADDLE_WIDTH;
		this.collisionRect.height = this.paddleSize;
		this.visible = true;
		this.score = 0;
	}
	
	/**
	 * Set player type
	 * 
	 * @param playerType	Player type
	 */
	public void setPlayerType(int playerType) {
		if (playerType == PLAYER_REMOTE) {
			this.remoteControlled = true;
		} else if (playerType == PLAYER_AI) {
			this.aiControlled = true;
		}
	}
	
	/**
	 * Assign PongScene
	 */
	@Override
	public void eventOnCreate() {
		if (scene instanceof PongScene) {
			pscene = (PongScene) scene;
		}
	}
	
	/**
	 * Get paddle top boundary
	 * 
	 * @return Paddle top boundary
	 */
	private int getTop() {
		return this.y - this.paddleSize / 2;
	}
	
	/**
	 * Get paddle bottom boundary
	 * 
	 * @return Paddle bottom boundary
	 */
	private int getBottom() {
		return this.y + this.paddleSize / 2;
	}
	
	/**
	 * Resize the paddle
	 * 
	 * @param size		New size of paddle
	 */
	public void resize(int size) {
		if (size < MIN_SIZE) {
			size = MIN_SIZE;
		} else if (size > MAX_SIZE) {
			size = MAX_SIZE;
		}
		this.paddleSize = size;
		this.collisionRect.height = this.paddleSize;
		this.anchorY = this.paddleSize / 2;
	}
	
	/**
	 * Get current size of paddle
	 * 
	 * @return Size of paddle
	 */
	public int getSize() {
		return this.paddleSize;
	}
	
	/**
	 * Set paddle visibility (in case of blinding)
	 */
	protected void setVisibility() {
		if (pscene.myPlayer != null) {
			if (pscene.myPlayer.getStatus(Player.STATUS_BLIND)
					&& this != pscene.myPlayer) {
				this.visible = false;
			} else {
				this.visible = true;
			}
		}
	}
	
	/**
	 * Pre-update event
	 * (Update status tick, set visibility and polling user input)
	 */
	@Override
	public void eventPreUpdate() {
		if (pscene.controller.isPaused()) {
			return;
		}
		
		tickStatus(); // update status tick
		setVisibility(); // set paddle visibility
		
		/*
		 * Poll the input
		 */
		if (this.remoteControlled) {
			/*
			 * Remote controlled
			 */
			if (pscene.controller instanceof ServerController) {
				ServerController scontroller = (ServerController) pscene.controller;
				if (scontroller.isRemoteUseMouse()) {
					this.y = input.getMouseY();
					if (invert < 0) {
						this.y = pscene.getHeight() - this.y;
					}
					if (!pscene.ball.launched && this == pscene.starting) {
						if (input.isMouseReleased(MouseEvent.BUTTON1)) {
							pscene.ball.launch();
						}
					}
				} else {
					if (input.isKeyDown(KeyEvent.VK_UP)) {
						this.y -= invert * scontroller.getRemoteKeySpeed();
					}
					if (input.isKeyDown(KeyEvent.VK_DOWN)) {
						this.y += invert * scontroller.getRemoteKeySpeed();
					}
					if (!pscene.ball.launched && this == pscene.starting) {
						if (input.isKeyReleased(KeyEvent.VK_SPACE)) {
							pscene.ball.launch();
						}
					}
				}
			}
		} else if (this.aiControlled) {
			/*
			 * Controlled by computer
			 */
			if (!pscene.ball.launched && this == pscene.starting) {
				if (aiLaunchTick < 0) {
					aiLaunchTick = 30;
				}
				if (aiLaunchTick <= 0) {
					pscene.ball.launch();
				}
				aiLaunchTick--;
			} else {
				if (Configuration.getBoolean("aiOptions", "cheat")) {
					this.y = pscene.ball.getCoordinate().y;
				} else {
					if (this.y < pscene.ball.getCoordinate().y) {
						this.y += Configuration.getInt("aiOptions", "speed");
						if (this.y > pscene.ball.getCoordinate().y) {
							this.y = pscene.ball.getCoordinate().y;
						}
					} else if (this.y > pscene.ball.getCoordinate().y) {
						this.y -= Configuration.getInt("aiOptions", "speed");
						if (this.y < pscene.ball.getCoordinate().y) {
							this.y = pscene.ball.getCoordinate().y;
						}
					}
				}
			}
		} else {
			/*
			 * Controlled by local player
			 */
			String inputOwner = "Primary";
			if (paddleSide == SIDE_RIGHT) {
				inputOwner = "Secondary";
			}
			
			switch (Configuration.get("input" + inputOwner + "Player", "type")) {
			case "mouse":
				/*
				 * Mouse
				 */
				int mbLaunch = Configuration.getInt("input" + inputOwner + "Player", "mbLaunch");
				this.y = input.getMouseY();
				if (invert < 0) {
					this.y = pscene.getHeight() - this.y;
				}
				if (!pscene.ball.launched && this == pscene.starting) {
					if (input.isMouseReleased(mbLaunch)) {
						pscene.ball.launch();
					}
				}
				break;
				
			case "keyboard":
				/*
				 * Keyboard
				 */
				int keyUp = Configuration.getInt("input" + inputOwner + "Player", "keyUp");
				int keyDown = Configuration.getInt("input" + inputOwner + "Player", "keyDown");
				int keyLaunch = Configuration.getInt("input" + inputOwner + "Player", "keyLaunch");
				int keySpeed = Configuration.getInt("input" + inputOwner + "Player", "keySpeed");
				
				if (input.isKeyDown(keyUp)) {
					this.y -= invert * keySpeed;
				}
				if (input.isKeyDown(keyDown)) {
					this.y += invert * keySpeed;
				}
				if (!pscene.ball.launched && this == pscene.starting) {
					if (input.isKeyReleased(keyLaunch)) {
						pscene.ball.launch();
					}
				}
				break;
			}
		}
		
		/*
		 * Block the paddle going beyond the boundary
		 */
		if (getTop() < pscene.getTopBoundary()) {
			this.y = pscene.getTopBoundary() + this.paddleSize / 2;
		} else if (getBottom() > pscene.getBottomBoundary()) {
			this.y = pscene.getBottomBoundary() - this.paddleSize / 2;
		}
	}
	
	/**
	 * Post-update event
	 */
	@Override
	public void eventPostUpdate() {
		Line2D trajectory = pscene.ball.getTrajectory(); // get ball trajectory
		
		/*
		 * If the ball collided with the paddle,
		 * bounce back the ball
		 */
		if (this.collisionBox().intersectsLine(trajectory)) {
			int targetX = this.x;
			targetX += -this.paddleSide * Ball.BALL_SIZE / 2;
			boolean backSide = false;
			if (pscene.ball.getDirection() != this.paddleSide) {
				// hit from the back
				targetX = this.x;
				targetX += this.paddleSide * PADDLE_WIDTH;
				targetX += this.paddleSide * Ball.BALL_SIZE / 2;
				backSide = true;
			}
			pscene.ball.reverseX();
			double targetY = trajectory.getY2() - trajectory.getY1();
			targetY /= trajectory.getX2() - trajectory.getX1();
			targetY *= targetX - trajectory.getX1();
			targetY += trajectory.getY1();
			pscene.ball.setCoordinate(targetX, (int) Math.round(targetY));
			pscene.ball.addSpeed();
			pscene.controller.playSound("boop");
			if (this.sticky && !backSide) {
				pscene.ball.stick(this);
			}
		}
	}
	
	/**
	 * Draw the paddle
	 */
	@Override
	public void draw(Graphics2D canvas) {
		canvas.setColor(Color.WHITE);
		if (this.invert < 0 && this.sticky) {
			if (colorAlternationTick <= 0) {
				if (colorAlternationCurrent == Color.GREEN) {
					colorAlternationCurrent = Color.RED;
				} else if (colorAlternationCurrent == Color.RED) {
					colorAlternationCurrent = Color.GREEN;
				}
			}
			canvas.setColor(colorAlternationCurrent);
			if (!pscene.controller.isPaused() || colorAlternationTick == 0) {
				colorAlternationTick--;
			}
			if (colorAlternationTick < 0) {
				colorAlternationTick = 10;
			}
		} else if (this.sticky) {
			canvas.setColor(Color.GREEN);
		} else if (this.invert < 0) {
			canvas.setColor(Color.RED);
		}
		canvas.fill(this.collisionBox());
	}
	
	/**
	 * Expand the paddle
	 */
	public void expand() {
		resize(this.paddleSize + PADDLE_SIZEDELTA);
	}
	
	/**
	 * Shirnk the paddle
	 */
	public void shrink() {
		resize(this.paddleSize - PADDLE_SIZEDELTA);
	}
	
	/**
	 * Unblind the player (cheat)
	 */
	public void unblind() {
		if (getStatus(STATUS_BLIND)) {
			this.statusTimer[STATUS_BLIND] = 1;
		}
	}
	
	/**
	 * Uninvert the player (cheat)
	 */
	public void uninvert() {
		if (getStatus(STATUS_INVERT)) {
			this.statusTimer[STATUS_INVERT] = 1;
		}
	}
	
	/**
	 * Set the player status
	 * 
	 * @param status	Player status
	 */
	public void setStatus(int status) {
		switch (status) {
		case STATUS_WALL:
			this.wall = true;
			break;
		
		case STATUS_STICKY:
			this.sticky = true;
			this.statusTimer[STATUS_STICKY] = STATUS_SECONDS * GameEngine.getUpdatesPerSec();
			break;
			
		case STATUS_BLIND:
			this.blind = true;
			this.statusTimer[STATUS_BLIND] = STATUS_SECONDS * GameEngine.getUpdatesPerSec();
			break;
			
		case STATUS_INVERT:
			this.invert = -1;
			this.statusTimer[STATUS_INVERT] = STATUS_SECONDS * GameEngine.getUpdatesPerSec();
			break;
		}
	}
	
	/**
	 * To flag that the wall is destroyed
	 */
	public void wallDestroyed() {
		this.wall = false;
	}
	
	/**
	 * Update status tick/timer
	 */
	public void tickStatus() {
		for (int i = 0; i < this.statusTimer.length; i++) {
			if (this.statusTimer[i] <= 0 && getStatus(i)) {
				switch (i) {
				case STATUS_STICKY:
					this.sticky = false;
					break;
					
				case STATUS_BLIND:
					this.blind = false;
					break;
					
				case STATUS_INVERT:
					this.invert = 1;
				}
			}
			if (this.statusTimer[i] > 0) {
				this.statusTimer[i]--;
			}
		}
	}
	
	/**
	 * Get player's specified status
	 * 
	 * @param status	Status
	 * @return True, if player has specified status
	 */
	public boolean getStatus(int status) {
		switch (status) {
		case STATUS_WALL:
			return this.wall;
		
		case STATUS_STICKY:
			return this.sticky;
			
		case STATUS_BLIND:
			return this.blind;
			
		case STATUS_INVERT:
			return (this.invert < 0);
		}
		
		return false;
	}
	
	/**
	 * Get player's all statuses
	 * 
	 * @return A map of player statuses
	 */
	public Map<Integer,Boolean> getAllStatuses() {
		Map<Integer,Boolean> s = new HashMap<Integer,Boolean>();
		
		s.put(STATUS_WALL, this.wall);
		s.put(STATUS_STICKY, this.sticky);
		s.put(STATUS_BLIND, this.blind);
		s.put(STATUS_INVERT, (this.invert < 0));
		
		return s;
	}
	
	/**
	 * Get timer of player's specified status
	 * 
	 * @param status	Status
	 * @return Timer of player's specified status
	 */
	public int getTimer(int status) {
		if (status >= 0 && status < this.statusTimer.length) {
			return (this.statusTimer[status] > 0) ? this.statusTimer[status] : 0;
		}
		
		return 0;
	}
	
	/**
	 * Get timers of player's all statuses
	 * 
	 * @return A map of timers of player's all statuses
	 */
	public Map<Integer,Integer> getAllTimers() {
		Map<Integer,Integer> t = new HashMap<Integer,Integer>();
		
		t.put(STATUS_STICKY, statusTimer[STATUS_STICKY]);
		t.put(STATUS_BLIND, statusTimer[STATUS_BLIND]);
		t.put(STATUS_INVERT, statusTimer[STATUS_INVERT]);
		
		return t;
	}
}
