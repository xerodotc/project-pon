package projectpon.game.objects.title;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import projectpon.engine.GameEngine;
import projectpon.engine.GameObject;

public class TitleParticles extends GameObject {
	private static class Line {
		public Line2D.Double l;
		public double dx;
		
		public Line(Line2D.Double l, double dx) {
			this.l = l;
			this.dx = dx;
		}
	}
	
	private HashSet<Line> lines = new HashSet<Line>();
	private Random rand = new Random();
	private static final int LINES_GEN_INIT_TICKS = 2;
	private int linesGenTick;
	
	public TitleParticles() {
		super();
		
		this.z = 100;
		this.visible = true;
		linesGenTick = LINES_GEN_INIT_TICKS;
	}
	
	@Override
	public void eventPreUpdate() {
		if (linesGenTick <= 0) {
			int generate = rand.nextInt(16);
			for (int i = 0; i < generate; i++) {
				double length = rand.nextInt(32) + 32;
				double yPos = rand.nextInt(scene.getHeight() - 1) + 1;
				double speed = rand.nextDouble() * 10 + 10;
				lines.add(new Line(new Line2D.Double(scene.getWidth(), yPos,
						scene.getWidth() + length, yPos), speed));
			}
		}
		linesGenTick--;
		if (linesGenTick < 0) {
			linesGenTick = LINES_GEN_INIT_TICKS;
		}
		
		for (Iterator<Line> iterator = lines.iterator(); iterator.hasNext();) {
			Line line = iterator.next();
			line.l.x1 -= line.dx;
			line.l.x2 -= line.dx;
			if (line.l.x2 < 0) {
				iterator.remove();
			}
		}
		
		if (GameEngine.isDebugOn()) {
			//System.out.println("Total line particles on screen: " + lines.size());
		}
	}
	
	@Override
	public void draw(Graphics2D canvas) {
		canvas.setColor(Color.DARK_GRAY);
		HashSet<Line> linesDraw = (HashSet<Line>) lines.clone();
		for (Line line : linesDraw) {
			canvas.draw(line.l);
		}
	}
}
