package projectpon.game.objects.ingame;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import projectpon.engine.GameEngine;
import projectpon.engine.GameImage;
import projectpon.engine.GameObject;
import projectpon.game.scenes.PongScene;

public class Item extends GameObject {
	public static final int ITEMBOX_SIZE = 32;
	public static final int EXPIRE_TIME = 30;
	
	public static final int ITEM_EXPAND = 0;
	public static final int ITEM_SHRINK = 1;
	public static final int ITEM_WALL = 2;
	public static final int ITEM_STICKY = 3;
	public static final int ITEM_INVERT = 4;
	public static final int ITEM_BLIND = 5; // won't available in 2-player mode
	
	private int type;
	protected PongScene pscene = null;
	private int expireTick;
	
	public Item(int x, int y, int type) {
		super(x, y);
		
		this.z = 1;
		this.collisionRect.width = this.collisionRect.height = ITEMBOX_SIZE;
		this.anchorX = this.anchorY = ITEMBOX_SIZE / 2;
		this.type = type;
		this.visible = true;
		expireTick = EXPIRE_TIME * GameEngine.getUpdatesPerSec();
	}
	
	public int getType() {
		return type;
	}
	
	@Override
	public void eventOnCreate() {
		if (scene instanceof PongScene) {
			pscene = (PongScene) scene;
		}
		
		pscene.addItem(this);
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
		if (pscene.controller.isPaused()) {
			return;
		}
		
		setVisibility();
		
		if (expireTick <= 0) {
			this.destroy();
		}
		expireTick--;
	}
	
	@Override
	public void eventPostUpdate() {
		if (this.isCollided(pscene.ball)) {
			pscene.controller.playSound("baap");
			this.destroy();
		}
	}
	
	@Override
	public void eventOnDestroy() {
		pscene.removeItem(this);
	}
	
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
