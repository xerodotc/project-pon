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
	protected int width, height;
	protected PongScene pscene;
	protected int centerPointX, centerPointY;
	protected int topBoundX, topBoundY;
	
	public static final int BORDER = 8;
	
	protected static final int VALIGN_TOP = 2;
	protected static final int VALIGN_MIDDLE = 1;
	protected static final int VALIGN_BOTTOM = 0;
	
	public Overlay(int width, int height) {
		super();
		
		this.visible = true;
		this.z = -200;
		this.width = width;
		this.height = height;
	}
	
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
	
	@Override
	public void draw(Graphics2D canvas) {
		canvas.setColor(Color.GRAY);
		canvas.fillRect(centerPointX - width / 2 - BORDER,
				centerPointY - height / 2 - BORDER,
				width + BORDER * 2, height + BORDER * 2);
		canvas.setColor(Color.WHITE);
		canvas.fillRect(centerPointX - width / 2,
				centerPointY - height / 2,
				width, height);
		canvas.setColor(Color.BLACK);
	}
}
