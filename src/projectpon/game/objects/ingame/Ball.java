/**
 * Ball.java
 * 
 * A class for a ball object
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.ingame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import projectpon.engine.GameEngine;
import projectpon.engine.GameObject;
import projectpon.game.scenes.PongScene;

public class Ball extends GameObject {
	public static final int BALL_SIZE = 8; // ball size
	
	public boolean launched = false; // is the ball, launched?
	
	protected PongScene pscene = null; // the PongScene
	
	private int oldX = 0; // old x-position
	private int oldY = 0; // old y-position
	private int actualDx = 0; // actual x-velocity (backup in case of pausing)
	private int actualDy = 0; // actual y-velocity (backup in case of pausing)
	
	// the difference of y-position of paddle and ball (in case of not launched)
	private int delta = 0;
	
	/**
	 * Initialize ball properties
	 */
	public Ball() {
		this.z = -1;
		this.anchorX = BALL_SIZE / 2;
		this.anchorY = BALL_SIZE / 2;
		this.collisionRect.width = this.collisionRect.height = BALL_SIZE;
		this.visible = true;
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
	 * Get ball's top boundary
	 * 
	 * @return Ball's top boundary
	 */
	private int getTop() {
		return this.y - BALL_SIZE / 2;
	}
	
	/**
	 * Get ball's bottom boundary
	 * 
	 * @return Ball's bottom boundary
	 */
	private int getBottom() {
		return this.y + BALL_SIZE / 2;
	}
	
	/**
	 * Get ball's trajectory as line
	 * 
	 * @return Ball's trajectory
	 */
	public Line2D getTrajectory() {
		if (!launched) {
			return new Line2D.Double(this.x, this.y, this.x, this.y);
		}
		
		return new Line2D.Double(this.oldX, this.oldY, this.x, this.y);
	}
	
	/**
	 * Set visibility (in case of blinding)
	 */
	protected void setVisibility() {
		if (pscene.myPlayer != null) {
			if (pscene.myPlayer.getStatus(Player.STATUS_BLIND)) {
				this.visible = true;
				if (getCoordinate().distance(pscene.myPlayer.getCoordinate()) > 
						pscene.myPlayer.getSize()) {
					this.visible = false;
				}
			} else {
				this.visible = true;
			}
		}
	}
	
	/**
	 * Pre-update event
	 */
	@Override
	public void eventPreUpdate() {
		setVisibility(); // set ball visibility
		
		this.oldX = this.x;
		this.oldY = this.y;
		/*
		 * If the game is paused or not ready...
		 * set velocity to zero
		 */
		if (!pscene.controller.isReady() || pscene.controller.isPaused()) {
			this.dx = 0;
			this.dy = 0;
		} else {
			this.dx = this.actualDx;
			this.dy = this.actualDy;
		}
	}
	
	/**
	 * Post-update event
	 */
	@Override
	public void eventPostUpdate() {
		if (!launched) {
			/*
			 * If the ball hasn't launched,
			 * keep stick to the paddle
			 */
			this.x = pscene.starting.getCoordinate().x;
			this.y = pscene.starting.getCoordinate().y;
			this.y += delta;
			
			if (pscene.starting.paddleSide == Player.SIDE_LEFT) {
				this.x += BALL_SIZE / 2;
			} else {
				this.x -= BALL_SIZE / 2;
			}
		} else {
			/*
			 * Is the ball hit the top or bottom of screen?
			 */
			if (getBottom() > pscene.getBottomBoundary()) {
				this.y = pscene.getBottomBoundary() - BALL_SIZE / 2;
				reverseY();
				pscene.controller.playSound("beep");
			} else if (getTop() < pscene.getTopBoundary()) {
				this.y = pscene.getTopBoundary() + BALL_SIZE / 2;
				reverseY();
				pscene.controller.playSound("beep");
			}
			
			/*
			 * Is the ball fall of the edge?
			 */
			if (this.x > scene.getWidth() + BALL_SIZE &&
					!pscene.playerRight.getStatus(Player.STATUS_WALL)) {
				// left-side player win
				pscene.starting = pscene.playerRight;
				this.destroy();
				pscene.spawnBall();
				pscene.addScore(pscene.playerLeft);
				pscene.controller.playSound("boop");
			} else if (this.x < 0 - BALL_SIZE &&
					!pscene.playerLeft.getStatus(Player.STATUS_WALL)) {
				// right-side player win
				pscene.starting = pscene.playerLeft;
				this.destroy();
				pscene.spawnBall();
				pscene.addScore(pscene.playerRight);
				pscene.controller.playSound("boop");
			}
		}
	}
	
	/**
	 * Draw the ball
	 */
	@Override
	public void draw(Graphics2D canvas) {
		canvas.setColor(Color.CYAN);
		canvas.fill(this.collisionBox());
		if (GameEngine.isDebugOn()) {
			canvas.draw(getTrajectory());
		}
	}
	
	/**
	 * Launch the ball
	 */
	public void launch() {
		this.launched = true;
		this.actualDx = 3;
		this.actualDy = 3;
		if ((this.y < scene.getHeight() / 2 && this.delta >= -4 && this.delta <= 4) ||
				this.delta < -4) {
			this.actualDy *= -1;
		}
		if (pscene.starting.paddleSide == Player.SIDE_RIGHT) {
			this.actualDx *= -1;
		}
		this.delta = 0;
		pscene.starting = null;
	}
	
	/**
	 * Reverse ball x-velocity
	 */
	public void reverseX() {
		this.actualDx *= -1;
	}
	
	/**
	 * Reverse ball y-velocity
	 */
	public void reverseY() {
		this.actualDy *= -1;
	}
	
	/**
	 * Set the ball coordinate
	 * 
	 * @param x		x-coordinate
	 * @param y		y-coordinate
	 */
	public void setCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Get the ball direction (-1 or 1)
	 * 
	 * @return -1 if moving toward left side, 1 if moving toward right side
	 */
	public int getDirection() {
		if (this.actualDx == 0) {
			return 0;
		}
		return this.actualDx / Math.abs(this.actualDx);
	}
	
	/**
	 * Add the speed to the ball (limit is 50)
	 */
	public void addSpeed() {
		if (Math.abs(this.actualDx) < 50) {
			this.actualDx += (this.actualDx < 0) ? -1 : 1;
			this.actualDy += (this.actualDy < 0) ? -1 : 1;
		}
	}
	
	/**
	 * Stick the ball to paddle
	 * 
	 * @param stick		Stick to which paddle
	 */
	public void stick(Player stick) {
		launched = false;
		delta = this.y - stick.getCoordinate().y;
		pscene.starting = stick;
	}
}
