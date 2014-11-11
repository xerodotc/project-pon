package projectpon.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Queue;

import javax.swing.JComponent;

import projectpon.engine.exceptions.*;

public abstract class GameScene extends JComponent {
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -4005456185202915012L;
	
	/**
	 * Game scene objects list
	 */
	private ArrayList<GameObject> objects;
	
	/**
	 * Game scene new objects queue
	 */
	private Queue<GameObject> newObjects;
	
	/**
	 * Game scene constructor
	 * 
	 * @throws InvalidWindowSize	Thrown when window size is invalid or not set
	 */
	public GameScene() throws InvalidWindowSize {
		this(GameEngine.windowWidth, GameEngine.windowHeight);
	}
	
	/**
	 * Game scene constructor
	 * 
	 * @param	width				Window width
	 * @param	height				Window height
	 * @throws	InvalidWindowSize	Thrown when window size is invalid or not set
	 */
	public GameScene(int width, int height) throws InvalidWindowSize {
		if (width < 1 || height < 1) {
			throw new InvalidWindowSize();
		}
		GameEngine.windowWidth = width;
		GameEngine.windowHeight = height;
		this.setPreferredSize(new Dimension(width, height));
		this.validate();
		this.setDoubleBuffered(true);
		this.setOpaque(true);
		objects = new ArrayList<GameObject>();
		newObjects = new ArrayDeque<GameObject>();
	}
	
	/**
	 * Abstract method for initializing stuff
	 * (such as add objects)
	 */
	public abstract void initialize();
	
	/**
	 * Called on scene exit
	 */
	public void exit() {
		// to be implemented in subclasses
	}
	
	/**
	 * Add object to scene
	 * 
	 * @param object	Game object
	 */
	public final void objectAdd(GameObject object) {
		object.setScene(this);
		newObjects.add(object);
	}
	
	/**
	 * Update each objects state
	 */
	public final void objectsUpdate() {
		/*
		 * Add new objects
		 */
		while (!newObjects.isEmpty()) {
			GameObject object = newObjects.remove();
			objects.add(object);
			object.eventOnCreate();
		}
		
		/*
		 * Pre-update event
		 */
		for (GameObject object : objects) {
			object.eventPreUpdate();
		}
		
		/*
		 * Move the object
		 */
		for (GameObject object : objects) {
			object.updateMove();
		}
		
		/*
		 * Post-update event
		 */
		for (GameObject object : objects) {
			object.eventPostUpdate();
		}
		
		/*
		 * Destroy flagged objects
		 */
		for (Iterator<GameObject> iterator = objects.iterator(); iterator.hasNext();) {
			GameObject object = iterator.next();
			if (object.isDestroyed()) {
				iterator.remove();
				object.eventOnDestroy();
			}
		}
	}
	
	@Override
	public final void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D canvas = (Graphics2D) g;
		
		@SuppressWarnings("unchecked")
		ArrayList<GameObject> objectsByZOrder =
				(ArrayList<GameObject>) objects.clone();
		
		/*
		 * Sort objects by z-order and move invisible objects to the end of list
		 */
		Collections.sort(objectsByZOrder, new Comparator<GameObject>() {
			@Override
			public int compare(GameObject o1, GameObject o2) {
				if (!o1.visible && !o2.visible) {
					return 0;
				} else if (!o1.visible) {
					// o2 should come first
					return 1;
				} else if (!o2.visible) {
					// o1 should come first
					return -1;
				}
				
				if (o1.z > o2.z) {
					return -1;
				} else if (o2.z > o1.z) {
					return 1;
				}
				return 0;
			}
		});
		
		/*
		 * Clear screen
		 */
		canvas.setBackground(GameEngine.DEFAULT_BACKGROUND_COLOR);
		canvas.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		/*
		 * Render each object
		 */
		for (GameObject object : objectsByZOrder) {
			if (!object.visible) {
				// objects after this shouldn't be visible
				// because of sorting algorithm above
				// (except in case of debug)
				if (!GameEngine.isDebugOn()) {
					break;
				}
			}
			
			/*
			 * Perform draw event
			 */
			if (object.visible) {
				object.draw(canvas);
			}
			
			/*
			 * Draw collision box for debug mode
			 */
			if (GameEngine.isDebugOn()) {
				Color oldColor = canvas.getColor();
				canvas.setColor(Color.RED);
				canvas.draw(object.collisionBox());
				canvas.setColor(oldColor);
			}
		}
	}
}
