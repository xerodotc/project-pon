package projectpon.game.objects.ingame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import projectpon.engine.GameObject;
import projectpon.game.scenes.PongScene;

public class Wall extends GameObject {
	public static final int WALL_WIDTH = 8;
	
	protected int wallSide;
	protected PongScene pscene = null;
	
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
		this.visible = false;
	}
	
	@Override
	public void eventOnCreate() {
		if (scene instanceof PongScene) {
			pscene = (PongScene) scene;
		}
		
		this.collisionRect.height = pscene.getHeight() - 
				PongScene.TOP_BOUNDARY - PongScene.BOTTOM_BOUNDARY;
		this.anchorY = this.collisionRect.height / 2;
	}
	
	protected void setVisibility() {
		if (pscene.myPlayer != null) {
			if (wallSide != pscene.myPlayer.paddleSide) {
				if (pscene.myPlayer.getStatus(Player.STATUS_BLIND)) {
					this.visible = false;
				}
			}
		}
	}
	
	@Override
	public void eventPreUpdate() {
		Player ownerPaddle;
		if (wallSide == Player.SIDE_LEFT) {
			ownerPaddle = pscene.playerLeft;
		} else {
			ownerPaddle = pscene.playerRight;
		}
		
		if (!ownerPaddle.getStatus(Player.STATUS_WALL)) {
			this.visible = false;
			return;
		}
		
		this.visible = true;
		setVisibility();
	}
	
	@Override
	public void eventPostUpdate() {
		Player ownerPaddle;
		if (wallSide == Player.SIDE_LEFT) {
			ownerPaddle = pscene.playerLeft;
		} else {
			ownerPaddle = pscene.playerRight;
		}
		
		if (!ownerPaddle.getStatus(Player.STATUS_WALL)) {
			return;
		}
		
		Line2D trajectory = pscene.ball.getTrajectory();
		
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
	
	@Override
	public void draw(Graphics2D canvas) {
		canvas.setColor(Color.WHITE);
		canvas.fill(this.collisionBox());
	}
}
