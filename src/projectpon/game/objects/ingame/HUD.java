/**
 * HUD.java
 * 
 * A class for drawing head-up display
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.ingame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import projectpon.engine.GameEngine;
import projectpon.engine.GameFont;
import projectpon.engine.GameImage;
import projectpon.engine.GameObject;
import projectpon.game.scenes.PongScene;

public class HUD extends GameObject {
	protected PongScene pscene; // the PongScene
	private Font scoreFont = GameFont.getFont("advocut").
			deriveFont(Font.PLAIN, 64); // font for score text
	private Font statusFont = GameFont.getFont("advocut").
			deriveFont(Font.PLAIN, 28); // font for status text
	
	private static final int STATUSES_GAP = 16; // space between each status
	
	/**
	 * Initialize HUD
	 */
	public HUD() {
		this.visible = true;
		this.z = -100; // bring to front
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
	 * Draw the HUD
	 */
	@Override
	public void draw(Graphics2D canvas) {
		int middle = pscene.getWidth() / 2;
		String leftScore = "" + pscene.playerLeft.score;
		String rightScore = "" + pscene.playerRight.score;
		
		canvas.setColor(Color.WHITE);
		canvas.setFont(scoreFont);
		Rectangle2D leftScoreBound = canvas.getFontMetrics().
				getStringBounds(leftScore, canvas);
		Rectangle2D rightScoreBound = canvas.getFontMetrics().
				getStringBounds(rightScore, canvas);
		canvas.drawLine(middle, 8, middle, pscene.getTopBoundary() -
				Boundary.HBORDER_THICKNESS - 8);
		canvas.drawString(leftScore, middle - 16 - Math.round(leftScoreBound.getWidth()),
				pscene.getTopBoundary() - Boundary.HBORDER_THICKNESS - 
				Math.round(leftScoreBound.getHeight()) / 2); // left player's score
		canvas.drawString(rightScore, middle + 16,
				pscene.getTopBoundary() - Boundary.HBORDER_THICKNESS - 
				Math.round(rightScoreBound.getHeight()) / 2); // right player's score
		
		// Draw status
		
		int startingY;
		int startingX;
		
		canvas.setFont(statusFont);
		canvas.setColor(Color.WHITE);
		
		// Draw left player status
		
		startingY = pscene.getBottomBoundary() + Boundary.HBORDER_THICKNESS
				+ STATUSES_GAP;
		startingX = STATUSES_GAP;
		
		/*
		 * Sticky
		 */
		if (pscene.playerLeft.getStatus(Player.STATUS_STICKY)) {
			int msec = GameEngine.Util.updatesToMs(
					pscene.playerLeft.getTimer(Player.STATUS_STICKY));
			int secs = (int) Math.ceil(msec / 1000.0);
			String text = "" + secs;
			Rectangle2D bound = canvas.getFontMetrics().getStringBounds(text, canvas);
			BufferedImage img = GameImage.getImage("item-sticky");
			int deltaNextX = (int) Math.round(bound.getWidth()) + STATUSES_GAP;
			int deltaTextX = 0;
			int deltaTextY = (int) Math.round(bound.getHeight());
			if (img != null) {
				canvas.drawImage(img, null, startingX, startingY);
				deltaNextX = img.getWidth() + STATUSES_GAP;
				deltaTextX += img.getWidth() / 2 - bound.getWidth() / 2;
				deltaTextY += img.getHeight();
			}
			canvas.drawString(text, startingX + deltaTextX, startingY + deltaTextY);
			startingX += deltaNextX;
		}
		
		/*
		 * Blind
		 */
		if (pscene.playerLeft.getStatus(Player.STATUS_BLIND)) {
			int msec = GameEngine.Util.updatesToMs(
					pscene.playerLeft.getTimer(Player.STATUS_BLIND));
			int secs = (int) Math.ceil(msec / 1000.0);
			String text = "" + secs;
			Rectangle2D bound = canvas.getFontMetrics().getStringBounds(text, canvas);
			BufferedImage img = GameImage.getImage("item-blind");
			int deltaNextX = (int) Math.round(bound.getWidth()) + STATUSES_GAP;
			int deltaTextX = 0;
			int deltaTextY = (int) Math.round(bound.getHeight());
			if (img != null) {
				canvas.drawImage(img, null, startingX, startingY);
				deltaNextX = img.getWidth() + STATUSES_GAP;
				deltaTextX += img.getWidth() / 2 - bound.getWidth() / 2;
				deltaTextY += img.getHeight();
			}
			canvas.drawString(text, startingX + deltaTextX, startingY + deltaTextY);
			startingX += deltaNextX;
		}
		
		/*
		 * Invert
		 */
		if (pscene.playerLeft.getStatus(Player.STATUS_INVERT)) {
			int msec = GameEngine.Util.updatesToMs(
					pscene.playerLeft.getTimer(Player.STATUS_INVERT));
			int secs = (int) Math.ceil(msec / 1000.0);
			String text = "" + secs;
			Rectangle2D bound = canvas.getFontMetrics().getStringBounds(text, canvas);
			BufferedImage img = GameImage.getImage("item-invert");
			int deltaNextX = (int) Math.round(bound.getWidth()) + STATUSES_GAP;
			int deltaTextX = 0;
			int deltaTextY = (int) Math.round(bound.getHeight());
			if (img != null) {
				canvas.drawImage(img, null, startingX, startingY);
				deltaNextX = img.getWidth() + STATUSES_GAP;
				deltaTextX += img.getWidth() / 2 - bound.getWidth() / 2;
				deltaTextY += img.getHeight();
			}
			canvas.drawString(text, startingX + deltaTextX, startingY + deltaTextY);
			startingX += deltaNextX;
		}
		
		// Draw right player status
		
		startingY = pscene.getBottomBoundary() + Boundary.HBORDER_THICKNESS
				+ STATUSES_GAP;
		startingX = pscene.getWidth() - STATUSES_GAP;
		
		/*
		 * Invert
		 */
		if (pscene.playerRight.getStatus(Player.STATUS_INVERT)) {
			int msec = GameEngine.Util.updatesToMs(
					pscene.playerRight.getTimer(Player.STATUS_INVERT));
			int secs = (int) Math.ceil(msec / 1000.0);
			String text = "" + secs;
			Rectangle2D bound = canvas.getFontMetrics().getStringBounds(text, canvas);
			BufferedImage img = GameImage.getImage("item-invert");
			int deltaNextX = (int) Math.round(bound.getWidth()) + STATUSES_GAP;
			int deltaTextX = (int) - Math.round(bound.getWidth());
			int deltaTextY = (int) Math.round(bound.getHeight());
			if (img != null) {
				canvas.drawImage(img, null, startingX - img.getWidth(), startingY);
				deltaNextX = img.getWidth() + STATUSES_GAP;
				deltaTextX = - img.getWidth();
				deltaTextX += img.getWidth() / 2 - bound.getWidth() / 2;
				deltaTextY += img.getHeight();
			}
			canvas.drawString(text, startingX + deltaTextX, startingY + deltaTextY);
			startingX -= deltaNextX;
		}
		
		/*
		 * Blind
		 */
		if (pscene.playerRight.getStatus(Player.STATUS_BLIND)) {
			int msec = GameEngine.Util.updatesToMs(
					pscene.playerRight.getTimer(Player.STATUS_BLIND));
			int secs = (int) Math.ceil(msec / 1000.0);
			String text = "" + secs;
			Rectangle2D bound = canvas.getFontMetrics().getStringBounds(text, canvas);
			BufferedImage img = GameImage.getImage("item-blind");
			int deltaNextX = (int) Math.round(bound.getWidth()) + STATUSES_GAP;
			int deltaTextX = (int) - Math.round(bound.getWidth());
			int deltaTextY = (int) Math.round(bound.getHeight());
			if (img != null) {
				canvas.drawImage(img, null, startingX - img.getWidth(), startingY);
				deltaNextX = img.getWidth() + STATUSES_GAP;
				deltaTextX = - img.getWidth();
				deltaTextX += img.getWidth() / 2 - bound.getWidth() / 2;
				deltaTextY += img.getHeight();
			}
			canvas.drawString(text, startingX + deltaTextX, startingY + deltaTextY);
			startingX -= deltaNextX;
		}
		
		/*
		 * Sticky
		 */
		if (pscene.playerRight.getStatus(Player.STATUS_STICKY)) {
			int msec = GameEngine.Util.updatesToMs(
					pscene.playerRight.getTimer(Player.STATUS_STICKY));
			int secs = (int) Math.ceil(msec / 1000.0);
			String text = "" + secs;
			Rectangle2D bound = canvas.getFontMetrics().getStringBounds(text, canvas);
			BufferedImage img = GameImage.getImage("item-sticky");
			int deltaNextX = (int) Math.round(bound.getWidth()) + STATUSES_GAP;
			int deltaTextX = (int) - Math.round(bound.getWidth());
			int deltaTextY = (int) Math.round(bound.getHeight());
			if (img != null) {
				canvas.drawImage(img, null, startingX - img.getWidth(), startingY);
				deltaNextX = img.getWidth() + STATUSES_GAP;
				deltaTextX = - img.getWidth();
				deltaTextX += img.getWidth() / 2 - bound.getWidth() / 2;
				deltaTextY += img.getHeight();
			}
			canvas.drawString(text, startingX + deltaTextX, startingY + deltaTextY);
			startingX -= deltaNextX;
		}
	}
}
