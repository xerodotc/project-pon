/**
 * GameObject.java
 * 
 * A prototype of game object
 */

package projectpon.engine;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public abstract class GameObject {
	protected int x = 0; // x-position
	protected int y = 0; // y-position
	protected int dx = 0; // x-velocity
	protected int dy = 0; // y-velocity
	protected int ddx = 0; // x-acceleration
	protected int ddy = 0; // y-acceleration
	protected int z = 0; // z-order
	protected boolean visible = false; // object visibility
	
	private boolean destroyed = false; // object destroyed status
	
	protected int anchorX = 0; // x-anchor position
	protected int anchorY = 0; // y-anchor position
	
	// object collision rectangle
	protected final Rectangle collisionRect = new Rectangle();
	
	protected GameScene scene = null; // object parent scene
	
	// set default input source to local first
	protected GameInput input = GameInput.localInput;
	
	/**
	 * Game object constructor
	 */
	public GameObject() {
	}
	
	/**
	 * Game object constructor with position
	 * 
	 * @param x			x-coordinate
	 * @param y			y-coordinate
	 */
	public GameObject(int x, int y) {
		this();
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Update the position
	 */
	public final void updateMove() {
		dx += ddx;
		dy += ddy;
		x += dx;
		y += dy;
	}
	
	/**
	 * On-create code to be called by game loop
	 */
	public void eventOnCreate() {
		// to be implemented in subclass
	}
	
	/**
	 * On-destroy code to be called by game loop
	 */
	public void eventOnDestroy() {
		// to be implemented in subclass
	}
	
	/**
	 * Pre-update code to be called by game loop
	 */
	public void eventPreUpdate() {
		// to be implemented in subclass
	}
	
	/**
	 * Post-update code to be called by game loop
	 */
	public void eventPostUpdate() {
		// to be implemented in subclass
	}
	
	/**
	 * Drawing code to be called by game loop
	 * 
	 * @param canvas	Drawing canvas
	 */
	public void draw(Graphics2D canvas) {
		// to be implemented in subclass
	}
	
	/**
	 * Destroy this object
	 */
	public final void destroy() {
		this.destroyed = true;
	}
	
	/**
	 * Set game scene (called by GameScene)
	 */
	public final void setScene(GameScene scene) {
		this.scene = scene;
	}
	
	/**
	 * Set input source
	 */
	public final void setInput(GameInput input) {
		this.input = input;
	}
	
	/**
	 * Get collision box
	 */
	protected final Rectangle collisionBox() {
		return this.collisionBox(0, 0);
	}
	
	/**
	 * Get collision box (with relative position)
	 */
	protected final Rectangle collisionBox(int relX, int relY) {
		Rectangle r = new Rectangle(collisionRect);
		r.setLocation(this.x + relX - this.anchorX, this.y + relY - this.anchorY);
		
		return r;
	}
	
	/**
	 * Is collided with other object?
	 */
	public final boolean isCollided(GameObject other) {
		return this.isCollided(other, 0, 0);
	}
	
	/**
	 * Is collided with other object? (with relative position)
	 */
	public final boolean isCollided(GameObject other, int relX, int relY) {
		return this.collisionBox(relX, relY).intersects(other.collisionBox());
	}
	
	/**
	 * Is this object destroyed
	 */
	public final boolean isDestroyed() {
		return this.destroyed;
	}
	
	/**
	 * Get object x,y-coordinate
	 */
	public final Point getCoordinate() {
		return new Point(this.x, this.y);
	}
	
	/**
	 * Get object z-order
	 */
	public final int getZ() {
		return this.z;
	}
	
	/**
	 * Get object visibility
	 */
	public final boolean isVisible() {
		return this.visible;
	}
	
	/**
	 * Is mouse over?
	 */
	public final boolean isMouseOver() {
		return this.collisionBox().contains(this.input.getMouseCoordinate());
	}
	
	/**
	 * Is mouse over? (detect from any input source)
	 */
	public final boolean isMouseOver(GameInput input) {
		return this.collisionBox().contains(input.getMouseCoordinate());
	}
	
	/**
	 * toString method
	 */
	public String toString() {
		return GameEngine.getElapsedTimeMs() + ": [" +
				this.getClass().getSimpleName() + ": x=" + this.x +
				", y=" + this.y + "]";
	}
}
