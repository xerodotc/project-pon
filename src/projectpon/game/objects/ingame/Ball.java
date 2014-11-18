package projectpon.game.objects.ingame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import projectpon.engine.GameEngine;
import projectpon.engine.GameObject;
import projectpon.game.scenes.PongScene;

public class Ball extends GameObject {
	public static final int BALL_SIZE = 8;
	
	public boolean launched = false;
	
	protected PongScene pscene = null;
	
	private int oldX = 0;
	private int oldY = 0;
	private int actualDx = 0;
	private int actualDy = 0;
	
	private int delta = 0;
	
	public Ball() {
		this.z = -1;
		this.anchorX = BALL_SIZE / 2;
		this.anchorY = BALL_SIZE / 2;
		this.collisionRect.width = this.collisionRect.height = BALL_SIZE;
		this.visible = true;
	}
	
	@Override
	public void eventOnCreate() {
		if (scene instanceof PongScene) {
			pscene = (PongScene) scene;
		}
	}
	
	private int getTop() {
		return this.y - BALL_SIZE / 2;
	}
	
	private int getBottom() {
		return this.y + BALL_SIZE / 2;
	}
	
	public Line2D getTrajectory() {
		if (!launched) {
			return new Line2D.Double(this.x, this.y, this.x, this.y);
		}
		
		return new Line2D.Double(this.oldX, this.oldY, this.x, this.y);
	}
	
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
	
	@Override
	public void eventPreUpdate() {
		setVisibility();
		
		this.oldX = this.x;
		this.oldY = this.y;
		if (!pscene.controller.isReady() || pscene.controller.isPaused()) {
			this.dx = 0;
			this.dy = 0;
		} else {
			this.dx = this.actualDx;
			this.dy = this.actualDy;
		}
	}
	
	@Override
	public void eventPostUpdate() {
		if (!launched) {
			this.x = pscene.starting.getCoordinate().x;
			this.y = pscene.starting.getCoordinate().y;
			this.y += delta;
			
			if (pscene.starting.paddleSide == Player.SIDE_LEFT) {
				this.x += BALL_SIZE / 2;
			} else {
				this.x -= BALL_SIZE / 2;
			}
		} else {
			if (getBottom() > pscene.getBottomBoundary()) {
				this.y = pscene.getBottomBoundary() - BALL_SIZE / 2;
				reverseY();
				pscene.controller.playSound("beep");
			} else if (getTop() < pscene.getTopBoundary()) {
				this.y = pscene.getTopBoundary() + BALL_SIZE / 2;
				reverseY();
				pscene.controller.playSound("beep");
			}
			
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
	
	@Override
	public void draw(Graphics2D canvas) {
		canvas.setColor(Color.CYAN);
		canvas.fill(this.collisionBox());
		if (GameEngine.isDebugOn()) {
			canvas.draw(getTrajectory());
		}
	}
	
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
	
	public void reverseX() {
		this.actualDx *= -1;
	}
	
	public void reverseY() {
		this.actualDy *= -1;
	}
	
	public void setCoordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getDirection() {
		if (this.actualDx == 0) {
			return 0;
		}
		return this.actualDx / Math.abs(this.actualDx);
	}
	
	public void addSpeed() {
		if (Math.abs(this.actualDx) < 50) {
			this.actualDx += (this.actualDx < 0) ? -1 : 1;
			this.actualDy += (this.actualDy < 0) ? -1 : 1;
		}
	}
	
	public void stick(Player stick) {
		launched = false;
		delta = this.y - stick.getCoordinate().y;
		pscene.starting = stick;
	}
}
