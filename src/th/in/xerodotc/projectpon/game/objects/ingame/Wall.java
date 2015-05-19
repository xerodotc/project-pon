/**
 * Wall.java
 * 
 * A class for wall objects
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.game.objects.ingame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import th.in.xerodotc.projectpon.engine.GameObject;
import th.in.xerodotc.projectpon.game.scenes.PongScene;

public class Wall extends GameObject {
	public static final int WALL_WIDTH = 8; // wall width
	
	protected int wallSide; // side of the wall
	protected PongScene pscene = null; // the PongScene
	
	/**
	 * Initialize the wall object
	 * 
	 * @param x		x-position
	 * @param y		y-position
	 * @param side	Side of the wall
	 */
	public Wall(int x, int y, int side) {
		super(x, y);
		
		this.z = -1;
		if (side != Player.SIDE_LEFT && side != Player.SIDE_RIGHT) {
			side = Player.SIDE_LEFT;
		}
		this.wallSide = side;
		this.anchorY = 0;
		if (side == Player.SIDE_LEFT) {
			this.anchorX = WALL_WIDTH;
		} else {
			this.anchorX = 0;
		}
		this.collisionRect.width = WALL_WIDTH;
		this.visible = false; // hide the wall at first
	}
	
	/**
	 * Assign PongScene and expand the wall to fit the boundary
	 */
	@Override
	public void eventOnCreate() {
		if (scene instanceof PongScene) {
			pscene = (PongScene) scene;
		}
		
		this.collisionRect.height = pscene.getHeight() - 
				PongScene.TOP_BOUNDARY - PongScene.BOTTOM_BOUNDARY;
		this.anchorY = this.collisionRect.height / 2;
	}
	
	/**
	 * Set wall visibility (in case of blinding)
	 */
	protected void setVisibility() {
		if (pscene.myPlayer != null) {
			if (wallSide != pscene.myPlayer.paddleSide) {
				if (pscene.myPlayer.getStatus(Player.STATUS_BLIND)) {
					this.visible = false;
				}
			}
		}
	}
	
	/**
	 * Pre-update event
	 */
	@Override
	public void eventPreUpdate() {
		Player ownerPaddle;
		if (wallSide == Player.SIDE_LEFT) {
			ownerPaddle = pscene.playerLeft;
		} else {
			ownerPaddle = pscene.playerRight;
		}
		
		// hide the wall, if the player doesn't have the wall
		if (!ownerPaddle.getStatus(Player.STATUS_WALL)) {
			this.visible = false;
			return;
		}
		
		this.visible = true;
		setVisibility();
	}
	
	/**
	 * Post-update event
	 */
	@Override
	public void eventPostUpdate() {
		Player ownerPaddle;
		if (wallSide == Player.SIDE_LEFT) {
			ownerPaddle = pscene.playerLeft;
		} else {
			ownerPaddle = pscene.playerRight;
		}
		
		// do nothing, if player doesn't have the wall
		if (!ownerPaddle.getStatus(Player.STATUS_WALL)) {
			return;
		}
		
		Line2D trajectory = pscene.ball.getTrajectory();
		
		/*
		 * Bounce back the ball and destroy/hide the wall
		 */
		if (wallSide * pscene.ball.getCoordinate().x >= wallSide * this.x && 
				pscene.ball.getDirection() == wallSide) {
			int targetX = this.x;
			targetX += -this.wallSide * Ball.BALL_SIZE / 2;
			pscene.ball.reverseX();
			double targetY = trajectory.getY2() - trajectory.getY1();
			targetY /= trajectory.getX2() - trajectory.getX1();
			targetY *= targetX - trajectory.getX1();
			targetY += trajectory.getY1();
			pscene.ball.setCoordinate(targetX, (int) Math.round(targetY));
			pscene.ball.addSpeed();
			pscene.controller.playSound("boop");
			
			ownerPaddle.wallDestroyed();
			this.visible = false;
		}
	}
	
	/**
	 * Draw the wall
	 */
	@Override
	public void draw(Graphics2D canvas) {
		canvas.setColor(Color.WHITE);
		canvas.fill(this.collisionBox());
	}
}
