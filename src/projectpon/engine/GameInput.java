package projectpon.engine;

import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JComponent;

public final class GameInput {
	/**
	 * Button input event container definition
	 */
	public static class ButtonsInputEvent {
		public Map<Integer, Boolean> pressed = new HashMap<Integer, Boolean>();
		public Map<Integer, Boolean> released = new HashMap<Integer, Boolean>();
		public Map<Integer, Boolean> down = new HashMap<Integer, Boolean>();
		
		public Queue<Integer> pressedQueue = new ArrayDeque<Integer>();
		public Queue<Integer> releasedQueue = new ArrayDeque<Integer>();
	}
	
	/**
	 * Local player input
	 */
	public static final GameInput localInput = new GameInput();
	
	/**
	 * Network players input
	 */
	public static final Map<Integer, GameInput> networkInputs
			= new HashMap<Integer, GameInput>();
	
	/**
	 * Get all input sources (both local and network)
	 * 
	 * @return All input sources as array list
	 */
	public static List<GameInput> getInputs() {
		ArrayList<GameInput> inputs = new ArrayList<GameInput>();
		inputs.add(localInput);
		inputs.addAll(networkInputs.values());
		
		return inputs;
	}
	
	/**
	 * Update status of all input sources
	 */
	public static void updateAllInputs() {
		List<GameInput> inputs = getInputs();
		for (GameInput input : inputs) {
			input.update();
		}
	}
	
	/**
	 * Clear status of all input sources
	 */
	public static void clearAllInputs() {
		List<GameInput> inputs = getInputs();
		for (GameInput input : inputs) {
			input.clear();
		}
	}
	
