/**
 * ShadowItems.java
 * 
 * A class for drawing on field items
 * 
 * @author Visatouch Deeying [5631083121]
 */

package projectpon.game.objects.ingame.shadow;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import projectpon.engine.GameImage;
import projectpon.engine.GameObject;
import projectpon.game.objects.ingame.Item;
import projectpon.game.objects.ingame.Player;
import projectpon.game.scenes.ShadowPongScene;

public class ShadowItems extends GameObject {
	private ShadowPongScene spscene = null; // the ShadowPongScene
	
	/**
	 * Initialize
	 */
	public ShadowItems() {
		super();
		
		this.z = 1;
		this.visible = true;
	}
	
	/**
	 * Assign ShadowPongScene
	 */
	@Override
	public void eventOnCreate() {
		if (scene instanceof ShadowPongScene) {
			spscene = (ShadowPongScene) scene;
		}
	}
	
	/**
	 * Draw all on-field items
	 */
	@Override
	public void draw(Graphics2D canvas) {
		boolean blind = false;
		
		if (spscene.myPlayer != null) {
			if (spscene.myPlayer.getStatus(Player.STATUS_BLIND)) {
				blind = true;
			}
		}
		
		List< Map<String,Integer> > shadowItems = spscene.getShadowItems();
		
		if (shadowItems == null) {
			return;
		}
		
		for (Map<String,Integer> item : shadowItems) {
			if (blind) {
				if (new Point(item.get("x"), item.get("y")).
						distance(spscene.myPlayer.getCoordinate()) > spscene.myPlayer.getSize()) {
					continue;
				}
			}
			
			String imageName = "";
			BufferedImage image = null;
			
			switch (item.get("type")) {
			case Item.ITEM_EXPAND:
				imageName = "item-expand";
				break;
				
			case Item.ITEM_SHRINK:
				imageName = "item-shrink";
				break;
				
			case Item.ITEM_WALL:
				imageName = "item-wall";
				break;
				
			case Item.ITEM_STICKY:
				imageName = "item-sticky";
				break;
				
			case Item.ITEM_INVERT:
				imageName = "item-invert";
				break;
				
			case Item.ITEM_BLIND:
				imageName = "item-blind";
				break;
			}
			
			image = GameImage.getImage(imageName);
			
			if (image == null) {
				continue;
			}
			
			canvas.drawImage(image, null, item.get("x") - Item.ITEMBOX_SIZE / 2, 
					item.get("y") - Item.ITEMBOX_SIZE / 2);
		}
	}
}
