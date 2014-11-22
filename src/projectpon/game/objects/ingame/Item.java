/**
 * Item.java
 * 
 * A class for item objects
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.ingame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import projectpon.engine.GameEngine;
import projectpon.engine.GameImage;
import projectpon.engine.GameObject;
import projectpon.game.scenes.PongScene;

public class Item extends GameObject {
	public static final int ITEMBOX_SIZE = 32; // item box size
	public static final int EXPIRE_TIME = 30; // item expire time in seconds
	
	public static final int ITEM_EXPAND = 0;
	public static final int ITEM_SHRINK = 1;
	public static final int ITEM_WALL = 2;
	public static final int ITEM_STICKY = 3;
	public static final int ITEM_INVERT = 4;
	public static final int ITEM_BLIND = 5; // won't available in 2-player mode
	
	private int type; // this instance item type
	protected PongScene pscene = null; // the PongScene
	private int expireTick; // item expire tick
	
	/**
	 * Initialize the item
	 * 
	 * @param x		x-position
	 * @param y		y-position
	 * @param type	Item type
	 */
	public Item(int x, int y, int type) {
		super(x, y);
		
		this.z = 1;
		this.collisionRect.width = this.collisionRect.height = ITEMBOX_SIZE;
		this.anchorX = this.anchorY = ITEMBOX_SIZE / 2;
		this.type = type;
		this.visible = true;
		expireTick = EXPIRE_TIME * GameEngine.getUpdatesPerSec();
	}
	
	/**
	 * Get item type
	 * 
	 * @return Item type
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Assign PongScene and register the item to PongScene
	 */
	@Override
	public void eventOnCreate() {
		if (scene instanceof PongScene) {
			pscene = (PongScene) scene;
		}
		
		pscene.addItem(this);
	}
	
	/**
	 * Set item visibility (in case of blinding)
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
		if (pscene.controller.isPaused()) {
			return;
		}
		
		setVisibility();
		
		if (expireTick <= 0) {
			this.destroy(); // destroy on item expired
		}
		expireTick--;
	}
	
	/**
	 * Post-update event
	 */
	@Override
	public void eventPostUpdate() {
		if (collisionBox().intersectsLine(pscene.ball.getTrajectory())) {
			pscene.controller.playSound("baap");
			this.destroy(); // if the ball hit the item, destroy itself
		}
		
		/*
		 * Set a status to player, if the ball was hit
		 */
		if (pscene.ball.getDirection() != 0) {
			if (collisionBox().intersectsLine(pscene.ball.getTrajectory())) {
				Player player = null;
				if (pscene.ball.getDirection() > 0) {
					player = pscene.playerLeft;
				} else if (pscene.ball.getDirection() < 0) {
					player = pscene.playerRight;
				}
				switch (type) {
				case Item.ITEM_EXPAND:
					player.expand();
					break;
					
				case Item.ITEM_SHRINK:
					player.shrink();
					break;
					
				case Item.ITEM_WALL:
					player.setStatus(Player.STATUS_WALL);
					break;
					
				case Item.ITEM_STICKY:
					player.setStatus(Player.STATUS_STICKY);
					break;
					
				case Item.ITEM_BLIND:
					player.setStatus(Player.STATUS_BLIND);
					break;
					
				case Item.ITEM_INVERT:
					player.setStatus(Player.STATUS_INVERT);
					break;
				}
			}
		}
	}
	
	/**
	 * Unregister the item from PongScene when destroyed
	 */
	@Override
	public void eventOnDestroy() {
		pscene.removeItem(this);
	}
	
	/**
	 * Draw the item
	 */
	@Override
	public void draw(Graphics2D canvas) {
		String imageName = "";
		BufferedImage image = null;
		
		switch (this.type) {
		case ITEM_EXPAND:
			imageName = "item-expand";
			break;
			
		case ITEM_SHRINK:
			imageName = "item-shrink";
			break;
			
		case ITEM_WALL:
			imageName = "item-wall";
			break;
			
		case ITEM_STICKY:
			imageName = "item-sticky";
			break;
			
		case ITEM_INVERT:
			imageName = "item-invert";
			break;
			
		case ITEM_BLIND:
			imageName = "item-blind";
			break;
		}
		
		image = GameImage.getImage(imageName);
		
		if (image == null) {
			return;
		}
		
		canvas.drawImage(image, null, this.x - this.anchorX, this.y - this.anchorY);
	}
}