	/**
	 * Bind local input to the game window
	 * 
	 * @param comp		The main component
	 */
	public static void bindLocalInput(JComponent comp) {
		/*
		 * Keyboard listener
		 */
		comp.addKeyListener(new KeyAdapter() {
			Map<Integer, Thread> keyReleaseTimer = new HashMap<Integer, Thread>();
			
			@Override
			public void keyReleased(KeyEvent e) {
				/*
				 * To prevent repeated key pressed and key released bug in Linux
				 * We need to use key release timer
				 */
				final int keyCode = e.getKeyCode();
				if (localInput.isKeyDown(keyCode)) {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(Math.round(1000.0 / 
										GameEngine.getUpdatesPerSec()) / 2);
								localInput.setKeyReleased(keyCode);
							} catch (InterruptedException e) {
								return;
							}
						}
					});
					keyReleaseTimer.put(keyCode, t);
					t.start();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				final int keyCode = e.getKeyCode();
				if (!localInput.isKeyDown(keyCode)) {
					localInput.setKeyPressed(keyCode);
				} else {
					Thread t = keyReleaseTimer.get(keyCode);
					if (t != null && t.isAlive()) {
						/*
						 * If key release timer still exists during
						 * key pressed event, assume that nobody able
						 * to press and released repeatedly like this.
						 * Cancel the key release timer.
						 */
						t.interrupt();
					}
				}
			}
		});
		
		/*
		 * Mouse listener
		 */
		comp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				localInput.setMouseReleased(e.getButton());
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				localInput.setMousePressed(e.getButton());
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				localInput.mouseOnScreen = false;
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				localInput.mouseOnScreen = true;
			}
		});
		
		/*
		 * Mouse motion listener
		 */
		comp.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				localInput.setMouseCoordinate(e.getX(), e.getY());
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				localInput.setMouseCoordinate(e.getX(), e.getY());
			}
		});
	}
	
	/**
	 * Fields for storing keyboard event
	 */
	private ButtonsInputEvent keyboard = new ButtonsInputEvent();
	
	/**
	 * Fields for storing mouse buttons event
	 */
	private ButtonsInputEvent mouse = new ButtonsInputEvent();
	
	/**
	 * Fields for storing mouse coordinate
	 */
	private Point mouseCoordinate = new Point();
	private Point mouseCoordinateNext = new Point();
	
	public boolean mouseOnScreen = false; // is mouse on screen flag
	
	/**
	 * Is key pressed? (triggered)
	 * 
	 * @param keyCode		Key code
	 * @return	True if key is pressed
	 */
	public boolean isKeyPressed(int keyCode) {
		Boolean status = keyboard.pressed.get(keyCode);
		if (status == null) {
			return false;
		}
		
		return status;
	}
	
	/**
	 * Is key released?
	 * 
	 * @param keyCode		Key code
	 * @return	True if key is released
	 */
	public boolean isKeyReleased(int keyCode) {
		Boolean status = keyboard.released.get(keyCode);
		if (status == null) {
			return false;
		}
		
		return status;
	}
	
	/**
	 * Is key held?
	 * 
	 * @param keyCode		Key code
	 * @return	True if key is held
	 */
	public boolean isKeyDown(int keyCode) {
		Boolean status = keyboard.down.get(keyCode);
		if (status == null) {
			return false;
		}
		
		return status;
	}
	
	/**
	 * Is mouse button pressed? (clicked)
	 * 
	 * @param btnCode		Button code
	 * @return	True if mouse button is pressed (clicked)
	 */
	public boolean isMousePressed(int btnCode) {
		Boolean status = mouse.pressed.get(btnCode);
		if (status == null) {
			return false;
		}
		
		return status;
	}
	
	/**
	 * Is mouse button released?
	 * 
	 * @param btnCode		Button code
	 * @return	True if mouse button is released
	 */
	public boolean isMouseReleased(int btnCode) {
		Boolean status = mouse.released.get(btnCode);
		if (status == null) {
			return false;
		}
		
		return status;
	}
	
	/**
	 * Is mouse button held?
	 * 
	 * @param btnCode		Button code
	 * @return	True if mouse button is held
	 */
	public boolean isMouseDown(int btnCode) {
		Boolean status = mouse.down.get(btnCode);
		if (status == null) {
			return false;
		}
		
		return status;
	}
	
	/**
	 * Is mouse on screen?
	 * 
	 * @return	True if cursor is on screen
	 */
	public boolean isMouseOnScreen() {
		return mouseOnScreen;
	}
	
	/**
	 * Get mouse coordinate as Point
	 * 
	 * @return	Mouse coordinate as Point
	 */
	public Point getMouseCoordinate() {
		return mouseCoordinate;
	}
	
	/**
	 * Get mouse x-coordinate
	 * 
	 * @return	Mouse x-coordinate
	 */
	public int getMouseX() {
		return mouseCoordinate.x;
	}
	
	/**
	 * Get mouse y-coordinate
	 * 
	 * @return	Mouse y-coordinate
	 */
	public int getMouseY() {
		return mouseCoordinate.y;
	}
	
	/**
	 * Set key pressed
	 * 
	 * @param keyCode	Key code
	 */
	public void setKeyPressed(int keyCode) {
		if (GameEngine.paused() || this.isKeyDown(keyCode)) {
			return;
		}
		if (GameEngine.isDebugOn()) {
			System.out.println("keyPressed: " + keyCode);
		}
		keyboard.pressedQueue.add(keyCode);
	}
	
	/**
	 * Set key released
	 * 
	 * @param keyCode	Key code
	 */
	public void setKeyReleased(int keyCode) {
		if (GameEngine.paused() || !this.isKeyDown(keyCode)) {
			return;
		}
		if (GameEngine.isDebugOn()) {
			System.out.println("keyReleased: " + keyCode);
		}
		keyboard.releasedQueue.add(keyCode);
	}
	
	/**
	 * Set mouse pressed
	 * 
	 * @param btnCode 	Button code
	 */
	public void setMousePressed(int btnCode) {
		if (GameEngine.paused() || this.isMouseDown(btnCode)) {
			return;
		}
		if (GameEngine.isDebugOn()) {
			System.out.println("mousePressed: " + btnCode);
		}
		mouse.pressedQueue.add(btnCode);
	}
	
	/**
	 * Set mouse released
	 * 
	 * @param btnCode 	Button code
	 */
	public void setMouseReleased(int btnCode) {
		if (GameEngine.paused() || !this.isMouseDown(btnCode)) {
			return;
		}
		if (GameEngine.isDebugOn()) {
			System.out.println("mouseReleased: " + btnCode);
		}
		mouse.releasedQueue.add(btnCode);
	}
	
	/**
	 * Set mouse coordinate
	 * 
	 * @param x		x-coordinate
	 * @param y		y-coordinate
	 */
	public void setMouseCoordinate(int x, int y) {
		if (mouseOnScreen) {
			mouseCoordinateNext.x = x;
			mouseCoordinateNext.y = y;
			if (GameEngine.isDebugOn()) {
				System.out.println("mouseCoordinate: " + x + " " + y);
			}
		}
	}
	
	/**
	 * Update inputs state
	 */
	private void update() {
		/*
		 * Clear pressed and released event
		 */
		keyboard.pressed.clear();
		keyboard.released.clear();
		mouse.pressed.clear();
		mouse.released.clear();
		
		/*
		 * Process pressed and released queue
		 */
		while (!keyboard.pressedQueue.isEmpty()) {
			int keyCode = keyboard.pressedQueue.remove();
			keyboard.pressed.put(keyCode, true);
			keyboard.down.put(keyCode, true);
		}
		while (!keyboard.releasedQueue.isEmpty()) {
			int keyCode = keyboard.releasedQueue.remove();
			keyboard.released.put(keyCode, true);
			keyboard.down.put(keyCode, false);
		}
		while (!mouse.pressedQueue.isEmpty()) {
			int btnCode = mouse.pressedQueue.remove();
			mouse.pressed.put(btnCode, true);
			mouse.down.put(btnCode, true);
		}
		while (!mouse.releasedQueue.isEmpty()) {
			int btnCode = mouse.releasedQueue.remove();
			mouse.released.put(btnCode, true);
			mouse.down.put(btnCode, false);
		}
		
		/*
		 * Set mouse coordinate
		 */
		mouseCoordinate.x = mouseCoordinateNext.x;
		mouseCoordinate.y = mouseCoordinateNext.y;
	}
	
	/**
	 * Clear inputs state
	 */
	private void clear() {
		Set< Entry<Integer, Boolean> > keyboardDownList = keyboard.down.entrySet();
		Set< Entry<Integer, Boolean> > mouseDownList = mouse.down.entrySet();
		
		/*
		 * For each holding buttons
		 * Queue for release events
		 */
		for (Entry<Integer, Boolean> e : keyboardDownList) {
			if (e.getValue()) {
				keyboard.releasedQueue.add(e.getKey());
			}
		}
		for (Entry<Integer, Boolean> e : mouseDownList) {
			if (e.getValue()) {
				mouse.releasedQueue.add(e.getKey());
			}
		}
		
		if (GameEngine.isDebugOn()) {
			System.out.println("inputCleared");
		}
	}
}
