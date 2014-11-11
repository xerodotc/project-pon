package projectpon.game.objects.title;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import projectpon.engine.GameFont;
import projectpon.engine.GameObject;

public class TitleLogo extends GameObject {
	private Font[] font = new Font[256];
	
	private static final int SIZE_RANGE_MIN = 96;
	private static final int SIZE_RANGE_MAX = 98;
	
	private int currentSize = SIZE_RANGE_MIN;
	private int delta = 1;
	
	private static final int SIZE_CHANGE_TICKS_INIT = 10;
	private int sizeChangeTicks = SIZE_CHANGE_TICKS_INIT;
	
	public TitleLogo() {
		super();
		
		this.z = -10;
		this.visible = true;
		
		for (int i = SIZE_RANGE_MIN; i <= SIZE_RANGE_MAX; i++) {
			font[i] = GameFont.getFont("advocut").
				deriveFont(Font.ITALIC, i);
		}
	}
	
	@Override
	public void draw(Graphics2D canvas) {
		canvas.setColor(Color.WHITE);
		canvas.setFont(font[currentSize]);
		FontMetrics metrics = canvas.getFontMetrics();
		String text = "PROJECT PON";
		int width = metrics.stringWidth(text);
		int height = metrics.getHeight();
		int center = scene.getWidth() / 2 - width / 2;
		
		canvas.drawString(text, center, 24 + height);
		
		if (sizeChangeTicks <= 0) {
			currentSize += delta;
			if (currentSize <= SIZE_RANGE_MIN || currentSize >= SIZE_RANGE_MAX) {
				delta *= -1;
			}
		}
		sizeChangeTicks--;
		if (sizeChangeTicks < 0) {
			sizeChangeTicks = SIZE_CHANGE_TICKS_INIT;
		}
	}
}
