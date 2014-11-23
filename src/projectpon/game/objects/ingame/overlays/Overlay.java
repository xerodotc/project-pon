/**
 * Overlay.java
 * 
 * An abstract class for every ingame overlay
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.ingame.overlays;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import projectpon.engine.GameObject;
import projectpon.game.scenes.PongScene;

public class Overlay extends GameObject {
	protected int width, height; // overlay size
	protected PongScene pscene; // the PongScene
	protected int centerPointX, centerPointY; // overlay center point
	protected int topBoundX, topBoundY; // overlay top bound
	
	public static final int BORDER = 8; // border for overlay
	
	/*
	 * Define vertical alignment
	 */
	protected static final int VALIGN_TOP = 2;
	protected static final int VALIGN_MIDDLE = 1;
	protected static final int VALIGN_BOTTOM = 0;
	
	/**
	 * Setup overlay
	 * 
	 * @param width		Overlay width
	 * @param height	Overlay height
	 */
	public Overlay(int width, int height) {
		super();
		
		this.visible = true;
		this.z = -200;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Assign PongScene and determine top bound and center point
	 */
	@Override
	public void eventOnCreate() {
		if (scene instanceof PongScene) {
			pscene = (PongScene) scene;
		}
		
		centerPointX = scene.getWidth() / 2;
		centerPointY = scene.getHeight() / 2;
		topBoundX = centerPointX - width / 2;
		topBoundY = centerPointY - height / 2;
	}
	
	/**
	 * Method for drawing horizontal centered string
	 * 
	 * @param canvas	Graphics canvas
	 * @param font		Font
	 * @param text		String
	 * @param y			y-position
	 * @param valign	Vertical alignment
	 * @return	A rectangle bound for the text
	 */
	protected Rectangle drawCenteredString(Graphics2D canvas, Font font,
			String text, int y, int valign) {
		canvas.setFont(font);
		FontMetrics metric = canvas.getFontMetrics();
		Rectangle2D bound = metric.getStringBounds(text, canvas);
		int boundWidth = (int) Math.round(bound.getWidth());
		int boundHeight = (int) Math.round(bound.getHeight());
		int dX = centerPointX - boundWidth / 2;
		int dY = y + topBoundY + boundHeight / 2 * valign - metric.getDescent();
		canvas.drawString(text, dX, dY);
		
		return new Rectangle(dX, dY - boundHeight + metric.getLeading(), boundWidth, boundHeight);
	}
	
	/**
	 * Draw the overlay background
	 */
	@Override
	public void draw(Graphics2D canvas) {
		// draw border
		canvas.setColor(Color.GRAY);
		canvas.fillRect(centerPointX - width / 2 - BORDER,
				centerPointY - height / 2 - BORDER,
				width + BORDER * 2, height + BORDER * 2);
		
		// draw background
		canvas.setColor(Color.WHITE);
		canvas.fillRect(centerPointX - width / 2,
				centerPointY - height / 2,
				width, height);
		canvas.setColor(Color.BLACK);
	}
}
