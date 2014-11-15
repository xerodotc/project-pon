package projectpon.game.objects.ingame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

import projectpon.engine.GameEngine;
import projectpon.engine.GameObject;
import projectpon.game.Configuration;
import projectpon.game.objects.ingame.controllers.ServerController;
import projectpon.game.scenes.PongScene;

public class Player extends GameObject {
	public static final int PADDLE_WIDTH = 8;
	public static final int PADDLE_INITSIZE = 64;
	public static final int PADDLE_SIZEDELTA = 16;
	
	public static final int SIDE_LEFT = -1;
	public static final int SIDE_RIGHT = 1;
	
	public static final int PLAYER_REPLAY = -1;
	public static final int PLAYER_LOCAL = 0;
	public static final int PLAYER_AI = 1;
	public static final int PLAYER_REMOTE = 2;
	public static final int PLAYER_SHADOW = 3;
	
	public static final int MIN_SIZE = 16;
	public static final int MAX_SIZE = 256;
	
	public static final int STATUS_WALL = -1;
	public static final int STATUS_STICKY = 0;
	public static final int STATUS_BLIND = 1;
	public static final int STATUS_INVERT = 2;
	
	public static final int STATUS_SECONDS = 10;
	
	protected int paddleSize = 128;
	protected int paddleSide;
	protected PongScene pscene = null;
	
	private boolean remoteControlled = false;
	private int aiLaunchTick = -1;
	private boolean aiControlled = false;
	
	public int score = 0;
	
	protected boolean sticky = false;
	protected boolean blind = false;
	protected int invert = 1;
	protected boolean wall = false;
	
	protected int[] statusTimer = new int[3];
	
	private int colorAlternationTick = 0;
	private Color colorAlternationCurrent = Color.GREEN;
	
	public Player(int x, int y, int size, int side) {
		super(x, y);
		
		this.z = 0;
		if (side != SIDE_LEFT && side != SIDE_RIGHT) {
			side = SIDE_LEFT;
		}
		this.paddleSide = side;
		this.paddleSize = size;
		this.anchorY = this.paddleSize / 2;
		if (side == SIDE_LEFT) {
			this.anchorX = PADDLE_WIDTH;
		} else {
			this.anchorX = 0;
		}
		this.collisionRect.width = PADDLE_WIDTH;
		this.collisionRect.height = this.paddleSize;
		this.visible = true;
		this.score = 0;
	}
	
	public void setPlayerType(int playerType) {
		if (playerType == PLAYER_REMOTE) {
			this.remoteControlled = true;
		} else if (playerType == PLAYER_AI) {
			this.aiControlled = true;
		}
	}
	
	@Override
	public void eventOnCreate() {
		if (scene instanceof PongScene) {
			pscene = (PongScene) scene;
		}
	}
	
	private int getTop() {
		return this.y - this.paddleSize / 2;
	}
	
	private int getBottom() {
		return this.y + this.paddleSize / 2;
	}
	
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
	
	public int getSize() {
		return this.paddleSize;
	}
	
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
	
	@Override
	public void eventPreUpdate() {
		if (pscene.controller.isPaused()) {
			return;
		}
		
		tickStatus();
		setVisibility();
		
		if (this.remoteControlled) {
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
			String inputOwner = "Primary";
			if (paddleSide == SIDE_RIGHT) {
				inputOwner = "Secondary";
			}
			
			switch (Configuration.get("input" + inputOwner + "Player", "type")) {
			case "mouse":
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
		
		if (getTop() < pscene.getTopBoundary()) {
			this.y = pscene.getTopBoundary() + this.paddleSize / 2;
		} else if (getBottom() > pscene.getBottomBoundary()) {
			this.y = pscene.getBottomBoundary() - this.paddleSize / 2;
		}
	}
	
	@Override
	public void eventPostUpdate() {
		Line2D trajectory = pscene.ball.getTrajectory();
		
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
	
	public void expand() {
		resize(this.paddleSize + PADDLE_SIZEDELTA);
	}
	
	public void shrink() {
		resize(this.paddleSize - PADDLE_SIZEDELTA);
	}
	
	public void unblind() {
		if (getStatus(STATUS_BLIND)) {
			this.statusTimer[STATUS_BLIND] = 1;
		}
	}
	
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
	
	public void wallDestroyed() {
		this.wall = false;
	}
	
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
	
	public Map<Integer,Boolean> getAllStatuses() {
		Map<Integer,Boolean> s = new HashMap<Integer,Boolean>();
		
		s.put(STATUS_WALL, this.wall);
		s.put(STATUS_STICKY, this.sticky);
		s.put(STATUS_BLIND, this.blind);
		s.put(STATUS_INVERT, (this.invert < 0));
		
		return s;
	}
	
	public int getTimer(int status) {
		if (status >= 0 && status < this.statusTimer.length) {
			return (this.statusTimer[status] > 0) ? this.statusTimer[status] : 0;
		}
		
		return 0;
	}
	
	public Map<Integer,Integer> getAllTimers() {
		Map<Integer,Integer> t = new HashMap<Integer,Integer>();
		
		t.put(STATUS_STICKY, statusTimer[STATUS_STICKY]);
		t.put(STATUS_BLIND, statusTimer[STATUS_BLIND]);
		t.put(STATUS_INVERT, statusTimer[STATUS_INVERT]);
		
		return t;
	}
}
